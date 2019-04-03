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
import com.uservoice.uservoicesdk.rest.RestResult;
import com.uservoice.uservoicesdk.ui.DefaultCallback;

public class UserVoice {

    public static void launchUserVoice(Context context) {
        context.startActivity(new Intent(context, PortalActivity.class));
    }

    public static void launchForum(Context context) {
        context.startActivity(new Intent(context, ForumActivity.class));
    }

    public static void launchContactUs(Context context) {
        context.startActivity(new Intent(context, ContactActivity.class));
    }

    public static void launchPostIdea(Context context) {
        context.startActivity(new Intent(context, PostIdeaActivity.class));
    }

    public static void init(Config config, Context context) {
        Session.reset();
        Session.getInstance().init(context, config);
        Babayaga.init(context);
    }

    public static void setExternalId(String scope, String id) {
        Session.getInstance().setExternalId(scope, id);
    }

    public static void track(Context context, String event, Map<String, Object> properties) {
        Babayaga.track(context, event, properties);
    }

    public static void track(Context context, String event) {
        track(context, event, null);
    }

    public static String getVersion() {
        return "1.2.10";
    }
}
