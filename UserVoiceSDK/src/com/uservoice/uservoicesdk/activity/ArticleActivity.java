package com.uservoice.uservoicesdk.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;

import com.uservoice.uservoicesdk.R;
import com.uservoice.uservoicesdk.Session;
import com.uservoice.uservoicesdk.babayaga.Babayaga;
import com.uservoice.uservoicesdk.model.Article;

@SuppressLint("SetJavaScriptEnabled")
public class ArticleActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.article_layout);
		Article article = Session.getInstance().getArticle();
		System.out.println(article.getHtml());
		String styles = "iframe, img { width: 100%; }";
	    String html = String.format("<html><head><link rel=\"stylesheet\" type=\"text/css\" href=\"http://uservoice.com/stylesheets/vendor/typeset.css\"/><style>%s</style></head><body class=\"typeset\" style=\"font-family: sans-serif; margin: 1em\"><h3>%s</h3>%s</body></html>", styles, article.getTitle(), article.getHtml());

		WebView webview = (WebView) findViewById(R.id.webview);
		webview.setWebChromeClient(new WebChromeClient());
		webview.getSettings().setJavaScriptEnabled(true);
		webview.getSettings().setPluginState(PluginState.ON);
		webview.loadUrl(String.format("data:text/html;charset=utf-8,%s", Uri.encode(html)));
		
		Babayaga.track(Babayaga.Event.VIEW_ARTICLE);
	}
}
