package com.uservoice.uservoicesdk.ui;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;

import com.uservoice.uservoicesdk.model.Article;

public class WebViews {

	@SuppressLint("SetJavaScriptEnabled")
	public static void displayArticle(WebView webView, Article article) {
		String styles = "iframe, img { width: 100%; }";
	    String html = String.format("<html><head><link rel=\"stylesheet\" type=\"text/css\" href=\"http://uservoice.com/stylesheets/vendor/typeset.css\"/><style>%s</style></head><body class=\"typeset\" style=\"font-family: sans-serif; margin: 1em\"><h3>%s</h3>%s</body></html>", styles, article.getTitle(), article.getHtml());
		webView.setWebChromeClient(new WebChromeClient());
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setPluginState(PluginState.ON);
		webView.loadUrl(String.format("data:text/html;charset=utf-8,%s", Uri.encode(html)));
	}
}
