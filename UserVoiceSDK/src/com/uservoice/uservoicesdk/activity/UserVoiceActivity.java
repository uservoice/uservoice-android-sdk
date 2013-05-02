package com.uservoice.uservoicesdk.activity;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

import com.uservoice.uservoicesdk.R;
import com.uservoice.uservoicesdk.ui.WelcomeAdapter;

public class UserVoiceActivity extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setTitle("Help");
		getListView().setPadding(10, 0, 10, 0);
		setListAdapter(new WelcomeAdapter(this));
		getListView().setOnItemClickListener(getModelAdapter());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.uv_main, menu);
		
		menu.findItem(R.id.action_search).setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
			@Override
			public boolean onMenuItemActionExpand(MenuItem item) {
				getModelAdapter().setSearchActive(true);
				return true;
			}
			
			@Override
			public boolean onMenuItemActionCollapse(MenuItem item) {
				getModelAdapter().setSearchActive(false);
				return true;
			}
		});
		
		SearchView search = (SearchView) menu.findItem(R.id.action_search).getActionView();
		search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				getModelAdapter().performSearch(query);
				return true;
			}
			
			@Override
			public boolean onQueryTextChange(String query) {
				getModelAdapter().performSearch(query);
				return true;
			}
		});
		
		return true;
	}
	
	private WelcomeAdapter getModelAdapter() {
		return (WelcomeAdapter) getListAdapter();
	}

}
