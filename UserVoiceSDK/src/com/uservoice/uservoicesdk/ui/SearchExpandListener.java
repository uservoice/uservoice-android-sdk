package com.uservoice.uservoicesdk.ui;

import android.annotation.SuppressLint;
import android.view.MenuItem;

import com.uservoice.uservoicesdk.activity.SearchActivity;

@SuppressLint("NewApi")
public class SearchExpandListener implements MenuItem.OnActionExpandListener {
	private final SearchActivity searchActivity;

	public SearchExpandListener(SearchActivity searchActivity) {
		this.searchActivity = searchActivity;
	}

	@Override
	public boolean onMenuItemActionExpand(MenuItem item) {
		searchActivity.getModelAdapter().setSearchActive(true);
		return true;
	}

	@Override
	public boolean onMenuItemActionCollapse(MenuItem item) {
		searchActivity.getModelAdapter().setSearchActive(false);
		searchActivity.hideScopeBar();
		return true;
	}
}