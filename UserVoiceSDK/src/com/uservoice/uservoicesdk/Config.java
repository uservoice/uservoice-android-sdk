package com.uservoice.uservoicesdk;

import java.util.Map;

public class Config {
	private String site;
	private String key;
	private String secret;
	private String ssoToken;
	private String email;
	private String name;
	private String guid;
	private Map<String, String> customFields;
	private int topicId;
	private int forumId;
	private boolean showForum = true;
	private boolean showPostIdea = true;
	private boolean showContactUs = true;
	private boolean showKnowledgeBase = true;
	
	public Config(String site, String key, String secret) {
		this.site = site;
		this.key = key;
		this.secret = secret;
	}
	
	public Config(String site, String key, String secret, String ssoToken) {
		this(site, key, secret);
		this.ssoToken = ssoToken;
	}
	
	public Config(String site, String key, String secret, String email, String name, String guid) {
		this(site, key, secret);
		this.email = email;
		this.name = name;
		this.guid = guid;
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
	
	public String getSsoToken() {
		return ssoToken;
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
		return forumId;
	}

	public void setForumId(int forumId) {
		this.forumId = forumId;
	}

	public boolean shouldShowForum() {
		return showForum;
	}

	public void setShowForum(boolean showForum) {
		this.showForum = showForum;
	}

	public boolean shouldShowPostIdea() {
		return showPostIdea;
	}

	public void setShowPostIdea(boolean showPostIdea) {
		this.showPostIdea = showPostIdea;
	}

	public boolean shouldShowContactUs() {
		return showContactUs;
	}

	public void setShowContactUs(boolean showContactUs) {
		this.showContactUs = showContactUs;
	}

	public boolean shouldShowKnowledgeBase() {
		return showKnowledgeBase;
	}

	public void setShowKnowledgeBase(boolean showKnowledgeBase) {
		this.showKnowledgeBase = showKnowledgeBase;
	}
}
