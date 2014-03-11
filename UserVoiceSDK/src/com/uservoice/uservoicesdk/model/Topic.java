package com.uservoice.uservoicesdk.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.uservoice.uservoicesdk.R;
import com.uservoice.uservoicesdk.Session;
import com.uservoice.uservoicesdk.rest.Callback;
import com.uservoice.uservoicesdk.rest.RestTaskCallback;

public class Topic extends BaseModel {
    protected String name;
    private int numberOfArticles;

    public static Topic ALL_ARTICLES = new Topic() {{
        this.name = Session.getInstance().getContext().getString(R.string.uv_all_articles);
    }};

    public static void loadTopics(final Callback<List<Topic>> callback) {
        doGet(apiPath("/topics.json"), new RestTaskCallback(callback) {
            @Override
            public void onComplete(JSONObject object) throws JSONException {
                List<Topic> allTopics = deserializeList(object, "topics", Topic.class);
                List<Topic> topicsWithArticles = new ArrayList<Topic>(allTopics.size());
                for (Topic topic : allTopics) {
                    if (topic.getNumberOfArticles() > 0)
                        topicsWithArticles.add(topic);
                }
                callback.onModel(topicsWithArticles);
            }
        });
    }

    public static void loadTopic(int topicId, final Callback<Topic> callback) {
        doGet(apiPath("/topics/%d.json", topicId), new RestTaskCallback(callback) {
            @Override
            public void onComplete(JSONObject object) throws JSONException {
                callback.onModel(deserializeObject(object, "topic", Topic.class));
            }
        });
    }

    @Override
    public void load(JSONObject object) throws JSONException {
        super.load(object);
        name = getString(object, "name");
        numberOfArticles = object.getInt("article_count");
    }

    public String getName() {
        return name;
    }

    public int getNumberOfArticles() {
        return numberOfArticles;
    }

    @Override
    public String toString() {
        return name;
    }
}
