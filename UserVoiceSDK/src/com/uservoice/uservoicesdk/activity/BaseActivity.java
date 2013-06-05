package com.uservoice.uservoicesdk.activity;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

import com.uservoice.uservoicesdk.R;
import com.uservoice.uservoicesdk.ui.PortalAdapter;
import com.uservoice.uservoicesdk.ui.SearchExpandListener;
import com.uservoice.uservoicesdk.ui.SearchQueryListener;
import com.uservoice.uservoicesdk.ui.Utils;

public class BaseActivity extends FragmentActivity {
	
	protected Tab allTab;
	protected Tab articlesTab;
	protected Tab ideasTab;
	private int originalNavigationMode = -1;

	@Override
	@SuppressLint("NewApi")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (Utils.hasActionBar()) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    if (item.getItemId() == android.R.id.home) {
	    	onBackPressed();
	        return true;
	    }
	    return super.onOptionsItemSelected(item);
	}
	
	@SuppressLint("NewApi")
	protected void setupScopedSearch(Menu menu) {
		if (Utils.hasActionBar()) {
			menu.findItem(R.id.action_search).setOnActionExpandListener(new SearchExpandListener((SearchActivity) this));
			SearchView search = (SearchView) menu.findItem(R.id.action_search).getActionView();
			search.setOnQueryTextListener(new SearchQueryListener((SearchActivity) this));
			
			ActionBar.TabListener listener = new ActionBar.TabListener() {
				@Override
				public void onTabUnselected(Tab tab, FragmentTransaction ft) {
				}
				
				@Override
				public void onTabSelected(Tab tab, FragmentTransaction ft) {
					((SearchActivity) BaseActivity.this).getModelAdapter().setScope((Integer) tab.getTag());
				}
				
				@Override
				public void onTabReselected(Tab tab, FragmentTransaction ft) {
				}
			};
			allTab = getActionBar().newTab().setText(getString(R.string.uv_all_results_filter)).setTabListener(listener).setTag(PortalAdapter.SCOPE_ALL);
			getActionBar().addTab(allTab);
			articlesTab = getActionBar().newTab().setText(getString(R.string.uv_articles_filter)).setTabListener(listener).setTag(PortalAdapter.SCOPE_ARTICLES);
			getActionBar().addTab(articlesTab);
			ideasTab = getActionBar().newTab().setText(getString(R.string.uv_ideas_filter)).setTabListener(listener).setTag(PortalAdapter.SCOPE_IDEAS);
			getActionBar().addTab(ideasTab);
		} else {
			menu.findItem(R.id.action_search).setVisible(false);
		}
	}
	
	@SuppressLint("NewApi")
	public void updateScopedSearch(int results, int articleResults, int ideaResults) {
		if (!Utils.hasActionBar())
			return;
		allTab.setText(String.format("%s (%d)", getString(R.string.uv_all_results_filter), results));
		articlesTab.setText(String.format("%s (%d)", getString(R.string.uv_articles_filter), articleResults));
		ideasTab.setText(String.format("%s (%d)", getString(R.string.uv_ideas_filter), ideaResults));
	}
	
	@SuppressLint("NewApi")
	public void showScopeBar() {
		if (!Utils.hasActionBar())
			return;
		if (originalNavigationMode == -1)
			originalNavigationMode = getActionBar().getNavigationMode();
		getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
	}
	
	@SuppressLint("NewApi")
	public void hideScopeBar() {
		if (!Utils.hasActionBar())
			return;
		getActionBar().setNavigationMode(originalNavigationMode == -1 ? ActionBar.NAVIGATION_MODE_STANDARD : originalNavigationMode);
	}
}
