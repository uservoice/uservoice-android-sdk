package com.uservoice.uservoicesdk;

import java.util.Map;

import android.content.Context;
import android.content.Intent;

import com.uservoice.uservoicesdk.activity.ContactActivity;
import com.uservoice.uservoicesdk.activity.ForumActivity;
import com.uservoice.uservoicesdk.activity.PortalActivity;
import com.uservoice.uservoicesdk.activity.PostIdeaActivity;
import com.uservoice.uservoicesdk.babayaga.Babayaga;
import com.uservoice.uservoicesdk.model.ClientConfig;
import com.uservoice.uservoicesdk.ui.DefaultCallback;

public class UserVoice {
	
	public static void launchUserVoice(Context context) {
		Babayaga.track(Babayaga.Event.VIEW_CHANNEL);
		context.startActivity(new Intent(context, PortalActivity.class));
	}
	
	public static void launchForum(Context context) {
		Babayaga.track(Babayaga.Event.VIEW_CHANNEL);
		context.startActivity(new Intent(context, ForumActivity.class));
	}
	
	public static void launchContactUs(Context context) {
		Babayaga.track(Babayaga.Event.VIEW_CHANNEL);
		context.startActivity(new Intent(context, ContactActivity.class));
	}
	
	public static void launchPostIdea(Context context) {
		Babayaga.track(Babayaga.Event.VIEW_CHANNEL);
		context.startActivity(new Intent(context, PostIdeaActivity.class));
	}
	
	public static void init(Config config, Context context) {
		Session.reset();
		Babayaga.init(context);
		Babayaga.setUserTraits(config.getUserTraits());
		Session.getInstance().setContext(context);
		Session.getInstance().setConfig(config);

        // we have to do this preemptively so that Babayaga can send the view_app event
        ClientConfig.loadClientConfig(new DefaultCallback<ClientConfig>(context) {
            @Override
            public void onModel(ClientConfig model) {
                Session.getInstance().setClientConfig(model);
            }
        });
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
