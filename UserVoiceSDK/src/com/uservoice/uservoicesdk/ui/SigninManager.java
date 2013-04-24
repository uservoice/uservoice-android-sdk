package com.uservoice.uservoicesdk.ui;

import android.app.Activity;
import android.app.DialogFragment;

import com.uservoice.uservoicesdk.Session;
import com.uservoice.uservoicesdk.model.RequestToken;
import com.uservoice.uservoicesdk.model.User;
import com.uservoice.uservoicesdk.rest.Callback;
import com.uservoice.uservoicesdk.rest.RestResult;

public class SigninManager {
	
	private final Runnable callback;
	private final String email;
	private final String name;
	private final Activity activity;

	public static void signIn(Activity activity, Runnable callback) {
		new SigninManager(activity, null, null, callback).signIn();
	}
	
	public static void signIn(Activity activity, String email, String name, Runnable callback) {
		new SigninManager(activity, email, name, callback).signIn();
	}
	
	private SigninManager(Activity activity, String email, String name, Runnable callback) {
		this.activity = activity;
		this.email = email;
		this.name = name;
		this.callback = callback;
	}
	
	private void signIn() {
		User currentUser = Session.getInstance().getUser();
		if (currentUser != null && (email == null || email.equals(currentUser.getEmail()))) {
			callback.run();
		} else {
			// TODO if there is a locally stored name & email, use that
			if (email != null) {
				User.discover(email, new Callback<User>() {
					@Override
					public void onModel(User model) {
						promptToSignIn();
					}
					
					@Override
					public void onError(RestResult error) {
						createUser();
					}
				});
			} else {
				promptToSignIn();
			}
		}
	}
	
	private void createUser() {
		RequestToken.getRequestToken(new DefaultCallback<RequestToken>(activity) {
			@Override
			public void onModel(RequestToken model) {
				Session.getInstance().setRequestToken(model);
				User.findOrCreate(email, name, new DefaultCallback<User>(activity) {
					@Override
					public void onModel(User model) {
						Session.getInstance().setUser(model);
						callback.run();
					}
				});
			}
		});
	}
	
	private void promptToSignIn() {
		DialogFragment dialog = new SigninDialogFragment(email, name, callback);
		dialog.show(activity.getFragmentManager(), "SigninDialogFragment");
	}
	
}
