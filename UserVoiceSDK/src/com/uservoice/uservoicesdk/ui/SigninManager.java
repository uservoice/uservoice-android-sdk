package com.uservoice.uservoicesdk.ui;

import com.uservoice.uservoicesdk.Session;
import com.uservoice.uservoicesdk.model.User;

public class SigninManager {
	
	private final SigninCallback callback;
	private final String email;
	private final String name;

	public static void signIn(SigninCallback callback) {
		new SigninManager(null, null, callback).signIn();
	}
	
	public static void signIn(String email, String name, SigninCallback callback) {
		new SigninManager(email, name, callback).signIn();
	}
	
	private SigninManager(String email, String name, SigninCallback callback) {
		this.email = email;
		this.name = name;
		this.callback = callback;
	}
	
	private void signIn() {
		User currentUser = Session.getInstance().getUser();
		if (currentUser != null && (email == null || email.equals(currentUser.getEmail()))) {
			callback.onSignIn();
		} else {
			// TODO if there is a locally stored name & email, use that
			if (email != null) {
			}
		}
	}
	
	
	
	
}
