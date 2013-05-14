package com.uservoice.uservoicesdk.activity;

import android.os.Bundle;
import android.view.ViewGroup;

import com.uservoice.uservoicesdk.compatibility.FragmentListActivity;
import com.uservoice.uservoicesdk.flow.InitManager;
import com.uservoice.uservoicesdk.ui.InstantAnswersAdapter;

public abstract class InstantAnswersActivity extends FragmentListActivity {

	public InstantAnswersActivity() {
		super();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getListView().setDivider(null);
		getListView().setPadding(10, 0, 10, 0);
		
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