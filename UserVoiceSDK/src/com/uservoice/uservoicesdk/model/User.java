package com.uservoice.uservoicesdk.model;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.uservoice.uservoicesdk.Session;
import com.uservoice.uservoicesdk.rest.Callback;
import com.uservoice.uservoicesdk.rest.RestTaskCallback;

public class User extends BaseModel {

    private String name;
    private String email;

    public static void discover(Context context, String email, final Callback<User> callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("email", email);
        doGet(context, apiPath("/users/discover.json"), params, new RestTaskCallback(callback) {
            @Override
            public void onComplete(JSONObject result) throws JSONException {
                callback.onModel(deserializeObject(result, "user", User.class));
            }
        });
    }

    public static void loadCurrentUser(Context context, final Callback<User> callback) {
        doGet(context, apiPath("/users/current.json"), new RestTaskCallback(callback) {
            @Override
            public void onComplete(JSONObject object) throws JSONException {
                callback.onModel(deserializeObject(object, "user", User.class));
            }
        });
    }

    public static void findOrCreate(Context context, String email, String name, String guid, final Callback<AccessTokenResult<User>> callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("user[display_name]", name);
        params.put("user[email]", email);
        params.put("user[guid]", guid);
        params.put("request_token", Session.getInstance().getRequestToken().getKey());
        doPost(context, apiPath("/users/find_or_create.json"), params, new RestTaskCallback(callback) {
            @Override
            public void onComplete(JSONObject result) throws JSONException {
                AccessToken accessToken = deserializeObject(result, "token", AccessToken.class);
                User user = deserializeObject(result, "user", User.class);
                callback.onModel(new AccessTokenResult<User>(user, accessToken));
            }
        });
    }

    public static void findOrCreate(Context context, String email, String name, final Callback<AccessTokenResult<User>> callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("user[display_name]", name);
        params.put("user[email]", email);
        params.put("request_token", Session.getInstance().getRequestToken().getKey());
        doPost(context, apiPath("/users.json"), params, new RestTaskCallback(callback) {
            @Override
            public void onComplete(JSONObject result) throws JSONException {
                AccessToken accessToken = deserializeObject(result, "token", AccessToken.class);
                User user = deserializeObject(result, "user", User.class);
                callback.onModel(new AccessTokenResult<User>(user, accessToken));
            }
        });
    }

    public static void sendForgotPassword(Context context, String email, final Callback<User> callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("user[email]", email);
        doGet(context, apiPath("/users/forgot_password.json"), params, new RestTaskCallback(callback) {
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
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}
