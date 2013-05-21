package com.uservoice.uservoicesdk.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.widget.BaseAdapter;

import com.uservoice.uservoicesdk.rest.Callback;

public abstract class SearchAdapter<T> extends BaseAdapter {
	
	protected List<T> searchResults = new ArrayList<T>();
	protected boolean searchActive = false;
	protected Timer timer;
	protected boolean loading;
	protected Context context;
	protected String currentQuery;
	protected String pendingQuery;
	protected int scope;
	
	public void performSearch(String query) {
		pendingQuery = query;
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
			currentQuery = query;
			search(query, new DefaultCallback<List<T>>(context) {
				@Override
				public void onModel(List<T> model) {
					if (!stop) {
						searchResults = model;
						loading = false;
						notifyDataSetChanged();
						timer = null;
						searchResultsUpdated();
					}
				}
			});
		}
	}
	
	protected void searchResultsUpdated() {
	}
	
	protected boolean shouldShowSearchResults() {
		return searchActive && pendingQuery != null && !pendingQuery.isEmpty();
	}
	
	protected CharSequence highlightResult(String item) {
		if (currentQuery == null)
			return item;
		
		String[] words = currentQuery.split("\\W+");
		StringBuilder matchBuilder = new StringBuilder();
		matchBuilder.append("(?i)(");
		boolean first = true;
		for (String word : words) {
			if (word.isEmpty())
				continue;
			if (first == true) {
				first = false;
			} else {
				matchBuilder.append("|");
			}
			matchBuilder.append(word);
		}
		matchBuilder.append(")");
		
		Pattern pattern = Pattern.compile(matchBuilder.toString());
		Matcher matcher = pattern.matcher(item);
		SpannableStringBuilder highlighted = new SpannableStringBuilder(item);
		Object bg = new BackgroundColorSpan(Color.parseColor("#fff2a3"));
		while (matcher.find()) {
			highlighted.setSpan(bg, matcher.start(), matcher.end(), 0);
		}
		return highlighted;
	}
	
	protected void search(String query, Callback<List<T>> callback) {}

	public void setScope(int scope) {
		this.scope = scope;
		notifyDataSetChanged();
	}
}
