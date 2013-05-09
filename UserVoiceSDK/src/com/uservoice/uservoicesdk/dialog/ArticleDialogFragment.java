package com.uservoice.uservoicesdk.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.webkit.WebView;

import com.uservoice.uservoicesdk.R;
import com.uservoice.uservoicesdk.model.Article;
import com.uservoice.uservoicesdk.ui.Utils;

@SuppressLint("ValidFragment")
public class ArticleDialogFragment extends InstantAnswerDialogFragment {
	
	private final Article article;

	public ArticleDialogFragment(Article article) {
		this.article = article;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.article_instant_answer_question);
		
		WebView webView = new WebView(getActivity());
		builder.setView(webView);
		Utils.displayArticle(webView, article);
		
		addButtons(builder);
		return builder.create();
	}
}
