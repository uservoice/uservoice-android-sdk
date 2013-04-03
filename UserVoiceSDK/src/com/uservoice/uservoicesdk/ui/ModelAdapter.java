package com.uservoice.uservoicesdk.ui;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public abstract class ModelAdapter<T> extends ArrayAdapter<T> {

	private final int layoutId;

	public ModelAdapter(Context context, int layoutId, List<T> objects) {
		super(context, 0, objects);
		this.layoutId = layoutId;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(layoutId, null);
		}
		
		T model = getItem(position);
		
		customizeLayout(view, model);
		
		return view;
	}
	
	protected abstract void customizeLayout(View view, T model);

}
