package com.uservoice.uservoicesdk;

import android.content.SharedPreferences;
import android.util.Log;

import com.uservoice.uservoicesdk.model.Attachment;
import com.uservoice.uservoicesdk.model.BaseModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Config extends BaseModel {
    private String site;
    private String key;
    private String secret;
    private String email;
    private String name;
    private String guid;
    private Map<String, String> customFields = new HashMap<String, String>();
    private int topicId = -1;
    private int forumId = -1;
    private boolean showForum = true;
    private boolean showPostIdea = true;
    private boolean showContactUs = true;
    private boolean showKnowledgeBase = true;
    private Map<String, Object> userTraits = new HashMap<String, Object>();
    private List<Attachment> attachmentList = new ArrayList<Attachment>();

    public Config() {}

    public Config(String site) {
        this.site = site;
    }

    public Config(String site, String key, String secret) {
        this.site = site;
        this.key = key;
        this.secret = secret;
    }

    public String getSite() {
        return site;
    }

    public String getKey() {
        return key;
    }

    public String getSecret() {
        return secret;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getGuid() {
        return guid;
    }

    public Map<String, String> getCustomFields() {
        return customFields;
    }

    public void setCustomFields(Map<String, String> customFields) {
        this.customFields = customFields;
    }

    public int getTopicId() {
        return topicId;
    }

    public void setTopicId(int topicId) {
        this.topicId = topicId;
    }

    public int getForumId() {
        if (forumId == -1 && Session.getInstance().getClientConfig() != null)
            return Session.getInstance().getClientConfig().getDefaultForumId();
        return forumId;
    }

    public void setForumId(int forumId) {
        this.forumId = forumId;
    }

    public List<Attachment> getAttachmentList() {
        return attachmentList;
    }

    public boolean shouldShowForum() {
        if (Session.getInstance().getClientConfig() != null && !Session.getInstance().getClientConfig().isFeedbackEnabled())
            return false;
        return showForum;
    }

    public void setShowForum(boolean showForum) {
        this.showForum = showForum;
    }

    public boolean shouldShowPostIdea() {
        if (Session.getInstance().getClientConfig() != null && !Session.getInstance().getClientConfig().isFeedbackEnabled())
            return false;
        return showPostIdea;
    }

    public void setShowPostIdea(boolean showPostIdea) {
        this.showPostIdea = showPostIdea;
    }

    public boolean shouldShowContactUs() {
        if (Session.getInstance().getClientConfig() != null && !Session.getInstance().getClientConfig().isTicketSystemEnabled())
            return false;
        return showContactUs;
    }

    public void setShowContactUs(boolean showContactUs) {
        this.showContactUs = showContactUs;
    }

    public boolean shouldShowKnowledgeBase() {
        if (Session.getInstance().getClientConfig() != null && !Session.getInstance().getClientConfig().isTicketSystemEnabled())
            return false;
        return showKnowledgeBase;
    }

    public void setShowKnowledgeBase(boolean showKnowledgeBase) {
        this.showKnowledgeBase = showKnowledgeBase;
    }

    public void identifyUser(String id, String name, String email) {
        guid = id;
        this.name = name;
        this.email = email;
        putUserTrait("id", id);
        putUserTrait("name", name);
        putUserTrait("email", email);
    }

    public void putUserTrait(String key, String value) {
        userTraits.put(key, value);
    }

    public void putUserTrait(String key, int value) {
        userTraits.put(key, value);
    }

    public void putUserTrait(String key, boolean value) {
        userTraits.put(key, value);
    }

    public void putUserTrait(String key, float value) {
        userTraits.put(key, value);
    }

    public void putUserTrait(String key, Date value) {
        userTraits.put(key, value.getTime() / 1000);
    }

    public void putAccountTrait(String key, String value) {
        putUserTrait("account_" + key, value);
    }

    public void putAccountTrait(String key, int value) {
        putUserTrait("account_" + key, value);
    }

    public void putAccountTrait(String key, boolean value) {
        putUserTrait("account_" + key, value);
    }

    public void putAccountTrait(String key, float value) {
        putUserTrait("account_" + key, value);
    }

    public void putAccountTrait(String key, Date value) {
        putUserTrait("account_" + key, value);
    }

    public Map<String, Object> getUserTraits() {
        return userTraits;
    }

    public void addAttachment(Attachment attachment) {
        if (attachment != null) {
            attachmentList.add(attachment);
        }
    }

    @Override
    public void save(JSONObject object) throws JSONException {
        object.put("site", site);
        object.put("key", key);
        object.put("secret", secret);
        object.put("email", email);
        object.put("name", name);
        object.put("guid", guid);
        object.put("customFields", serializeStringMap(customFields));
        object.put("topicId", topicId);
        object.put("forumId", forumId);
        object.put("showForum", showForum);
        object.put("showPostIdea", showPostIdea);
        object.put("showContactUs", showContactUs);
        object.put("showKnowledgeBase", showKnowledgeBase);
        object.put("userTraits", serializeMap(userTraits));
        object.put("attachmentList", serializeList(attachmentList));
    }

    @Override
    public void load(JSONObject object) throws JSONException {
        site = getString(object, "site");
        key = getString(object, "key");
        secret = getString(object, "secret");
        email = getString(object, "email");
        name = getString(object, "name");
        guid = getString(object, "guid");
        customFields = deserializeStringMap(object.getJSONObject("customFields"));
        topicId = object.getInt("topicId");
        forumId = object.getInt("forumId");
        showForum = object.getBoolean("showForum");
        showPostIdea = object.getBoolean("showPostIdea");
        showContactUs = object.getBoolean("showContactUs");
        showKnowledgeBase = object.getBoolean("showKnowledgeBase");
        userTraits = deserializeMap(object.getJSONObject("userTraits"));
        attachmentList = deserializeList(object, "attachmentList", Attachment.class);
    }
}