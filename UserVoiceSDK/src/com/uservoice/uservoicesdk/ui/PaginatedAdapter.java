package com.uservoice.uservoicesdk.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;

import com.uservoice.uservoicesdk.rest.Callback;

public abstract class PaginatedAdapter<T> extends ModelAdapter<T> {

	private List<T> searchResults = new ArrayList<T>();
	private int page = 1;
	private boolean searchActive;
	private Timer timer;

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
	
	protected abstract void search(String query, Callback<List<T>> callback);
	protected abstract int getTotalNumberOfObjects();
	
	protected List<T> getObjects() {
		return searchActive && (loading || !searchResults.isEmpty()) ? searchResults : objects;
	}
	
	public void performSearch(String query) {
		if (query.isEmpty()) {
			searchResults = new ArrayList<T>();
			loading = false;
			notifyDataSetChanged();
		} else {
			loading = true;
			notifyDataSetChanged();
			if (timer != null) {
				timer.cancel();
				timer = null;
			}
			timer = new Timer();
			timer.schedule(new SearchTask(query), 200);
		}
	}
	
	public void setSearchActive(boolean searchActive) {
		this.searchActive = searchActive;
		loading = false;
		notifyDataSetChanged();
	}
	
	private class SearchTask extends TimerTask {
		
		private final String query;
		private boolean stop;

		public SearchTask(String query) {
			this.query = query;
		}
		
		@Override
		public boolean cancel() {
			stop = true;
			return true;
		}

		@Override
		public void run() {
			search(query, new DefaultCallback<List<T>>(context) {
				@Override
				public void onModel(List<T> model) {
					if (!stop) {
						searchResults = model;
						loading = false;
						notifyDataSetChanged();
						timer = null;
					}
				}
			});
		}
		
	}

}
