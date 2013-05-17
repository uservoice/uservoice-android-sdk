package com.uservoice.uservoicesdk.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

import com.uservoice.uservoicesdk.R;
import com.uservoice.uservoicesdk.Session;
import com.uservoice.uservoicesdk.babayaga.Babayaga;
import com.uservoice.uservoicesdk.model.Article;
import com.uservoice.uservoicesdk.ui.Utils;

public class ArticleActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.article_layout);
		Article article = Session.getInstance().getArticle();
		setTitle(article.getTitle());
		WebView webView = (WebView) findViewById(R.id.webview);
		Utils.displayArticle(webView, article);
		
		Babayaga.track(Babayaga.Event.VIEW_ARTICLE);
	}
	
	@Override
	@SuppressLint("NewApi")
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.portal, menu);
		if (Utils.hasActionBar()) {
//			menu.findItem(R.id.action_search).setOnActionExpandListener(new SearchExpandListener(this));
//			SearchView search = (SearchView) menu.findItem(R.id.action_search).getActionView();
//			search.setOnQueryTextListener(new SearchQueryListener(this));
		} else {
			menu.findItem(R.id.action_search).setVisible(false);
		}
		return true;
	}
	

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		if (item.getItemId() == R.id.action_contact) {
			startActivity(new Intent(this, ContactActivity.class));
			return true;
		}
		return super.onMenuItemSelected(featureId, item);
	}
}
