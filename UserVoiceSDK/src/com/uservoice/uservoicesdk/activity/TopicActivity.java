package com.uservoice.uservoicesdk.activity;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.uservoice.uservoicesdk.R;
import com.uservoice.uservoicesdk.Session;
import com.uservoice.uservoicesdk.babayaga.Babayaga;
import com.uservoice.uservoicesdk.compatibility.FragmentListActivity;
import com.uservoice.uservoicesdk.model.Article;
import com.uservoice.uservoicesdk.model.Topic;
import com.uservoice.uservoicesdk.rest.Callback;
import com.uservoice.uservoicesdk.ui.LoadAllAdapter;
import com.uservoice.uservoicesdk.ui.Utils;

public class TopicActivity extends FragmentListActivity {
	
	@SuppressLint({ "InlinedApi", "NewApi" })
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (Utils.hasActionBar()) {
			ActionBar actionBar = getActionBar();
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
			actionBar.setListNavigationCallbacks(new ArrayAdapter<Topic>(this, android.R.layout.simple_spinner_dropdown_item, Session.getInstance().getTopics()) {
				@Override
				public View getView(int position, View convertView, ViewGroup parent) {
					View view = super.getView(position, convertView, parent);
					((TextView) view).setTextColor(Color.WHITE);
					return view;
				}
				
				@Override
				public View getDropDownView(int position, View convertView, ViewGroup parent) {
					View view = super.getDropDownView(position, convertView, parent);
					((TextView) view).setTextColor(Color.WHITE);
					return view;
				}
			}, new ActionBar.OnNavigationListener() {
				@Override
				@SuppressWarnings("unchecked")
				public boolean onNavigationItemSelected(int itemPosition, long itemId) {
					Topic topic = Session.getInstance().getTopics().get(itemPosition);
					Session.getInstance().setTopic(topic);
					((LoadAllAdapter<Article>)getListAdapter()).reload();
					return true;
				}
			});
			actionBar.setSelectedNavigationItem(Session.getInstance().getTopics().indexOf(Session.getInstance().getTopic()));
		}
		
		setTitle(null);
		getListView().setPadding(10, 0, 10, 0);
		getListView().setDivider(null);
		setListAdapter(new LoadAllAdapter<Article>(this, R.layout.article_item, new ArrayList<Article>()) {
			@Override
			protected void loadPage(int page, Callback<List<Article>> callback) {
				Topic topic = Session.getInstance().getTopic();
				if (topic == Topic.ALL_ARTICLES) {
					Article.loadAll(callback);
				} else {
					Article.loadForTopic(topic.getId(), callback);
				}
			}
			
			@Override
			protected void customizeLayout(View view, Article model) {
				TextView text = (TextView) view.findViewById(R.id.article_name);
				text.setText(model.getTitle());
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
		
		Babayaga.track(Babayaga.Event.VIEW_TOPIC);
	}
}
