package com.uservoice.uservoicesdk.babayaga;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.util.Base64;

import com.uservoice.uservoicesdk.Session;

public class Babayaga {
	
	private static String DOMAIN = "by.uservoice.com";
	private static String CHANNEL = "d";
	
	private static String uvts;
	private static Map<String,Object> props;

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
	
	public static void setUvts(String uvts) {
		Babayaga.uvts = uvts;
	}
	
	@SuppressLint("DefaultLocale")
	private static String getTzOffset() {
		int offset = TimeZone.getDefault().getOffset(new Date().getTime());
		return String.format("%s%02d:%02d", offset > 0 ? "-" : "+", Math.floor(Math.abs(offset) / 60.0), Math.abs(offset) % 60);
	}
	
	public static void sendTrack(String event, Map<String,Object> eventProps) {
		Map<String,Object> data = new HashMap<String,Object>();
		if (props != null && !props.isEmpty()) {
			Map<String,Object> copy = new HashMap<String,Object>(props);
			copy.put("o", getTzOffset());
			data.put("u", copy);
		}
		if (eventProps != null && !eventProps.isEmpty()) {
			data.put("e", eventProps);
		}
		String subdomain = Session.getInstance().getClientConfig().getSubdomain();
		StringBuilder url = new StringBuilder(String.format("http://%s/t/%s/%s/%s", DOMAIN, subdomain, CHANNEL, event));
		if (uvts != null) {
			url.append("/");
			url.append(uvts);
		}
		url.append("/track.js?_=");
		url.append(new Date().getTime());
		url.append("&c=_");
		if (!data.isEmpty()) {
			url.append("&d=");
			try {
				url.append(URLEncoder.encode(Base64.encodeToString(new JSONObject(data).toString().getBytes(), Base64.DEFAULT), "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e);
			}
		}
		new BabayagaTask(url.toString()).execute();
	}

}
