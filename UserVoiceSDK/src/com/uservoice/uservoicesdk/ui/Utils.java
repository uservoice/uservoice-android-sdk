package com.uservoice.uservoicesdk.ui;

import java.util.Locale;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.util.TypedValue;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.graphics.Color;

import com.uservoice.uservoicesdk.R;
import com.uservoice.uservoicesdk.Session;
import com.uservoice.uservoicesdk.activity.TopicActivity;
import com.uservoice.uservoicesdk.dialog.ArticleDialogFragment;
import com.uservoice.uservoicesdk.dialog.SuggestionDialogFragment;
import com.uservoice.uservoicesdk.model.Article;
import com.uservoice.uservoicesdk.model.BaseModel;
import com.uservoice.uservoicesdk.model.Suggestion;
import com.uservoice.uservoicesdk.model.Topic;

public class Utils {

	@SuppressLint("SetJavaScriptEnabled")
	public static void displayArticle(WebView webView, Article article, Context context) {
		String styles = "iframe, img { width: 100%; }";
		if (isDarkTheme(context)) {
			webView.setBackgroundColor(Color.BLACK);
			styles += "body { background-color: #000000; color: #F6F6F6; } a { color: #0099FF; }";
		}
		String html = String.format("<html><head><meta charset=\"utf-8\"><link rel=\"stylesheet\" type=\"text/css\" href=\"http://cdn.uservoice.com/stylesheets/vendor/typeset.css\"/><style>%s</style></head><body class=\"typeset\" style=\"font-family: sans-serif; margin: 1em\"><h3>%s</h3>%s</body></html>", styles, article.getTitle(), article.getHtml());
		webView.setWebChromeClient(new WebChromeClient());
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setPluginState(PluginState.ON);
		webView.loadUrl(String.format("data:text/html;charset=utf-8,%s", Uri.encode(html)));
	}

    public static boolean isDarkTheme(Context context) {
        TypedValue tv = new TypedValue();
        float [] hsv =  new float[3];
        context.getTheme().resolveAttribute(android.R.attr.textColorPrimary, tv, true);
        Color.colorToHSV(context.getResources().getColor(tv.resourceId), hsv);
        return hsv[2] > 0.5f;
    }

	@SuppressLint("DefaultLocale")
	public static String getQuantityString(View view, int id, int count) {
		return String.format("%,d %s", count, view.getContext().getResources().getQuantityString(id, count));
	}

	public static void displayInstantAnswer(View view, BaseModel model) {
		TextView title = (TextView) view.findViewById(R.id.uv_title);
		TextView detail = (TextView) view.findViewById(R.id.uv_detail);
		View suggestionDetails = view.findViewById(R.id.uv_suggestion_details);
		ImageView image = (ImageView) view.findViewById(R.id.uv_icon);
		if (model instanceof Article) {
			Article article = (Article) model;
			image.setImageResource(R.drawable.uv_article);
			title.setText(article.getTitle());
			if (article.getTopicName() != null) {
				detail.setVisibility(View.VISIBLE);
				detail.setText(article.getTopicName());
			} else {
				detail.setVisibility(View.GONE);
			}
			suggestionDetails.setVisibility(View.GONE);
		} else if (model instanceof Suggestion) {
			Suggestion suggestion = (Suggestion) model;
			image.setImageResource(R.drawable.uv_idea);
			title.setText(suggestion.getTitle());
			detail.setVisibility(View.VISIBLE);
			detail.setText(suggestion.getForumName());
			if (suggestion.getStatus() != null) {
				View statusColor = suggestionDetails.findViewById(R.id.uv_suggestion_status_color);
				TextView status = (TextView) suggestionDetails.findViewById(R.id.uv_suggestion_status);
				int color = Color.parseColor(suggestion.getStatusColor());
				suggestionDetails.setVisibility(View.VISIBLE);
				status.setText(suggestion.getStatus().toUpperCase(Locale.getDefault()));
				status.setTextColor(color);
				statusColor.setBackgroundColor(color);
			} else {
				suggestionDetails.setVisibility(View.GONE);
			}

		}
	}

	public static void showModel(FragmentActivity context, BaseModel model) {
		if (model instanceof Article) {
			ArticleDialogFragment fragment = new ArticleDialogFragment((Article) model);
			fragment.show(context.getSupportFragmentManager(), "ArticleDialogFragment");
		} else if (model instanceof Suggestion) {
			SuggestionDialogFragment fragment = new SuggestionDialogFragment((Suggestion) model);
			fragment.show(context.getSupportFragmentManager(), "SuggestionDialogFragment");
		} else if (model instanceof Topic) {
			Session.getInstance().setTopic((Topic) model);
			context.startActivity(new Intent(context, TopicActivity.class));
		}
	}
}
