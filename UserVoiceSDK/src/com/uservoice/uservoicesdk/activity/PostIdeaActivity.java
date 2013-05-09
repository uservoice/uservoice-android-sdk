package com.uservoice.uservoicesdk.activity;

import com.uservoice.uservoicesdk.ui.InstantAnswersAdapter;
import com.uservoice.uservoicesdk.ui.PostIdeaAdapter;

public class PostIdeaActivity extends InstantAnswersActivity {

	@Override
	protected InstantAnswersAdapter createAdapter() {
		return new PostIdeaAdapter(this);
	}

}
