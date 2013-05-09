package com.uservoice.uservoicesdk.activity;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.uservoice.uservoicesdk.ui.InstantAnswersAdapter;

public abstract class InstantAnswersActivity extends ListActivity {

	public InstantAnswersActivity() {
		super();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		getListView().setDivider(null);
		getListView().setPadding(10, 0, 10, 0);
		
		InstantAnswersAdapter adapter = createAdapter();
		setListAdapter(adapter);
		getListView().setOnHierarchyChangeListener(adapter);
		getListView().setOnItemClickListener(adapter);
		getListView().setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
	}

	protected abstract InstantAnswersAdapter createAdapter();

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    if (item.getItemId() == android.R.id.home) {
	        finish();
	        return true;
	    }
	    return super.onOptionsItemSelected(item);
	}

}