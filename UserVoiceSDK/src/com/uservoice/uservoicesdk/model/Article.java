package com.uservoice.uservoicesdk.model;

import static com.uservoice.uservoicesdk.rest.RestMethod.GET;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.uservoice.uservoicesdk.rest.Callback;
import com.uservoice.uservoicesdk.rest.RestTask;
import com.uservoice.uservoicesdk.rest.RestTaskCallback;

public class Article extends BaseModel {
	
	private String question;
	private String answerHtml;
	
	public static void loadAll(final Callback<List<Article>> callback) {
        new RestTask(GET, apiPath("/articles.json"), null, new RestTaskCallback(callback) {
            @Override
            public void onComplete(JSONObject result) {
                callback.onModel(deserializeList(result, "articles", Article.class));
            }
        }).execute();
	}
	
	public static void loadForTopic(int topicId, final Callback<List<Article>> callback) {
		new RestTask(GET, apiPath("/topics/%i/articles.json", topicId), null, new RestTaskCallback(callback) {
			@Override
			public void onComplete(JSONObject result) {
                callback.onModel(deserializeList(result, "articles", Article.class));
			}
		}).execute();
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
