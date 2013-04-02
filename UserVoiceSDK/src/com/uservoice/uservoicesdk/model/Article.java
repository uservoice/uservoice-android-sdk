package com.uservoice.uservoicesdk.model;

import java.util.List;

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
	
	@Override
	public void load(JSONObject object) throws JSONException {
		super.load(object);
		question = stringOrNull(object, "question");
		answerHtml = stringOrNull(object, "answer_html");
	}
	
	public String getQuestion() {
		return question;
	}
	
	public String getAnswerHtml() {
		return answerHtml;
	}
}
