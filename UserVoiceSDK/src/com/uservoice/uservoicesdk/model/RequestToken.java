package com.uservoice.uservoicesdk.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.uservoice.uservoicesdk.rest.Callback;
import com.uservoice.uservoicesdk.rest.RestTaskCallback;

public class RequestToken extends BaseModel {
	
	private String key;
	private String secret;
	
	public static void getRequestToken(final Callback<RequestToken> callback) {
		doGet(apiPath("/oauth/request_token.json"), new RestTaskCallback(callback) {
			@Override
			public void onComplete(JSONObject result) throws JSONException {
				callback.onModel(deserializeObject(result, "token", RequestToken.class));
			}
		});
	}
	
	@Override
	public void load(JSONObject object) throws JSONException {
		key = object.getString("oauth_token");
		secret = object.getString("oauth_token_secret");
	}
	
	public String getKey() {
		return key;
	}
	
	public String getSecret() {
		return secret;
	}
}
