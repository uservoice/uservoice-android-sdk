package com.uservoice.uservoicesdk.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.uservoice.uservoicesdk.R;
import com.uservoice.uservoicesdk.Session;
import com.uservoice.uservoicesdk.model.AccessToken;
import com.uservoice.uservoicesdk.model.RequestToken;
import com.uservoice.uservoicesdk.ui.DefaultCallback;
import com.uservoice.uservoicesdk.ui.Utils;

@SuppressLint("ValidFragment")
public class PasswordDialogFragment extends DialogFragmentBugfixed {

    private final Runnable callback;
    private EditText password;

    public PasswordDialogFragment(Runnable callback) {
        this.callback = callback;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.uv_password_dialog_title);
        if (!Utils.isDarkTheme(getActivity())) {
            builder.setInverseBackgroundForced(true);
        }
        View view = getActivity().getLayoutInflater().inflate(R.layout.uv_password_dialog, null);
        password = (EditText) view.findViewById(R.id.uv_password);
        builder.setView(view);
        builder.setNegativeButton(R.string.uv_cancel, null);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                if (Session.getInstance().getRequestToken() != null) {
                    authorize();
                } else {
                    RequestToken.getRequestToken(new DefaultCallback<RequestToken>(getActivity()) {
                        @Override
                        public void onModel(RequestToken model) {
                            Session.getInstance().setRequestToken(model);
                            authorize();
                        }
                    });
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        return dialog;
    }

    private void authorize() {
        AccessToken.authorize(Session.getInstance().getEmail(), password.getText().toString(), new DefaultCallback<AccessToken>(getActivity()) {
            @Override
            public void onModel(AccessToken model) {
                Session.getInstance().setAccessToken(model);
                callback.run();
            }
        });
    }

}
