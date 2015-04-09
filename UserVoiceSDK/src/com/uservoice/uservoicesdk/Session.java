package com.uservoice.uservoicesdk;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.uservoice.uservoicesdk.babayaga.Babayaga;
import com.uservoice.uservoicesdk.flow.SigninManager;
import com.uservoice.uservoicesdk.model.AccessToken;
import com.uservoice.uservoicesdk.model.ClientConfig;
import com.uservoice.uservoicesdk.model.Forum;
import com.uservoice.uservoicesdk.model.RequestToken;
import com.uservoice.uservoicesdk.model.Topic;
import com.uservoice.uservoicesdk.model.User;

public class Session {

    private static Session instance;

    public static synchronized Session getInstance() {
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
    private Map<String, String> externalIds = new HashMap<String, String>();
    private Runnable signinListener;

    public Context getContext() {
        return context;
    }

    public Config getConfig() {
        if (config == null && context != null) {
            config = Config.load(getSharedPreferences(), "config", "config", Config.class);
        }
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
        persistIdentity(config.getName(), config.getEmail());
        config.persist(getSharedPreferences(), "config", "config");
        persistSite();
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void persistIdentity(String name, String email) {
        Editor edit = getSharedPreferences().edit();
        edit.putString("user_name", name);
        if (SigninManager.isValidEmail(email)) {
            edit.putString("user_email", email);
        }
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
            if (getConfig().getKey() != null)
                oauthConsumer = new CommonsHttpOAuthConsumer(getConfig().getKey(), getConfig().getSecret());
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

    protected void persistSite() {
        Editor edit = context.getSharedPreferences("uv_site", 0).edit();
        edit.putString("site", config.getSite());
        edit.commit();
    }

    public SharedPreferences getSharedPreferences() {
        String site;
        if (config != null) {
            site = config.getSite();
        } else {
            site = context.getSharedPreferences("uv_site", 0).getString("site", null);
        }
        return context.getSharedPreferences("uv_" + site.replaceAll("\\W", "_"), 0);
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

    public void setTopics(List<Topic> topics) {
        this.topics = topics;
    }

    public List<Topic> getTopics() {
        return topics;
    }

    public void setSignInListener(Runnable runnable) {
        signinListener = runnable;
    }
}
