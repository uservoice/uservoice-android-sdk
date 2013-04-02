package com.uservoice.uservoicesdk.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.uservoice.uservoicesdk.rest.Callback;
import com.uservoice.uservoicesdk.rest.RestTaskCallback;

import static com.uservoice.uservoicesdk.rest.RestMethod.GET;
import com.uservoice.uservoicesdk.rest.RestTask;

public class RequestToken extends BaseModel {
	
	private String key;
	private String secret;
	
	public static void getRequestToken(final Callback<RequestToken> callback) {
		new RestTask(GET, apiPath("/oauth/request_token.json"), null, new RestTaskCallback(callback) {
			
			@Override
			public void onComplete(JSONObject result) {
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
