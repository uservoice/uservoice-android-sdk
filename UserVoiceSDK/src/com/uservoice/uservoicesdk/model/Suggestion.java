package com.uservoice.uservoicesdk.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Suggestion extends BaseModel {
	
	private String title;
	private String text;
	private String status;
	
	@Override
	public void load(JSONObject object) throws JSONException {
		super.load(object);
		title = stringOrNull(object, "title");
		text = stringOrNull(object, "text");
		status = stringOrNull(object, "status");
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getText() {
		return text;
	}
	
	public String getStatus() {
		return status;
	}
	
}
