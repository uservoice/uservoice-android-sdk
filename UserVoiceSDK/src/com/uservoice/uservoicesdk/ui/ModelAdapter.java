package com.uservoice.uservoicesdk.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.uservoice.uservoicesdk.R;
import com.uservoice.uservoicesdk.rest.Callback;

public abstract class ModelAdapter<T> extends BaseAdapter {

	private static final int MODEL = 0;
	private static final int LOADING = 1;
	
	private final Context context;
	private final int layoutId;
	private boolean loading;
	private LayoutInflater inflater;
	private List<T> searchResults = new ArrayList<T>();
	private List<T> objects;
	private int page = 1;
	private boolean searchActive;
	private Timer timer;

	public ModelAdapter(Context context, int layoutId, List<T> objects) {
		this.context = context;
		this.layoutId = layoutId;
		this.objects = objects;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		int type = getItemViewType(position);
		if (view == null) {
			view = inflater.inflate(type == LOADING ? R.layout.loading_item : layoutId, null);
		}
		
		if (type == MODEL) {
			T model = getObjects().get(position);
			customizeLayout(view, model);
		}
		
		return view;
	}
	
	@Override
	public int getItemViewType(int position) {
		return position == getObjects().size() ? LOADING : MODEL;
	}
	
	@Override
	public int getCount() {
		return getObjects().size() + ((!getObjects().isEmpty() || searchActive) && loading ? 1 : 0);
	}
	
	@Override
	public int getViewTypeCount() {
		return 2;
	}
	
	@Override
	public long getItemId(int position) {
		return getItemViewType(position) == LOADING ? -1 : position;
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
	
	public abstract void search(String query, Callback<List<T>> callback);
	public abstract void loadPage(int page, Callback<List<T>> callback);
	public abstract int getTotalNumberOfObjects();
	protected abstract void customizeLayout(View view, T model);

	@Override
	public Object getItem(int position) {
		return position < getObjects().size() ? getObjects().get(position) : null;
	}
	
	private List<T> getObjects() {
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
