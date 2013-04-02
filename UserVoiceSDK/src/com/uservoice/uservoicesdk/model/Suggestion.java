package com.uservoice.uservoicesdk.model;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

public class Suggestion extends BaseModel {
	
	private String title;
	private String text;
	private String status;
	private String statusColor;
	private String creatorName;
	private String adminResponseText;
	private String adminResponseUserName;
	private String adminResponseAvatarUrl;
	private Date adminResponseCreatedAt;
	private Date createdAt;
	private Category category;
	private int numberOfComments;
	private int numberOfVotes;
	private int numberOfVotesByCurrentUser;
	private int numberOfVotesRemainingForCurrentUser;
	private int forumId;
	
	@Override
	public void load(JSONObject object) throws JSONException {
		super.load(object);
		title = getString(object, "title");
		text = getString(object, "text");
		createdAt = getDate(object, "created_at");
		forumId = object.getJSONObject("topic").getJSONObject("forum").getInt("forum_id");
		category = deserializeObject(object, "category", Category.class);
		numberOfComments = object.getInt("comments_count");
		numberOfVotes = object.getInt("votes_count");
		numberOfVotesByCurrentUser = object.getInt("votes_for");
		numberOfVotesRemainingForCurrentUser = object.getInt("votes_remaining");
		JSONObject statusObject = object.getJSONObject("status");
		status = getString(statusObject, "name");
		statusColor = getString(statusObject, "hex_color");
		creatorName = getString(object.getJSONObject("creator"), "name");
		JSONObject response = object.getJSONObject("response");
		adminResponseText = getString(response, "text");
		adminResponseCreatedAt = getDate(response, "created_at");
		JSONObject responseUser = response.getJSONObject("creator");
		adminResponseUserName = getString(responseUser, "name");
		adminResponseAvatarUrl = getString(responseUser, "avatar_url");
	}
	
	public int getForumId() {
		return forumId;
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
