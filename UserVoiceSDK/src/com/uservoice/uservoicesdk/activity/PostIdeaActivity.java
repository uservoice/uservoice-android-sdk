package com.uservoice.uservoicesdk.activity;

import android.os.Bundle;

import com.uservoice.uservoicesdk.R;
import com.uservoice.uservoicesdk.Session;
import com.uservoice.uservoicesdk.model.Forum;
import com.uservoice.uservoicesdk.ui.DefaultCallback;
import com.uservoice.uservoicesdk.ui.InstantAnswersAdapter;
import com.uservoice.uservoicesdk.ui.PostIdeaAdapter;

public class PostIdeaActivity extends InstantAnswersActivity {

    @Override
    protected void onInitialize() {
        if (Session.getInstance().getForum() != null) {
            super.onInitialize();
        } else {
            Forum.loadForum(Session.getInstance().getConfig().getForumId(), new DefaultCallback<Forum>(this) {
                @Override
                public void onModel(Forum model) {
                    Session.getInstance().setForum(model);
                    PostIdeaActivity.super.onInitialize();
                }
            });

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.uv_idea_form_title);
    }

    @Override
    protected InstantAnswersAdapter createAdapter() {
        return new PostIdeaAdapter(this);
    }

    @Override
    protected int getDiscardDialogMessage() {
        return R.string.uv_msg_confirm_discard_idea;
    }

}
