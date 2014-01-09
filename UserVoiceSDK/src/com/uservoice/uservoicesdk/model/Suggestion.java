package com.uservoice.uservoicesdk.model;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.uservoice.uservoicesdk.babayaga.Babayaga;
import com.uservoice.uservoicesdk.rest.Callback;
import com.uservoice.uservoicesdk.rest.RestTask;
import com.uservoice.uservoicesdk.rest.RestTaskCallback;

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
    private int numberOfSubscribers;
    private int forumId;
    private boolean subscribed;
    private String forumName;
    private int weight;

    public static void loadSuggestions(Forum forum, int page, final Callback<List<Suggestion>> callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("page", String.valueOf(page));
        params.put("per_page", "20");
        params.put("filter", "public");
        params.put("sort", getClientConfig().getSuggestionSort());
        doGet(apiPath("/forums/%d/suggestions.json", forum.getId()), params, new RestTaskCallback(callback) {
            @Override
            public void onComplete(JSONObject object) throws JSONException {
                callback.onModel(deserializeList(object, "suggestions", Suggestion.class));
            }
        });
    }

    public static RestTask searchSuggestions(Forum forum, String query, final Callback<List<Suggestion>> callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("query", query);
        return doGet(apiPath("/forums/%d/suggestions/search.json", forum.getId()), params, new RestTaskCallback(callback) {
            @Override
            public void onComplete(JSONObject object) throws JSONException {
                callback.onModel(deserializeList(object, "suggestions", Suggestion.class));
            }
        });
    }

    public static void createSuggestion(Forum forum, Category category, String title, String text, int numberOfVotes, final Callback<Suggestion> callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("subscribe", "true");
        params.put("suggestion[title]", title);
        params.put("suggestion[text]", text);
        if (category != null)
            params.put("suggestion[category_id]", String.valueOf(category.getId()));
        doPost(apiPath("/forums/%d/suggestions.json", forum.getId()), params, new RestTaskCallback(callback) {
            @Override
            public void onComplete(JSONObject object) throws JSONException {
                callback.onModel(deserializeObject(object, "suggestion", Suggestion.class));
            }
        });
    }

    public void subscribe(final Callback<Suggestion> callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("subscribe", "true");
        doPost(apiPath("/forums/%d/suggestions/%d/watch.json", forumId, id), params, new RestTaskCallback(callback) {
            @Override
            public void onComplete(JSONObject result) throws JSONException {
                Babayaga.track(Babayaga.Event.VOTE_IDEA, getId());
                Babayaga.track(Babayaga.Event.SUBSCRIBE_IDEA, getId());
                load(result.getJSONObject("suggestion"));
                callback.onModel(Suggestion.this);
            }
        });
    }

    public void unsubscribe(final Callback<Suggestion> callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("subscribe", "false");
        doPost(apiPath("/forums/%d/suggestions/%d/watch.json", forumId, id), params, new RestTaskCallback(callback) {
            @Override
            public void onComplete(JSONObject result) throws JSONException {
                load(result.getJSONObject("suggestion"));
                callback.onModel(Suggestion.this);
            }
        });
    }

    @Override
    public void load(JSONObject object) throws JSONException {
        super.load(object);
        title = getString(object, "title");
        text = getString(object, "formatted_text");
        createdAt = getDate(object, "created_at");
        forumId = object.getJSONObject("topic").getJSONObject("forum").getInt("id");
        forumName = object.getJSONObject("topic").getJSONObject("forum").getString("name");
        subscribed = object.has("subscribed") && object.getBoolean("subscribed");
        if (!object.isNull("category"))
            category = deserializeObject(object, "category", Category.class);
        numberOfComments = object.getInt("comments_count");
        numberOfSubscribers = object.getInt("subscriber_count");
        if (!object.isNull("creator"))
            creatorName = getString(object.getJSONObject("creator"), "name");
        if (!object.isNull("status")) {
            JSONObject statusObject = object.getJSONObject("status");
            status = getString(statusObject, "name");
            statusColor = getString(statusObject, "hex_color");
        }
        if (!object.isNull("response")) {
            JSONObject response = object.getJSONObject("response");
            adminResponseText = getString(response, "formatted_text");
            adminResponseCreatedAt = getDate(response, "created_at");
            JSONObject responseUser = response.getJSONObject("creator");
            adminResponseUserName = getString(responseUser, "name");
            adminResponseAvatarUrl = getString(responseUser, "avatar_url");
        }
        if (object.has("normalized_weight")) {
            weight = object.getInt("normalized_weight");
        }
    }

    public String getForumName() {
        return forumName;
    }

    public boolean isSubscribed() {
        return subscribed;
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

    public String getStatusColor() {
        return statusColor;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public String getAdminResponseText() {
        return adminResponseText;
    }

    public String getAdminResponseUserName() {
        return adminResponseUserName;
    }

    public String getAdminResponseAvatarUrl() {
        return adminResponseAvatarUrl;
    }

    public Date getAdminResponseCreatedAt() {
        return adminResponseCreatedAt;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Category getCategory() {
        return category;
    }

    public int getNumberOfComments() {
        return numberOfComments;
    }

    public int getNumberOfSubscribers() {
        return numberOfSubscribers;
    }

    public void commentPosted(Comment comment) {
        numberOfComments += 1;
    }
    
    public int getWeight() {
        return weight;
    }

}
