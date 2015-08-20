package com.uservoice.uservoicesdk.model;

import android.content.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.uservoice.uservoicesdk.babayaga.Babayaga;
import com.uservoice.uservoicesdk.rest.Callback;
import com.uservoice.uservoicesdk.rest.RestTaskCallback;

public class Comment extends BaseModel {
    private String text;
    private String userName;
    private String avatarUrl;
    private Date createdAt;

    public static void loadComments(Context context, Suggestion suggestion, int page, final Callback<List<Comment>> callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("page", String.valueOf(page));
        doGet(context, apiPath("/forums/%d/suggestions/%d/comments.json", suggestion.getForumId(), suggestion.getId()), params, new RestTaskCallback(callback) {
            @Override
            public void onComplete(JSONObject object) throws JSONException {
                callback.onModel(deserializeList(object, "comments", Comment.class));
            }
        });
    }

    public static void createComment(final Context context, final Suggestion suggestion, String text, final Callback<Comment> callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("comment[text]", text);
        doPost(context, apiPath("/forums/%d/suggestions/%d/comments.json", suggestion.getForumId(), suggestion.getId()), params, new RestTaskCallback(callback) {
            @Override
            public void onComplete(JSONObject object) throws JSONException {
                Babayaga.track(context, Babayaga.Event.COMMENT_IDEA, suggestion.getId());
                callback.onModel(deserializeObject(object, "comment", Comment.class));
            }
        });
    }

    @Override
    public void load(JSONObject object) throws JSONException {
        super.load(object);
        text = getString(object, "formatted_text");
        JSONObject user = object.getJSONObject("creator");
        userName = getString(user, "name");
        avatarUrl = getString(user, "avatar_url");
        createdAt = getDate(object, "created_at");
    }

    public String getText() {
        return text;
    }

    public String getUserName() {
        return userName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public Date getCreatedAt() {
        return createdAt;
    }
}
