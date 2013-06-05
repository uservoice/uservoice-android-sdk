package com.uservoice.uservoicesdk.activity;

import com.uservoice.uservoicesdk.ui.SearchAdapter;

public interface SearchActivity {
	SearchAdapter<?> getSearchAdapter();
	void updateScopedSearch(int results, int articleResults, int ideaResults);
	void showSearch();
	void hideSearch();
}
