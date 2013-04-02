package com.uservoice.uservoicesdk;

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
	
	public static void setExternalId(String identifier, String scope) {
		
	}

	public static String getVersion() {
		// TODO get this from the package version maybe
		return "0.0.1";
	}

}
