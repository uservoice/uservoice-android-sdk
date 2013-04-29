package com.uservoice.uservoicesdk.ui;

import java.util.List;

import android.content.Context;

public abstract class PaginatedAdapter<T> extends ModelAdapter<T> {

	private int page = 1;

	public PaginatedAdapter(Context context, int layoutId, List<T> objects) {
		super(context, layoutId, objects);
	}

	
	public void loadMore() {
		if (loading || searchActive || objects.size() == getTotalNumberOfObjects()) return;
		loading = true;
		notifyDataSetChanged();
		loadPage(page, new DefaultCallback<List<T>>(context) {
			@Override
			public void onModel(List<T> model) {
				objects.addAll(model);
				page += 1;
				loading = false;
				notifyDataSetChanged();
			}
		});
	}
	
	protected abstract int getTotalNumberOfObjects();
	
	protected List<T> getObjects() {
		return searchActive && (loading || !searchResults.isEmpty()) ? searchResults : objects;
	}
}
