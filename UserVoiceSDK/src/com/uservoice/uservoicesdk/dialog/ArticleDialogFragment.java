package com.uservoice.uservoicesdk.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.webkit.WebView;

import com.uservoice.uservoicesdk.R;
import com.uservoice.uservoicesdk.activity.InstantAnswersActivity;
import com.uservoice.uservoicesdk.babayaga.Babayaga;
import com.uservoice.uservoicesdk.deflection.Deflection;
import com.uservoice.uservoicesdk.model.Article;
import com.uservoice.uservoicesdk.ui.InstantAnswersAdapter;
import com.uservoice.uservoicesdk.ui.Utils;

@SuppressLint({"ValidFragment", "NewApi"})
public class ArticleDialogFragment extends DialogFragmentBugfixed {

    private final Article article;
    private WebView webView;
    private String deflectingType;

    public ArticleDialogFragment(Article article, String deflectingType) {
        this.article = article;
        this.deflectingType = deflectingType;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.uv_article_instant_answer_question);

        webView = new WebView(getActivity());
        if (!Utils.isDarkTheme(getActivity())) {
            builder.setInverseBackgroundForced(true);
        }
        builder.setView(webView);
        Utils.displayArticle(webView, article, getActivity());

        builder.setNegativeButton(R.string.uv_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (getActivity() instanceof InstantAnswersActivity) {
                    Deflection.trackDeflection("unhelpful", deflectingType, article);
                    InstantAnswersActivity activity = (InstantAnswersActivity) getActivity();
                    InstantAnswersAdapter adapter = (InstantAnswersAdapter) activity.getListAdapter();
                    adapter.notHelpful();
                } else {
                    UnhelpfulDialogFragment dialogFragment = new UnhelpfulDialogFragment();
                    dialogFragment.show(getActivity().getSupportFragmentManager(), "UnhelpfulDialogFragment");
                }
            }
        });

        builder.setPositiveButton(R.string.uv_very_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Babayaga.track(Babayaga.Event.VOTE_ARTICLE, article.getId());
                if (getActivity() instanceof InstantAnswersActivity) {
                    Deflection.trackDeflection("helpful", deflectingType, article);
                    HelpfulDialogFragment helpfulDialog = new HelpfulDialogFragment();
                    helpfulDialog.show(getActivity().getSupportFragmentManager(), "HelpfulDialogFragment");
                }
            }
        });

        Babayaga.track(Babayaga.Event.VIEW_ARTICLE, article.getId());
        return builder.create();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        webView.onPause();
        webView.loadUrl("about:blank");
        super.onDismiss(dialog);
    }
}
