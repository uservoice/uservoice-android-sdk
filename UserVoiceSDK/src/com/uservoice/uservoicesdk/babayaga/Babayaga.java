package com.uservoice.uservoicesdk.babayaga;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import android.util.Log;
import com.uservoice.uservoicesdk.Session;
import com.uservoice.uservoicesdk.model.BaseModel;

public class Babayaga {
	
	static String DOMAIN = "by.uservoice.com";
	public static String CHANNEL = "d";
	
	private static class Track {
		public String event;
		public Map<String,Object> eventProps;
		
		public Track(String event, Map<String, Object> eventProps) {
			this.event = event;
			this.eventProps = eventProps;
		}
	}
	
	private static String uvts;
	private static Map<String,Object> traits;
	private static SharedPreferences prefs;
	private static List<Track> queue = new ArrayList<Track>();

	public enum Event {
    VIEW_APP("g"),
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
		IDENTIFY("y"),
		COMMENT_IDEA("h");
		
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
		Editor edit = prefs.edit();
		edit.putString("uvts", uvts);
		edit.commit();
	}
	
	public static void setUserTraits(Map<String,Object> traits) {
		Babayaga.traits = traits;
	}

	public static void track(Event event) {
		track(event, null);
	}
	
	public static void track(Event event, String searchText, List<? extends BaseModel> results) {
		Map<String,Object> props = new HashMap<String,Object>();
		List<Integer> ids = new ArrayList<Integer>(results.size());
		for (BaseModel model : results) {
			ids.add(model.getId());
		}
		props.put("ids", ids);
		props.put("text", searchText);
		track(event, props);
	}
	
	public static void track(Event event, int id) {
		Map<String,Object> props = new HashMap<String,Object>();
		props.put("id", id);
		track(event, props);
	}

	public static void track(Event event, Map<String,Object> eventProps) {
		track(event.getCode(), eventProps);
	}
	
	public static void track(String event, Map<String,Object> eventProps) {
		if (Session.getInstance().getClientConfig() == null) {
			queue.add(new Track(event, eventProps));
		} else {
//            Log.d("UV", "BY flushing: " + event);
			new BabayagaTask(event, uvts, traits, eventProps).execute();
		}
	}
	
	public static void flush() {
		for (Track track : queue) {
			track(track.event, track.eventProps);
		}
		queue = new ArrayList<Track>();
	}

	public static void init(Context context) {
		prefs = context.getSharedPreferences("uv", 0);
		if (prefs.contains("uvts")) {
			uvts = prefs.getString("uvts", null);
		}
        track(Event.VIEW_APP);
	}
	
	public static String getUvts() {
		return uvts;
	}

}
