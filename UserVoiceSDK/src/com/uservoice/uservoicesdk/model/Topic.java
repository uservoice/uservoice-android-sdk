package com.uservoice.uservoicesdk.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.uservoice.uservoicesdk.R;
import com.uservoice.uservoicesdk.Session;
import com.uservoice.uservoicesdk.rest.Callback;
import com.uservoice.uservoicesdk.rest.RestTaskCallback;

public class Topic extends BaseModel implements Parcelable {

    protected String name;
    private int numberOfArticles;

    public Topic() {}

    public static Topic allArticlesTopic(Context context) {
        Topic allArticles = new Topic();
        allArticles.name = context.getString(R.string.uv_all_articles);
        allArticles.id = -1;
        return allArticles;
    }

    public static void loadTopics(Context context, final Callback<List<Topic>> callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("per_page", "100");
        doGet(context, apiPath("/topics.json"), params, new RestTaskCallback(callback) {
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

    public static void loadTopic(Context context, int topicId, final Callback<Topic> callback) {
        doGet(context, apiPath("/topics/%d.json", topicId), new RestTaskCallback(callback) {
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

    //
    // Parcelable
    //

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(id);
        out.writeString(name);
        out.writeInt(numberOfArticles);
    }

    public static final Parcelable.Creator<Topic> CREATOR = new Parcelable.Creator<Topic>() {
        public Topic createFromParcel(Parcel in) {
            return new Topic(in);
        }

        public Topic[] newArray(int size) {
            return new Topic[size];
        }
    };

    private Topic(Parcel in) {
        id = in.readInt();
        name = in.readString();
        numberOfArticles = in.readInt();
    }
}
