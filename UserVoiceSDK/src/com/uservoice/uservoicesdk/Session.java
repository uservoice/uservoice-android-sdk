package com.uservoice.uservoicesdk;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.uservoice.uservoicesdk.babayaga.Babayaga;
import com.uservoice.uservoicesdk.model.AccessToken;
import com.uservoice.uservoicesdk.model.Article;
import com.uservoice.uservoicesdk.model.ClientConfig;
import com.uservoice.uservoicesdk.model.Comment;
import com.uservoice.uservoicesdk.model.CustomField;
import com.uservoice.uservoicesdk.model.Forum;
import com.uservoice.uservoicesdk.model.RequestToken;
import com.uservoice.uservoicesdk.model.Suggestion;
import com.uservoice.uservoicesdk.model.Topic;
import com.uservoice.uservoicesdk.model.User;

public class Session {

    private static Session instance;

    public static Session getInstance() {
        if (instance == null) {
            instance = new Session();
        }
        return instance;
    }

    public static void reset() {
        instance = null;
    }

    private Session() {
    }

    private Context context;
    private Config config;
    private OAuthConsumer oauthConsumer;
    private RequestToken requestToken;
    private AccessToken accessToken;
    private User user;
    private ClientConfig clientConfig;
    private Forum forum;
    private List<Topic> topics;
    private List<Article> articles;
    private Map<String, String> externalIds = new HashMap<String, String>();
    private Suggestion suggestion;
    private Article article;
    private Topic topic;
    private Comment comment;
    private CustomField customField;
    private Runnable signinListener;

    public Context getContext() {
        return context;
    }

    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
        if (config.getEmail() != null) {
            persistIdentity(config.getName(), config.getEmail());
        }
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void persistIdentity(String name, String email) {
        Editor edit = getSharedPreferences().edit();
        edit.putString("user_name", name);
        edit.putString("user_email", email);
        edit.commit();
    }

    public String getName() {
        if (user != null)
            return user.getName();
        return getSharedPreferences().getString("user_name", null);
    }

    public String getEmail() {
        if (user != null)
            return user.getEmail();
        return getSharedPreferences().getString("user_email", null);
    }

    public RequestToken getRequestToken() {
        return requestToken;
    }

    public void setRequestToken(RequestToken requestToken) {
        this.requestToken = requestToken;
    }

    public OAuthConsumer getOAuthConsumer() {
        if (oauthConsumer == null) {
            if (config.getKey() != null)
                oauthConsumer = new CommonsHttpOAuthConsumer(config.getKey(), config.getSecret());
            else if (clientConfig != null)
                oauthConsumer = new CommonsHttpOAuthConsumer(clientConfig.getKey(), clientConfig.getSecret());
        }
        return oauthConsumer;
    }

    public AccessToken getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(Context context, AccessToken accessToken) {
        this.accessToken = accessToken;
        accessToken.persist(getSharedPreferences(), "access_token", "access_token");
        if (signinListener != null)
            signinListener.run();
    }

    public SharedPreferences getSharedPreferences() {
        return context.getSharedPreferences("uv_" + config.getSite().replaceAll("\\W", "_"), 0);
    }

    public void setAccessToken(AccessToken accessToken) {
        this.accessToken = accessToken;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        persistIdentity(user.getName(), user.getEmail());
    }

    public ClientConfig getClientConfig() {
        return clientConfig;
    }

    public void setClientConfig(ClientConfig clientConfig) {
        this.clientConfig = clientConfig;
    }

    public void setExternalId(String scope, String id) {
        externalIds.put(scope, id);
    }

    public Map<String, String> getExternalIds() {
        return externalIds;
    }

    public Forum getForum() {
        return forum;
    }

    public void setForum(Forum forum) {
        this.forum = forum;
    }

    public Suggestion getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(Suggestion suggestion) {
        this.suggestion = suggestion;
    }

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    public void setTopics(List<Topic> topics) {
        this.topics = topics;
    }

    public List<Topic> getTopics() {
        return topics;
    }

    public List<Article> getArticles() {
        return articles;
    }

    public void setArticles(List<Article> articles) {
        this.articles = articles;
    }

    public void setCustomField(CustomField customField) {
        this.customField = customField;
    }

    public CustomField getCustomField() {
        return customField;
    }

    public void setSignInListener(Runnable runnable) {
        signinListener = runnable;
    }
}
