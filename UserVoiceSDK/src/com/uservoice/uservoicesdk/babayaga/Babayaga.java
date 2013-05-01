package com.uservoice.uservoicesdk.babayaga;

import java.util.Map;

public class Babayaga {
	
	static String DOMAIN = "by.uservoice.com";
	static String CHANNEL = "d";
	
	private static String uvts;
	private static Map<String,Object> traits;

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
		
		public String getCode() {
			return code;
		}
	}
	
	public static void setUvts(String uvts) {
		Babayaga.uvts = uvts;
	}
	
	public static void setUserTraits(Map<String,Object> traits) {
		Babayaga.traits = traits;
	}
	

	public static void track(Event event, Map<String,Object> eventProps) {
	}
	
	public static void track(String event, Map<String,Object> eventProps) {
		new BabayagaTask(event, uvts, traits, eventProps).execute();
	}

}
