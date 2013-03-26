package com.uservoice.uservoicesdk;

import com.uservoice.uservoicesdk.activity.UserVoiceActivity;

import android.content.Context;
import android.content.Intent;

public class UserVoice {
	
	public static void launchUserVoice(Config config, Context context) {
		Intent intent = new Intent(context, UserVoiceActivity.class);
		context.startActivity(intent);
	}
	
	public static void launchForum(Config config, Context context) {
		
	}
	
	public static void launchContactUs(Config config, Context context) {
		
	}
	
	public static void launchNewIdea(Config config, Context context) {
		
	}
	
	public static void setExternalId(String identifier, String scope) {
		
	}

	public static String version() {
		return "0.0.1";
	}

}
