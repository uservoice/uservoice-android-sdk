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
        searchActivity.getSearchAdapter().performSearch(query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        searchActivity.getSearchAdapter().performSearch(query);
        if (query.length() > 0) {
            searchActivity.showSearch();
        } else {
            searchActivity.hideSearch();
        }
        return true;
    }
}
