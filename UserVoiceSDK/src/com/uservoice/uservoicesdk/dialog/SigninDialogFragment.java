package com.uservoice.uservoicesdk.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.uservoice.uservoicesdk.R;
import com.uservoice.uservoicesdk.Session;
import com.uservoice.uservoicesdk.babayaga.Babayaga;
import com.uservoice.uservoicesdk.model.AccessToken;
import com.uservoice.uservoicesdk.model.AccessTokenResult;
import com.uservoice.uservoicesdk.model.RequestToken;
import com.uservoice.uservoicesdk.model.User;
import com.uservoice.uservoicesdk.rest.Callback;
import com.uservoice.uservoicesdk.rest.RestResult;
import com.uservoice.uservoicesdk.ui.DefaultCallback;
import com.uservoice.uservoicesdk.ui.Utils;

@SuppressLint("ValidFragment")
public class SigninDialogFragment extends DialogFragmentBugfixed {

    private EditText emailField;
    private EditText nameField;
    private EditText passwordField;
    private View passwordFields;
    private Button forgotPassword;
    private String email;
    private String name;
    private Runnable callback;
    private Runnable requestTokenCallback;

    public SigninDialogFragment() {
    }

    public SigninDialogFragment(String email, String name, Runnable callback) {
        this.email = email;
        this.name = name;
        this.callback = callback;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        RequestToken.getRequestToken(new DefaultCallback<RequestToken>(getActivity()) {
            @Override
            public void onModel(RequestToken requestToken) {
                Session.getInstance().setRequestToken(requestToken);
                if (requestTokenCallback != null)
                    requestTokenCallback.run();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if (!Utils.isDarkTheme(getActivity())) {
            builder.setInverseBackgroundForced(true);
        }
        builder.setTitle(R.string.uv_signin_dialog_title);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.uv_signin_layout, null);
        emailField = (EditText) view.findViewById(R.id.uv_signin_email);
        nameField = (EditText) view.findViewById(R.id.uv_signin_name);
        passwordField = (EditText) view.findViewById(R.id.uv_signin_password);
        passwordFields = view.findViewById(R.id.uv_signin_password_fields);
        forgotPassword = (Button) view.findViewById(R.id.uv_signin_forgot_password);

        passwordFields.setVisibility(View.GONE);

        emailField.setText(email);
        nameField.setText(name);
        if (email != null)
            discoverUser();

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendForgotPassword();
            }
        });

        emailField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (v == emailField && hasFocus == false) {
                    discoverUser();
                }
            }
        });
        builder.setView(view);
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.setPositiveButton(R.string.uv_signin_dialog_ok, null);

        // the crap you have to do to have a button that doesn't always close the dialog
        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface di) {
                Button positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        signIn();
                    }
                });
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(emailField, InputMethodManager.SHOW_IMPLICIT);
            }
        });
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        return dialog;
    }

    private void discoverUser() {
        User.discover(emailField.getText().toString(), new Callback<User>() {
            @Override
            public void onModel(User model) {
                passwordFields.setVisibility(View.VISIBLE);
                nameField.setVisibility(View.GONE);
                passwordField.requestFocus();
            }

            @Override
            public void onError(RestResult error) {
                passwordFields.setVisibility(View.GONE);
                nameField.setVisibility(View.VISIBLE);
                nameField.requestFocus();
            }
        });
    }

    private void signIn() {
        final Activity activity = getActivity();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (nameField.getVisibility() == View.VISIBLE) {
                    User.findOrCreate(emailField.getText().toString(), nameField.getText().toString(), new DefaultCallback<AccessTokenResult<User>>(getActivity()) {
                        @Override
                        public void onModel(AccessTokenResult<User> model) {
                            Session.getInstance().setUser(model.getModel());
                            Session.getInstance().setAccessToken(activity, model.getAccessToken());
                            Babayaga.track(Babayaga.Event.AUTHENTICATE);
                            dismiss();
                            callback.run();
                        }
                    });
                } else {
                    AccessToken.authorize(emailField.getText().toString(), passwordField.getText().toString(), new Callback<AccessToken>() {
                        @Override
                        public void onModel(AccessToken accessToken) {
                            Session.getInstance().setAccessToken(activity, accessToken);
                            User.loadCurrentUser(new DefaultCallback<User>(getActivity()) {
                                @Override
                                public void onModel(User model) {
                                    Session.getInstance().setUser(model);
                                    Babayaga.track(Babayaga.Event.AUTHENTICATE);
                                    dismiss();
                                    callback.run();
                                }
                            });
                        }

                        @Override
                        public void onError(RestResult error) {
                            Toast.makeText(activity, R.string.uv_failed_signin_error, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        };
        if (Session.getInstance().getRequestToken() != null) {
            runnable.run();
        } else {
            requestTokenCallback = runnable;
        }
    }

    private void sendForgotPassword() {
        final Activity activity = getActivity();
        User.sendForgotPassword(emailField.getText().toString(), new DefaultCallback<User>(getActivity()) {
            @Override
            public void onModel(User model) {
                Toast.makeText(activity, R.string.uv_msg_forgot_password, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
