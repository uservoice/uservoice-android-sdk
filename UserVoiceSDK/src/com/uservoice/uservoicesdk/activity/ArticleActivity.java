package com.uservoice.uservoicesdk.activity;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

import com.uservoice.uservoicesdk.R;
import com.uservoice.uservoicesdk.Session;
import com.uservoice.uservoicesdk.babayaga.Babayaga;
import com.uservoice.uservoicesdk.model.Article;
import com.uservoice.uservoicesdk.ui.WebViews;

public class ArticleActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.article_layout);
		Article article = Session.getInstance().getArticle();
		WebView webView = (WebView) findViewById(R.id.webview);
		WebViews.displayArticle(webView, article);
		
		Babayaga.track(Babayaga.Event.VIEW_ARTICLE);
	}
}
