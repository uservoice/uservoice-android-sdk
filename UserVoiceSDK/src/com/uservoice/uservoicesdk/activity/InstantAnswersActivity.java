package com.uservoice.uservoicesdk.activity;

import android.os.Bundle;
import android.view.ViewGroup;

import com.uservoice.uservoicesdk.flow.InitManager;
import com.uservoice.uservoicesdk.ui.InstantAnswersAdapter;

public abstract class InstantAnswersActivity extends BaseListActivity {

	public InstantAnswersActivity() {
		super();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getListView().setDivider(null);
		
		final InstantAnswersAdapter adapter = createAdapter();
		setListAdapter(adapter);
		getListView().setOnHierarchyChangeListener(adapter);
		getListView().setOnItemClickListener(adapter);
		getListView().setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
		new InitManager(this, new Runnable() {
			@Override
			public void run() {
				adapter.notifyDataSetChanged();
			}
		}).init();
	}

	protected abstract InstantAnswersAdapter createAdapter();

}