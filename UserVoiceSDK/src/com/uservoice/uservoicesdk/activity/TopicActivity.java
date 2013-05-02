package com.uservoice.uservoicesdk.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.uservoice.uservoicesdk.R;
import com.uservoice.uservoicesdk.Session;
import com.uservoice.uservoicesdk.model.Article;
import com.uservoice.uservoicesdk.model.Topic;
import com.uservoice.uservoicesdk.rest.Callback;
import com.uservoice.uservoicesdk.ui.LoadAllAdapter;

public class TopicActivity extends ListActivity {
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final Topic topic = Session.getInstance().getTopic();
		
		setTitle(topic == null ? getString(R.string.all_articles) : topic.getName());
		getListView().setPadding(10, 0, 10, 0);
		setListAdapter(new LoadAllAdapter<Article>(this, R.layout.article_item, new ArrayList<Article>()) {
			@Override
			protected void loadPage(int page, Callback<List<Article>> callback) {
				if (topic == null) {
					Article.loadAll(callback);
				} else {
					Article.loadForTopic(topic.getId(), callback);
				}
			}
			
			@Override
			protected void customizeLayout(View view, Article model) {
				TextView text = (TextView) view.findViewById(R.id.article_name);
				text.setText(model.getQuestion());
			}
		});
		
		getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Article article = (Article) getListAdapter().getItem(position);
				Session.getInstance().setArticle(article);
				startActivity(new Intent(TopicActivity.this, ArticleActivity.class));
			}
		});
	}
}
