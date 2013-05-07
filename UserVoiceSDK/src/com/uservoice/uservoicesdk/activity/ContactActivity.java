package com.uservoice.uservoicesdk.activity;

import android.app.ListActivity;
import android.os.Bundle;

import com.uservoice.uservoicesdk.ui.ContactAdapter;

public class ContactActivity extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getListView().setDivider(null);
		getListView().setPadding(10, 0, 10, 0);
		
		setListAdapter(new ContactAdapter(this));
		getListView().setOnHierarchyChangeListener((ContactAdapter) getListAdapter());
	}
}
