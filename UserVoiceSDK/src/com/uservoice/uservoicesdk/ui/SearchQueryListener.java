package com.uservoice.uservoicesdk.ui;

import android.widget.SearchView;

import com.uservoice.uservoicesdk.activity.SearchActivity;

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
		if (!query.isEmpty()) {
			searchActivity.showScopeBar();
		} else {
			searchActivity.hideScopeBar();
		}
		return true;
	}
}