package com.uservoice.uservoicesdk.activity;

import android.os.Bundle;
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
}
