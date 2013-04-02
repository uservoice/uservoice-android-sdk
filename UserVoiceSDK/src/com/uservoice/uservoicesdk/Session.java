package com.uservoice.uservoicesdk;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;

import com.uservoice.uservoicesdk.model.RequestToken;

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
}
