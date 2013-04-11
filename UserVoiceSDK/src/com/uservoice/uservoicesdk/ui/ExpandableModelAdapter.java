package com.uservoice.uservoicesdk.ui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import com.uservoice.uservoicesdk.R;
import com.uservoice.uservoicesdk.rest.Callback;

public abstract class ExpandableModelAdapter<A,B> extends BaseExpandableListAdapter {
	
	private static int LOADING = 0;
	private static int MODEL = 1;

	private List<A> objects;
	private LayoutInflater inflater;
	private boolean loading;
	private final int groupLayoutId;
	private final int childLayoutId;
	private Map<A,List<B>> children = new HashMap<A, List<B>>();

	public ExpandableModelAdapter(LayoutInflater inflater, int groupLayoutId, int childLayoutId, List<A> objects) {
		this.groupLayoutId = groupLayoutId;
		this.childLayoutId = childLayoutId;
		this.objects = objects;
		this.inflater = inflater;
		loadAll();
	}
	
	protected abstract void customizeGroupLayout(View view, A group);
	protected abstract void customizeChildLayout(View view, B child);
	protected abstract void loadGroup(Callback<List<A>> callback);
	protected abstract void loadChildren(A group, Callback<List<B>> callback);
	
	protected List<B> getChildren(A group) {
		return children.get(group);
	}
	
	@Override
	public int getGroupCount() {
		return loading ? 1 : objects.size();
	}
	
	private void loadAll() {
		loading = true;
		notifyDataSetChanged();
		loadGroup(new DefaultCallback<List<A>>(null) {
			@Override
			public void onModel(List<A> model) {
				objects.addAll(model);
				loading = false;
				notifyDataSetChanged();
			}
		});
	}
	
	@Override
	public void onGroupExpanded(final int groupPosition) {
		super.onGroupCollapsed(groupPosition);
		loadChildren(objects.get(groupPosition), new DefaultCallback<List<B>>(null) {
			@Override
			public void onModel(List<B> model) {
				children.put(objects.get(groupPosition), model);
				notifyDataSetChanged();
			}
		});
	}
	
	@Override
	public int getChildrenCount(int groupPosition) {
		if (loading)
			return 0;
		A group = objects.get(groupPosition);
		if (children.containsKey(group)) {
			return children.get(group).size();
		}
		return 1;
	}

	@Override
	public Object getGroup(int groupPosition) {
		return objects.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return getChildren(objects.get(groupPosition)).get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return groupPosition * childPosition;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}
	
	@Override
	public int getGroupTypeCount() {
		return 2;
	}
	
	@Override
	public int getGroupType(int groupPosition) {
		return loading ? LOADING : MODEL;
	}
	
	@Override
	public int getChildTypeCount() {
		return 2;
	}
	
	@Override
	public int getChildType(int groupPosition, int childPosition) {
		return children.containsKey(objects.get(groupPosition)) ? MODEL : LOADING;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		View view = convertView;
		int type = getGroupType(groupPosition);
		if (view == null) {
			view = inflater.inflate(type == LOADING ? R.layout.loading_item : groupLayoutId, null);
		}
		
		if (type == MODEL) {
			A model = objects.get(groupPosition);
			customizeGroupLayout(view, model);
		}
		
		return view;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		View view = convertView;
		int type = getChildType(groupPosition, childPosition);
		if (view == null) {
			view = inflater.inflate(type == LOADING ? R.layout.loading_item : childLayoutId, null);
		}
		
		if (type == MODEL) {
			B model = getChildren(objects.get(groupPosition)).get(childPosition);
			customizeChildLayout(view, model);
		}
		
		return view;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return getChildType(groupPosition, childPosition) == MODEL;
	}

}
