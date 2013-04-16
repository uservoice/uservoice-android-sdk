package com.uservoice.uservoicesdk.activity;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;

import com.uservoice.uservoicesdk.R;
import com.uservoice.uservoicesdk.ui.WelcomeAdapter;

public class UserVoiceActivity extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setTitle("Help");
		getListView().setPadding(10, 0, 10, 0);
		setListAdapter(new WelcomeAdapter(this));
		getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.uv_main, menu);
		return true;
	}
	
	public void showForum() {
		startActivity(new Intent(this, ForumActivity.class));
	}

}
