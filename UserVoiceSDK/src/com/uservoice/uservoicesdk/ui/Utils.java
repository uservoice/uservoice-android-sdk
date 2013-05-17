package com.uservoice.uservoicesdk.ui;

import java.text.DateFormat;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Build;
import android.text.Html;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.uservoice.uservoicesdk.R;
import com.uservoice.uservoicesdk.image.ImageCache;
import com.uservoice.uservoicesdk.model.Article;
import com.uservoice.uservoicesdk.model.Suggestion;

public class Utils {

	@SuppressLint("SetJavaScriptEnabled")
	public static void displayArticle(WebView webView, Article article) {
		String styles = "iframe, img { width: 100%; }";
	    String html = String.format("<html><head><link rel=\"stylesheet\" type=\"text/css\" href=\"http://uservoice.com/stylesheets/vendor/typeset.css\"/><style>%s</style></head><body class=\"typeset\" style=\"font-family: sans-serif; margin: 1em\"><h3>%s</h3>%s</body></html>", styles, article.getTitle(), article.getHtml());
		webView.setWebChromeClient(new WebChromeClient());
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setPluginState(PluginState.ON);
		webView.loadUrl(String.format("data:text/html;charset=utf-8,%s", Uri.encode(html)));
	}
	
	@SuppressLint("DefaultLocale")
	public static String getQuantityString(View view, int id, int count) {
		return String.format("%,d %s", count, view.getContext().getResources().getQuantityString(id, count));
	}
	
	private static String buttonName(View view, int count, int verb, int plural) {
		if (count == 0)
			return view.getContext().getString(verb);
		else
			return getQuantityString(view, plural, count);
	}
	
	public static boolean hasActionBar() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
	}
}
