package com.uservoice.uservoicesdk.ui;

import com.uservoice.uservoicesdk.activity.SearchActivity;

import android.widget.SearchView;

public class SearchQueryListener implements SearchView.OnQueryTextListener {
	private final SearchActivity searchActivity;

	public SearchQueryListener(SearchActivity searchActivity) {
		this.searchActivity = searchActivity;
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		searchActivity.getModelAdapter().performSearch(query);
		return true;
	}

	@Override
	public boolean onQueryTextChange(String query) {
		searchActivity.getModelAdapter().performSearch(query);
		return true;
	}
}