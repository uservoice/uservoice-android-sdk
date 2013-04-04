package com.uservoice.uservoicesdk.ui;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.uservoice.uservoicesdk.R;
import com.uservoice.uservoicesdk.rest.Callback;

public abstract class ModelAdapter<T> extends BaseAdapter implements Filterable {

	private static final int MODEL = 0;
	private static final int LOADING = 1;
	
	private final Context context;
	private final int layoutId;
	private boolean loading;
	private LayoutInflater inflater;
//	private List<T> searchResults;
	private List<T> objects;
	private int page = 1;

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
			T model = objects.get(position);
			customizeLayout(view, model);
		}
		
		return view;
	}
	
	protected abstract void customizeLayout(View view, T model);

	@Override
	public int getItemViewType(int position) {
		return position == objects.size() ? LOADING : MODEL;
	}
	
	@Override
	public int getCount() {
		return objects.size() + (!objects.isEmpty() && loading ? 1 : 0);
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
		if (loading || objects.size() == getTotalNumberOfObjects()) return;
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
	
	public abstract void loadPage(int page, Callback<List<T>> callback);
	public abstract int getTotalNumberOfObjects();
	
	public void setLoading(boolean loading) {
		this.loading = loading;
		notifyDataSetChanged();
	}
	
	public boolean isLoading() {
		return loading;
	}
	
	@Override
	public Object getItem(int position) {
		return position < objects.size() ? objects.get(position) : null;
	}
	
	@Override
	public Filter getFilter() {
		return new ModelFilter();
	}
	
	private class ModelFilter extends Filter {

		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		protected void publishResults(CharSequence constraint, FilterResults results) {
			// TODO Auto-generated method stub
			
		}
	}
	    
}
