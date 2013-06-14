package com.uservoice.uservoicesdk.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.uservoice.uservoicesdk.rest.Callback;
import com.uservoice.uservoicesdk.rest.RestTask;
import com.uservoice.uservoicesdk.rest.RestTaskCallback;

public class Article extends BaseModel {
	
	private String title;
	private String html;
	private String topicName;
	
	public static void loadAll(final Callback<List<Article>> callback) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("sort", "ordered");
        doGet(apiPath("/articles.json"), params, new RestTaskCallback(callback) {
            @Override
            public void onComplete(JSONObject result) throws JSONException {
                callback.onModel(deserializeList(result, "articles", Article.class));
            }
        });
	}
	
	public static void loadForTopic(int topicId, final Callback<List<Article>> callback) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("sort", "ordered");
		doGet(apiPath("/topics/%d/articles.json", topicId), params, new RestTaskCallback(callback) {
			@Override
			public void onComplete(JSONObject result) throws JSONException {
                callback.onModel(deserializeList(result, "articles", Article.class));
			}
		});
	}
	
	public static RestTask loadInstantAnswers(String query, final Callback<List<BaseModel>> callback) {
		Map<String,String> params = new HashMap<String,String>();
		params.put("per_page", "3");
		params.put("forum_id", String.valueOf(getConfig().getForumId()));
		params.put("query", query);
		if (getConfig().getTopicId() != -1) {
			params.put("topic_id", String.valueOf(getConfig().getTopicId()));
		}
		return doGet(apiPath("/instant_answers/search.json"), params, new RestTaskCallback(callback) {
			@Override
			public void onComplete(JSONObject result) throws JSONException {
				callback.onModel(deserializeHeterogenousList(result, "instant_answers"));
			}
		});
	}
	
	@Override
	public void load(JSONObject object) throws JSONException {
		super.load(object);
		title = getString(object, "question");
		html = getHtml(object, "answer_html");
		if (!object.isNull("topic")) {
			JSONObject topic = object.getJSONObject("topic");
			topicName = topic.getString("name");
		}
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getHtml() {
		return html;
	}
	
	public String getTopicName() {
		return topicName;
	}
}
