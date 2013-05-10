package com.uservoice.uservoicesdk.activity;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

import com.uservoice.uservoicesdk.R;
import com.uservoice.uservoicesdk.babayaga.Babayaga;
import com.uservoice.uservoicesdk.ui.PortalAdapter;
import com.uservoice.uservoicesdk.ui.SearchExpandListener;
import com.uservoice.uservoicesdk.ui.SearchQueryListener;

public class PortalActivity extends ListActivity implements SearchActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setTitle(R.string.portal_title);
		getListView().setPadding(10, 0, 10, 0);
		setListAdapter(new PortalAdapter(this));
		getListView().setOnItemClickListener(getModelAdapter());
		
		Babayaga.track(Babayaga.Event.VIEW_CHANNEL);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.portal, menu);
		menu.findItem(R.id.action_search).setOnActionExpandListener(new SearchExpandListener(this));
		SearchView search = (SearchView) menu.findItem(R.id.action_search).getActionView();
		search.setOnQueryTextListener(new SearchQueryListener(this));
		return true;
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		if (item.getItemId() == R.id.action_contact) {
			startActivity(new Intent(this, ContactActivity.class));
			return true;
		}
		return super.onMenuItemSelected(featureId, item);
	}
	
	public PortalAdapter getModelAdapter() {
		return (PortalAdapter) getListAdapter();
	}

}
