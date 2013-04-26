package com.uservoice.uservoicesdk.ui;

import java.util.List;

import com.uservoice.uservoicesdk.R;
import com.uservoice.uservoicesdk.rest.Callback;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class ModelAdapter<T> extends BaseAdapter {
	
	protected static final int MODEL = 0;
	protected static final int LOADING = 1;
	
	protected final Context context;
	protected final int layoutId;
	protected LayoutInflater inflater;
	protected List<T> objects;
	protected boolean loading;

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
	public boolean isEnabled(int position) {
		return getItemViewType(position) == MODEL;
	}
	
	@Override
	public int getItemViewType(int position) {
		return position == getObjects().size() ? LOADING : MODEL;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}
	
	@Override
	public int getCount() {
		return getObjects().size() + (loading ? 1 : 0);
	}
	
	@Override
	public Object getItem(int position) {
		return position < getObjects().size() ? getObjects().get(position) : null;
	}

	@Override
	public long getItemId(int position) {
		return getItemViewType(position) == LOADING ? -1 : position;
	}

	protected List<T> getObjects() {
		return objects;
	}
	
	public void add(int location, T object) {
		objects.add(location, object);
		notifyDataSetChanged();
	}
	
	protected abstract void customizeLayout(View view, T model);
	protected abstract void loadPage(int page, Callback<List<T>> callback);
}
