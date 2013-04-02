package com.uservoice.uservoicesdk.model;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.uservoice.uservoicesdk.rest.Callback;
import com.uservoice.uservoicesdk.rest.RestTaskCallback;

public class Forum extends BaseModel {
	private String name;
	private int numberOfVotesAllowed;
	private int numberOfOpenSuggestions;
	private List<Category> categories;
	
	public static void loadForum(int forumId, final Callback<Forum> callback) {
		doGet(apiPath("/forums/%d.json", forumId), new RestTaskCallback(callback) {
			@Override
			public void onComplete(JSONObject object) {
				callback.onModel(deserializeObject(object, "forum", Forum.class));
			}
		});
	}
	
	@Override
	public void load(JSONObject object) throws JSONException {
		super.load(object);
		name = getString(object, "name");
		numberOfOpenSuggestions = object.getInt("open_suggestion_count");
		numberOfVotesAllowed = object.getInt("votes_allowed");
		categories = deserializeList(object, "categories", Category.class);
	}
	
	public String getName() {
		return name;
	}
	
	public int getNumberOfOpenSuggestions() {
		return numberOfOpenSuggestions;
	}
	
	public int getNumberOfVotesAllowed() {
		return numberOfVotesAllowed;
	}
	
	public List<Category> getCategories() {
		return categories;
	}
}
