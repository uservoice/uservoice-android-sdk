package com.uservoice.uservoicesdk;

import java.util.Map;

import android.content.Context;
import android.content.Intent;

import com.uservoice.uservoicesdk.activity.UserVoiceActivity;

public class UserVoice {
	
	public static void launchUserVoice(Config config, Context context) {
		Session.getInstance().setConfig(config);
		Intent intent = new Intent(context, UserVoiceActivity.class);
		context.startActivity(intent);
	}
	
	public static void launchForum(Config config, Context context) {
		Session.getInstance().setConfig(config);
		
	}
	
	public static void launchContactUs(Config config, Context context) {
		Session.getInstance().setConfig(config);
		
	}
	
	public static void launchNewIdea(Config config, Context context) {
		Session.getInstance().setConfig(config);
		
	}
	
	public static void setExternalId(String scope, String id) {
		Session.getInstance().setExternalId(scope, id);
	}
	
	public static String getVersion() {
		// TODO get this from the package version maybe
		return "0.0.1";
	}
	
	
	public enum Event {
		VIEW_FORUM("m"),
		VIEW_TOPIC("c"),
		VIEW_KB("k"),
		VIEW_CHANNEL("o"),
		VIEW_IDEA("i"),
		VIEW_ARTICLE("f"),
		AUTHENTICATE("u"),
		SEARCH_IDEAS("s"),
		SEARCH_ARTICLES("r"),
		VOTE_IDEA("v"),
		VOTE_ARTICLE("z"),
		SUBMIT_TICKET("t"),
		SUBMIT_IDEA("d"),
		SUBSCRIBE_IDEA("b"),
		IDENTIFY("x");
		
		private final String code;
		
		private Event(String code) {
			this.code = code;
		}
		
		@Override
		public String toString() {
			return code;
		}
	}
	
	public static void track(Event event, Map<String,Object> props) {
		
	}
	
	public static void track(String event, Map<String,Object> props) {
	}
	
	public static void track(String event) {
		
	}
}
