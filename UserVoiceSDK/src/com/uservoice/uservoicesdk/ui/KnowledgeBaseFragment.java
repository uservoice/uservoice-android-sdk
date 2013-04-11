package com.uservoice.uservoicesdk.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.uservoice.uservoicesdk.R;
import com.uservoice.uservoicesdk.model.Article;
import com.uservoice.uservoicesdk.model.Topic;
import com.uservoice.uservoicesdk.rest.Callback;

public class KnowledgeBaseFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.topics_layout, null);
		ExpandableListView listView = (ExpandableListView) view.findViewById(R.id.list);
		
		listView.setAdapter(new ExpandableModelAdapter<Topic, Article>(inflater, R.layout.topic_item, R.layout.article_item, new ArrayList<Topic>()) {

			@Override
			protected void customizeGroupLayout(View view, Topic group) {
				TextView text = (TextView) view.findViewById(R.id.topic_name);
				text.setText(group.getName());
				
				text = (TextView) view.findViewById(R.id.article_count);
				text.setText(String.format("%d articles", group.getNumberOfArticles()));
			}

			@Override
			protected void customizeChildLayout(View view, Article child) {
				TextView text = (TextView) view.findViewById(R.id.article_name);
				text.setText(child.getQuestion());
			}

			@Override
			protected void loadGroup(Callback<List<Topic>> callback) {
				Topic.loadTopics(callback);
			}

			@Override
			protected void loadChildren(Topic group, Callback<List<Article>> callback) {
				Article.loadForTopic(group.getId(), callback);
			}
		});
		
		return view;
	}
	
}
