package com.uservoice.uservoicesdk.activity;

import com.uservoice.uservoicesdk.ui.ContactAdapter;

import android.app.ListActivity;
import android.os.Bundle;

public class ContactActivity extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getListView().setDivider(null);
		getListView().setPadding(10, 0, 10, 0);
		
		setListAdapter(new ContactAdapter(this));
	}
}
