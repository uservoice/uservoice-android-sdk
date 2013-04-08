package com.uservoice.uservoicesdk;

import java.util.HashMap;
import java.util.Map;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;

import com.uservoice.uservoicesdk.model.AccessToken;
import com.uservoice.uservoicesdk.model.ClientConfig;
import com.uservoice.uservoicesdk.model.Forum;
import com.uservoice.uservoicesdk.model.RequestToken;
import com.uservoice.uservoicesdk.model.Suggestion;
import com.uservoice.uservoicesdk.model.User;

public class Session {
	
	private static Session instance;
	public static Session getInstance() {
		if (instance == null) {
			instance = new Session();
		}
		return instance;
	}
	
	private Session() {}
	
	private Config config;
	private OAuthConsumer oauthConsumer;
	private RequestToken requestToken;
	private AccessToken accessToken;
	private User user;
	private ClientConfig clientConfig;
	private Forum forum;
	private Map<String,String> externalIds = new HashMap<String,String>();
	private Suggestion suggestion;
	
	public Config getConfig() {
		return config;
	}
	
	public void setConfig(Config config) {
		this.config = config;
	}
	
	public RequestToken getRequestToken() {
		return requestToken;
	}
	
	public void setRequestToken(RequestToken requestToken) {
		this.requestToken = requestToken;
	}
	
	public OAuthConsumer getOAuthConsumer() {
		if (oauthConsumer == null) {
			oauthConsumer = new CommonsHttpOAuthConsumer(config.getKey(), config.getSecret());
		}
		return oauthConsumer;
	}
	
	public AccessToken getAccessToken() {
		return accessToken;
	}
	
	public void setAccessToken(AccessToken accessToken) {
		this.accessToken = accessToken;
	}
	
	public User getUser() {
		return user;
	}
	
	public void setUser(User user) {
		this.user = user;
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

}
