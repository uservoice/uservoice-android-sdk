package com.uservoice.uservoicesdk.model;

import org.json.JSONException;
import org.json.JSONObject;

public class AccessToken extends BaseModel {

	private String key;
	private String secret;
	
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
