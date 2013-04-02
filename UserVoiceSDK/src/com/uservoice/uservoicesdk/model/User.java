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
	
	public static void findOrCreate(String email, String name, final Callback<User> callback) {
		Map<String,String> params = new HashMap<String,String>();
		params.put("user[display_name]", name);
		params.put("user[email]", email);
		params.put("request_token", Session.getInstance().getRequestToken().getKey());
		doPost(apiPath("/users.json"), params, new RestTaskCallback(callback) {
			@Override
			public void onComplete(JSONObject result) {
				Session.getInstance().setAccessToken(deserializeObject(result, "token", AccessToken.class));
				callback.onModel(deserializeObject(result, "user", User.class));
			}
		});
	}
	
	@Override
	public void load(JSONObject object) throws JSONException {
		super.load(object);
		name = stringOrNull(object, "name");
		email = stringOrNull(object, "email");
		avatarUrl = stringOrNull(object, "avatar_url");
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
