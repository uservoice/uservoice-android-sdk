package com.uservoice.uservoicesdk.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.webkit.WebView;

import com.uservoice.uservoicesdk.R;
import com.uservoice.uservoicesdk.activity.InstantAnswersActivity;
import com.uservoice.uservoicesdk.model.Article;
import com.uservoice.uservoicesdk.ui.InstantAnswersAdapter;
import com.uservoice.uservoicesdk.ui.Utils;

@SuppressLint("ValidFragment")
public class ArticleDialogFragment extends DialogFragment {
	
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
		
		builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				InstantAnswersActivity activity = (InstantAnswersActivity) getActivity();
				InstantAnswersAdapter adapter = (InstantAnswersAdapter) activity.getListAdapter();
				adapter.notHelpful();
			}
		});
		
		builder.setPositiveButton(R.string.very_yes, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				HelpfulDialogFragment helpfulDialog = new HelpfulDialogFragment();
				helpfulDialog.show(getActivity().getSupportFragmentManager(), "HelpfulDialogFragment");
			}
		});
		return builder.create();
	}
}
