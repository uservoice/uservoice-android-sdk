package com.uservoice.uservoicesdk.rest;

import org.json.JSONObject;

public class RestResult {
	
	private Exception exception;
	private JSONObject object;
	private int statusCode;

	public RestResult(int statusCode, JSONObject object) {
		this.statusCode = statusCode;
		this.object = object;
	}
	
	public RestResult(Exception exception) {
		this.exception = exception;
	}
	
	public RestResult(Exception exception, int statusCode, JSONObject object) {
		this.exception = exception;
		this.statusCode = statusCode;
		this.object = object;
	}

	public boolean isError() {
		return exception != null || statusCode > 400;
	}
	
	public JSONObject getObject() {
		return object;
	}
	
	public Exception getException() {
		return exception;
	}
	
	public int getStatusCode() {
		return statusCode;
	}

	public String getMessage() {
		return String.format("%s -- %s", exception == null ? String.valueOf(statusCode) : exception.getMessage(), object);
	}
}
