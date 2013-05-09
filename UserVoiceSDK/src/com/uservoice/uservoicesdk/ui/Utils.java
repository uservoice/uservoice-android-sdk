package com.uservoice.uservoicesdk.ui;

import java.text.DateFormat;

import android.annotation.SuppressLint;
import android.net.Uri;
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
	
	public static void displaySuggestion(View view, Suggestion suggestion) {
		((TextView)view.findViewById(R.id.suggestion_details_title)).setText(suggestion.getTitle());
		
		TextView status = (TextView) view.findViewById(R.id.suggestion_details_status);
		if (suggestion.getStatus() != null) {
			status.setVisibility(View.VISIBLE);
			status.setText(Html.fromHtml(String.format("<font color='%s'>%s</font>", suggestion.getStatusColor(), suggestion.getStatus())));
		} else {
			status.setVisibility(View.GONE);
		}
		
		((TextView)view.findViewById(R.id.suggestion_details_text)).setText(suggestion.getText());
		((TextView)view.findViewById(R.id.suggestion_details_creator)).setText(String.format(view.getContext().getString(R.string.posted_by_format), suggestion.getCreatorName(), DateFormat.getDateInstance().format(suggestion.getCreatedAt())));
		
		if (suggestion.getAdminResponseText() == null) {
			view.findViewById(R.id.suggestion_details_admin_response).setVisibility(View.GONE);
		} else {
			view.findViewById(R.id.suggestion_details_admin_response).setVisibility(View.VISIBLE);
			((TextView)view.findViewById(R.id.suggestion_details_admin_name)).setText(suggestion.getAdminResponseUserName());
			((TextView)view.findViewById(R.id.suggestion_details_admin_response_date)).setText(DateFormat.getDateInstance().format(suggestion.getAdminResponseCreatedAt()));
			((TextView)view.findViewById(R.id.suggestion_details_admin_response_text)).setText(suggestion.getAdminResponseText());
		}
		
		ImageView avatar = (ImageView) view.findViewById(R.id.suggestion_details_admin_avatar);
		ImageCache.getInstance().loadImage(suggestion.getAdminResponseAvatarUrl(), avatar);
		
		((TextView)view.findViewById(R.id.suggestion_details_vote_count)).setText(String.format("%s ¥ %s", getQuantityString(view, R.plurals.votes, suggestion.getNumberOfVotes()), getQuantityString(view, R.plurals.comments, suggestion.getNumberOfComments())));
		Button button = (Button) view.findViewById(R.id.vote_button);
		button.setText(buttonName(view, suggestion.getNumberOfVotesByCurrentUser(), R.string.vote_verb, R.plurals.votes_capitalized));
	}
	
	@SuppressLint("DefaultLocale")
	private static String getQuantityString(View view, int id, int count) {
		return String.format("%d %s", count, view.getContext().getResources().getQuantityString(id, count));
	}
	
	private static String buttonName(View view, int count, int verb, int plural) {
		if (count == 0)
			return view.getContext().getString(verb);
		else
			return getQuantityString(view, plural, count);
	}
}
