package com.uservoice.uservoicesdk.model;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.uservoice.uservoicesdk.Session;
import com.uservoice.uservoicesdk.rest.Callback;
import com.uservoice.uservoicesdk.rest.RestTaskCallback;

public class AccessToken extends BaseModel {

    private String key;
    private String secret;

    public static void authorize(String email, String password, final Callback<AccessToken> callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("email", email);
        params.put("password", password);
        params.put("request_token", Session.getInstance().getRequestToken().getKey());
        doPost(apiPath("/oauth/authorize.json"), params, new RestTaskCallback(callback) {
            @Override
            public void onComplete(JSONObject result) throws JSONException {
                callback.onModel(deserializeObject(result, "token", AccessToken.class));
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

    public void save(JSONObject object) throws JSONException {
        object.put("oauth_token", key);
        object.put("oauth_token_secret", secret);
    }
}
