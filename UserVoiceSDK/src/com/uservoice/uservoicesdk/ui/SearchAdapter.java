package com.uservoice.uservoicesdk.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.widget.BaseAdapter;

import com.uservoice.uservoicesdk.rest.Callback;

public abstract class SearchAdapter<T> extends BaseAdapter {
	
	protected List<T> searchResults = new ArrayList<T>();
	protected boolean searchActive = false;
	protected Timer timer;
	protected boolean loading;
	protected Context context;
	
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
	
	protected void search(String query, Callback<List<T>> callback) {}
}
