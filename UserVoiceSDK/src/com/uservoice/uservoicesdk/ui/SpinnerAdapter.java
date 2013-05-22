package com.uservoice.uservoicesdk.ui;

import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.uservoice.uservoicesdk.R;

public class SpinnerAdapter<T> extends BaseAdapter {
	
	private static int NONE = 0;
	private static int OBJECT = 1;
	
	private final List<T> objects;
	private LayoutInflater inflater;

	public SpinnerAdapter(Activity context, List<T> objects) {
		this.objects = objects;
		inflater = context.getLayoutInflater();
	}

	@Override
	public int getCount() {
		return objects.size() + 1;
	}

	@Override
	public Object getItem(int position) {
		if (position == 0)
			return null;
		return objects.get(position - 1);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}
	
	@Override
	public int getItemViewType(int position) {
		return position == 0 ? NONE : OBJECT;
	}
	
	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		int type = getItemViewType(position);
		if (view == null) {
			view = inflater.inflate(android.R.layout.simple_list_item_1, null);
		}

		TextView textView = (TextView) view;
		if (type == OBJECT) {
			textView.setTextColor(Color.BLACK);
			textView.setText(getItem(position).toString());
		} else {
			textView.setTextColor(Color.GRAY);
			textView.setText(R.string.select_none);
		}
		return view;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		int type = getItemViewType(position);
		if (view == null) {
			view = inflater.inflate(android.R.layout.simple_list_item_1, null);
		}

		TextView textView = (TextView) view;
		if (type == OBJECT) {
			textView.setTextColor(Color.BLACK);
			textView.setText(getItem(position).toString());
		} else {
			textView.setTextColor(Color.BLACK);
			textView.setText(R.string.select_one);
		}
		return view;
	}

}
