package com.uservoice.uservoicesdk.rest;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class RestTaskCallback {
	
	private final Callback<?> callback;

	public RestTaskCallback(Callback<?> callback) {
		this.callback = callback;
	}
	
	public abstract void onComplete(JSONObject result) throws JSONException;
	
	public void onError(RestResult result) {
		callback.onError(result);
	}
}
