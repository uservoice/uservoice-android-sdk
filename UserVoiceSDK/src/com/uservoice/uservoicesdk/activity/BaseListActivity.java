package com.uservoice.uservoicesdk.activity;

import android.os.Bundle;

import com.uservoice.uservoicesdk.compatibility.FragmentListActivity;

public class BaseListActivity extends FragmentListActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getListView().setPadding(15, 0, 15, 0);
	}

}
