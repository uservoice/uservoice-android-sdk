package com.uservoice.uservoicesdk.ui;

import java.util.List;

import com.uservoice.uservoicesdk.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public abstract class ModelAdapter<T> extends ArrayAdapter<T> {

	private static final int MODEL = 0;
	private static final int LOADING = 1;
	
	private final int layoutId;
	private boolean loading;
	private LayoutInflater inflater;

	public ModelAdapter(Context context, int layoutId, List<T> objects) {
		super(context, 0, objects);
		this.layoutId = layoutId;
		inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		int type = getItemViewType(position);
		if (view == null) {
			view = inflater.inflate(type == LOADING ? R.layout.loading_item : layoutId, null);
		}
		
		if (type == MODEL) {
			T model = getItem(position);
			customizeLayout(view, model);
		}
		
		return view;
	}
	
	protected abstract void customizeLayout(View view, T model);

	@Override
	public int getItemViewType(int position) {
		return position == super.getCount() ? LOADING : MODEL;
	}
	
	@Override
	public int getCount() {
		return super.getCount() + (loading ? 1 : 0);
	}
	
	@Override
	public int getViewTypeCount() {
		return 2;
	}
	
	@Override
	public long getItemId(int position) {
		return getItemViewType(position) == LOADING ? -1 : position;
	}
	
	public void setLoading(boolean loading) {
		this.loading = loading;
		notifyDataSetChanged();
	}
	
	public boolean isLoading() {
		return loading;
	}
}
