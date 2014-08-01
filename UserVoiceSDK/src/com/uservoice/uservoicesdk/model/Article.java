package com.uservoice.uservoicesdk.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.uservoice.uservoicesdk.rest.Callback;
import com.uservoice.uservoicesdk.rest.RestTask;
import com.uservoice.uservoicesdk.rest.RestTaskCallback;

public class Article extends BaseModel implements Parcelable {

    private String title;
    private String html;
    private String topicName;
    private int weight;

    public Article() {}

    public static void loadPage(int page, final Callback<List<Article>> callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("sort", "ordered");
        params.put("per_page", "50");
        params.put("page", String.valueOf(page));
        doGet(apiPath("/articles.json"), params, new RestTaskCallback(callback) {
            @Override
            public void onComplete(JSONObject result) throws JSONException {
                callback.onModel(deserializeList(result, "articles", Article.class));
            }
        });
    }

    public static void loadPageForTopic(int topicId, int page, final Callback<List<Article>> callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("sort", "ordered");
        params.put("per_page", "50");
        params.put("page", String.valueOf(page));
        doGet(apiPath("/topics/%d/articles.json", topicId), params, new RestTaskCallback(callback) {
            @Override
            public void onComplete(JSONObject result) throws JSONException {
                callback.onModel(deserializeList(result, "articles", Article.class));
            }
        });
    }

    public static RestTask loadInstantAnswers(String query, final Callback<List<BaseModel>> callback) {
        Map<String, String> params = new HashMap<String, String>();
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
        if (object.has("normalized_weight")) {
            weight = object.getInt("normalized_weight");
        }
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

    public int getWeight() {
        return weight;
    }

    //
    // Parcelable
    //

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(id);
        out.writeString(title);
        out.writeString(html);
        out.writeString(topicName);
        out.writeInt(weight);
    }

    public static final Parcelable.Creator<Article> CREATOR = new Parcelable.Creator<Article>() {
        public Article createFromParcel(Parcel in) {
            return new Article(in);
        }

        public Article[] newArray(int size) {
            return new Article[size];
        }
    };

    private Article(Parcel in) {
        id = in.readInt();
        title = in.readString();
        html = in.readString();
        topicName = in.readString();
        weight = in.readInt();
    }
}
