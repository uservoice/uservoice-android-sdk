package com.uservoice.uservoicesdk.model;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.uservoice.uservoicesdk.Session;
import com.uservoice.uservoicesdk.rest.Callback;
import com.uservoice.uservoicesdk.rest.RestTaskCallback;

public class User extends BaseModel {
	
	private String avatarUrl;
	private String name;
	private String email;
	
	public static void discover(String email, final Callback<User> callback) {
		Map<String,String> params = new HashMap<String,String>();
		params.put("email", email);
		doGet(apiPath("/users/discover.json"), params, new RestTaskCallback(callback) {
			@Override
			public void onComplete(JSONObject result) throws JSONException {
				callback.onModel(deserializeObject(result, "user", User.class));
			}
		});
	}
	
	public static void loadCurrentUser(final Callback<User> callback) {
		doGet(apiPath("/users/current.json"), new RestTaskCallback(callback) {
			@Override
			public void onComplete(JSONObject object) throws JSONException {
				callback.onModel(deserializeObject(object, "user", User.class));
			}
		});
	}
	
	public static void findOrCreate(String ssoToken, final Callback<User> callback) {
		Map<String,String> params = new HashMap<String,String>();
		params.put("sso", ssoToken);
		params.put("request_token", Session.getInstance().getRequestToken().getKey());
		doPost(apiPath("/users/find_or_create.json"), params, new RestTaskCallback(callback) {
			@Override
			public void onComplete(JSONObject result) throws JSONException {
				Session.getInstance().setAccessToken(deserializeObject(result, "token", AccessToken.class));
				callback.onModel(deserializeObject(result, "user", User.class));
			}
		});
	}
	
	public static void findOrCreate(String email, String name, String guid, final Callback<User> callback) {
		Map<String,String> params = new HashMap<String,String>();
		params.put("user[display_name]", name);
		params.put("user[email]", email);
		params.put("user[guid]", guid);
		params.put("request_token", Session.getInstance().getRequestToken().getKey());
		doPost(apiPath("/users/find_or_create.json"), params, new RestTaskCallback(callback) {
			@Override
			public void onComplete(JSONObject result) throws JSONException {
				Session.getInstance().setAccessToken(deserializeObject(result, "token", AccessToken.class));
				callback.onModel(deserializeObject(result, "user", User.class));
			}
		});
	}
	
	public static void findOrCreate(String email, String name, final Callback<User> callback) {
		Map<String,String> params = new HashMap<String,String>();
		params.put("user[display_name]", name);
		params.put("user[email]", email);
		params.put("request_token", Session.getInstance().getRequestToken().getKey());
		doPost(apiPath("/users.json"), params, new RestTaskCallback(callback) {
			@Override
			public void onComplete(JSONObject result) throws JSONException {
				Session.getInstance().setAccessToken(deserializeObject(result, "token", AccessToken.class));
				callback.onModel(deserializeObject(result, "user", User.class));
			}
		});
	}
	
	public static void sendForgotPassword(String email, final Callback<User> callback) {
		Map<String,String> params = new HashMap<String,String>();
		params.put("user[email]", email);
		doGet(apiPath("/users/forgot_password.json"), params, new RestTaskCallback(callback) {
			@Override
			public void onComplete(JSONObject result) throws JSONException {
				callback.onModel(deserializeObject(result, "user", User.class));
			}
		});
	}
	
	@Override
	public void load(JSONObject object) throws JSONException {
		super.load(object);
		name = getString(object, "name");
		email = getString(object, "email");
		avatarUrl = getString(object, "avatar_url");
		// TODO something about votes remaining
	}
	
	public String getName() {
		return name;
	}
	
	public String getEmail() {
		return email;
	}
	
	public String getAvatarUrl() {
		return avatarUrl;
	}
	
}
