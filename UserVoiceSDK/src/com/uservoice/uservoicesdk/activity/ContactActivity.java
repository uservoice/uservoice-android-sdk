package com.uservoice.uservoicesdk.activity;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.uservoice.uservoicesdk.ui.ContactAdapter;

public class ContactActivity extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		getListView().setDivider(null);
		getListView().setPadding(10, 0, 10, 0);
		
		setListAdapter(new ContactAdapter(this));
		getListView().setOnHierarchyChangeListener((ContactAdapter) getListAdapter());
		getListView().setOnItemClickListener((ContactAdapter) getListAdapter());
		getListView().setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
	    }
	    return super.onOptionsItemSelected(item);
	}
}
