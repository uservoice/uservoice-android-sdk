package com.uservoice.uservoicesdk;

import java.util.Map;

import android.content.Context;
import android.content.Intent;

import com.uservoice.uservoicesdk.activity.PortalActivity;
import com.uservoice.uservoicesdk.babayaga.Babayaga;

public class UserVoice {
	
	public static void launchUserVoice(Context context) {
		context.startActivity(new Intent(context, PortalActivity.class));
	}
	
	public static void launchForum(Config config, Context context) {
	}
	
	public static void launchContactUs(Config config, Context context) {
	}
	
	public static void launchNewIdea(Config config, Context context) {
	}
	
	public static void init(Config config, Context context) {
		Babayaga.init(context);
		Babayaga.setUserTraits(config.getUserTraits());
		Session.getInstance().setContext(context);
		Session.getInstance().setConfig(config);
	}
	
	public static void setExternalId(String scope, String id) {
		Session.getInstance().setExternalId(scope, id);
	}
	
	public static void track(String event, Map<String, Object> properties) {
		Babayaga.track(event, properties);
	}
	
	public static void track(String event) {
		track(event, null);
	}
	
	public static String getVersion() {
		// TODO get this from the package version maybe
		return "0.0.1";
	}
}
