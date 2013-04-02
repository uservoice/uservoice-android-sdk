package com.uservoice.uservoicesdk.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.uservoice.uservoicesdk.rest.Callback;
import com.uservoice.uservoicesdk.rest.RestTaskCallback;

public class Article extends BaseModel {
	
	private String question;
	private String answerHtml;
	
	public static void loadAll(final Callback<List<Article>> callback) {
        doGet(apiPath("/articles.json"), new RestTaskCallback(callback) {
            @Override
            public void onComplete(JSONObject result) {
                callback.onModel(deserializeList(result, "articles", Article.class));
            }
        });
	}
	
	public static void loadForTopic(int topicId, final Callback<List<Article>> callback) {
		doGet(apiPath("/topics/%d/articles.json", topicId), new RestTaskCallback(callback) {
			@Override
			public void onComplete(JSONObject result) {
                callback.onModel(deserializeList(result, "articles", Article.class));
			}
		});
	}
	
	public static void loadInstantAnswers(String query, final Callback<List<BaseModel>> callback) {
		Map<String,String> params = new HashMap<String,String>();
		params.put("per_page", "3");
		params.put("forum_id", String.valueOf(getConfig().getForumId()));
		params.put("query", query);
		if (getConfig().getTopicId() != -1) {
			params.put("topic_id", String.valueOf(getConfig().getTopicId()));
		}
		doGet(apiPath("/instant_answers/search.json"), params, new RestTaskCallback(callback) {
			@Override
			public void onComplete(JSONObject result) {
				callback.onModel(deserializeHeterogenousList(result, "instant_answers"));
			}
		});
	}
	
	@Override
	public void load(JSONObject object) throws JSONException {
		super.load(object);
		question = getString(object, "question");
		answerHtml = getString(object, "answer_html");
	}
	
	public String getQuestion() {
		return question;
	}
	
	public String getAnswerHtml() {
		return answerHtml;
	}
}
