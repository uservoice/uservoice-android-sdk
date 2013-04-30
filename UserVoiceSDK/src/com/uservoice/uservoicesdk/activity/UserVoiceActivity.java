package com.uservoice.uservoicesdk.activity;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SearchView;

import com.uservoice.uservoicesdk.R;
import com.uservoice.uservoicesdk.Session;
import com.uservoice.uservoicesdk.model.Article;
import com.uservoice.uservoicesdk.model.Forum;
import com.uservoice.uservoicesdk.model.Topic;
import com.uservoice.uservoicesdk.ui.WelcomeAdapter;

public class UserVoiceActivity extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setTitle("Help");
		getListView().setPadding(10, 0, 10, 0);
		setListAdapter(new WelcomeAdapter(this));
		getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Object obj = getListAdapter().getItem(position);
				if (obj instanceof Forum) {
					startActivity(new Intent(UserVoiceActivity.this, ForumActivity.class));
				} else if (obj instanceof Topic) {
					Session.getInstance().setTopic((Topic) obj);
					startActivity(new Intent(UserVoiceActivity.this, TopicActivity.class));
				} else if (obj instanceof Article) {
					Session.getInstance().setArticle((Article) obj);
					startActivity(new Intent(UserVoiceActivity.this, ArticleActivity.class));
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.uv_main, menu);
		
		menu.findItem(R.id.action_search).setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
			@Override
			public boolean onMenuItemActionExpand(MenuItem item) {
				getModelAdapter().setSearchActive(true);
				return true;
			}
			
			@Override
			public boolean onMenuItemActionCollapse(MenuItem item) {
				getModelAdapter().setSearchActive(false);
				return true;
			}
		});
		
		SearchView search = (SearchView) menu.findItem(R.id.action_search).getActionView();
		search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				getModelAdapter().performSearch(query);
				return true;
			}
			
			@Override
			public boolean onQueryTextChange(String query) {
				getModelAdapter().performSearch(query);
				return true;
			}
		});
		
		return true;
	}
	
	public void showForum() {
		startActivity(new Intent(this, ForumActivity.class));
	}
	
	private WelcomeAdapter getModelAdapter() {
		return (WelcomeAdapter) getListAdapter();
	}

}
