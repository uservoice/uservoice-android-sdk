package com.uservoice.uservoicesdk;

import com.uservoice.uservoicesdk.activity.ForumActivity;
import com.uservoice.uservoicesdk.activity.TabActivity;

import android.content.Context;
import android.content.Intent;

public class UserVoice {
	
	public static void launchUserVoice(Config config, Context context) {
		Session.getInstance().setConfig(config);
//		Intent intent = new Intent(context, UserVoiceActivity.class);
		Intent intent = new Intent(context, TabActivity.class);
//		Intent intent = new Intent(context, ForumActivity.class);
		context.startActivity(intent);
//		ContactDialog dialog = new ContactDialog(context);
//		dialog.show();
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

}
