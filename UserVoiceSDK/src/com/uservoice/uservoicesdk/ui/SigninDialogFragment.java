package com.uservoice.uservoicesdk.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.uservoice.uservoicesdk.R;
import com.uservoice.uservoicesdk.Session;
import com.uservoice.uservoicesdk.model.AccessToken;
import com.uservoice.uservoicesdk.model.RequestToken;
import com.uservoice.uservoicesdk.model.User;
import com.uservoice.uservoicesdk.rest.Callback;
import com.uservoice.uservoicesdk.rest.RestResult;

public class SigninDialogFragment extends DialogFragment {
	
	private EditText emailField;
	private EditText nameField;
	private EditText passwordField;
	private View passwordFields;
	private Button forgotPassword;
	private String email;
	private String name;
	private Runnable callback;
	
	public SigninDialogFragment() {}
	
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
			}
		});
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("Sign in");
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.signin_layout, null);
		emailField = (EditText) view.findViewById(R.id.signin_email);
		nameField = (EditText) view.findViewById(R.id.signin_name);
		passwordField = (EditText) view.findViewById(R.id.signin_password);
		passwordFields = view.findViewById(R.id.signin_password_fields);
		forgotPassword = (Button) view.findViewById(R.id.signin_forgot_password);
		
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
		builder.setNegativeButton("Cancel", null);
		builder.setPositiveButton("Sign in", null);

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
			}
		});
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
		final Callback<User> userCallback = new DefaultCallback<User>(getActivity()) {
			@Override
			public void onModel(User model) {
				Session.getInstance().setUser(model);
				dismiss();
				callback.run();
			}
		};
		if (nameField.getVisibility() == View.VISIBLE) {
			User.findOrCreate(emailField.getText().toString(), nameField.getText().toString(), userCallback);
		} else {
			AccessToken.authorize(emailField.getText().toString(), passwordField.getText().toString(), new Callback<AccessToken>() {
				@Override
				public void onModel(AccessToken accessToken) {
					Session.getInstance().setAccessToken(accessToken);
					User.loadCurrentUser(userCallback);
				}
	
				@Override
				public void onError(RestResult error) {
					Toast.makeText(activity, "Incorrect email or password", Toast.LENGTH_SHORT).show();
				}
			});
		}
	}

	private void sendForgotPassword() {
		final Activity activity = getActivity();
		User.sendForgotPassword(emailField.getText().toString(), new DefaultCallback<User>(getActivity()) {
			@Override
			public void onModel(User model) {
				Toast.makeText(activity, "Forgot password email sent", Toast.LENGTH_SHORT).show();
			}
		});
	}
}
