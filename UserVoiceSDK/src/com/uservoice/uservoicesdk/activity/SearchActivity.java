package com.uservoice.uservoicesdk.activity;

import com.uservoice.uservoicesdk.ui.SearchAdapter;

public interface SearchActivity {
	SearchAdapter<?> getModelAdapter();
	void updateScopedSearch(int results, int articleResults, int ideaResults);
	void showScopeBar();
	void hideScopeBar();
}
