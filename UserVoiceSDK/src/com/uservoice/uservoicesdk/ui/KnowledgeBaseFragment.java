package com.uservoice.uservoicesdk.ui;

import java.util.ArrayList;
import java.util.List;

import com.uservoice.uservoicesdk.model.Topic;
import com.uservoice.uservoicesdk.rest.Callback;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class KnowledgeBaseFragment extends ListFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setListAdapter(new LoadAllAdapter<Topic>(getActivity(), android.R.layout.simple_list_item_2, new ArrayList<Topic>()) {
			@Override
			protected void customizeLayout(View view, Topic model) {
				TextView textView = (TextView) view.findViewById(android.R.id.text1);
				textView.setText(model.getName());
				
				textView = (TextView) view.findViewById(android.R.id.text2);
				textView.setText(String.format("%d articles", model.getNumberOfArticles()));
			}

			@Override
			protected void loadPage(int page, Callback<List<Topic>> callback) {
//				Topic.loadTopics(callback);
			}
		});
	}
}
