package com.uservoice.uservoicesdk.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.uservoice.uservoicesdk.R;
import com.uservoice.uservoicesdk.Session;
import com.uservoice.uservoicesdk.babayaga.Babayaga;
import com.uservoice.uservoicesdk.dialog.UnhelpfulDialogFragment;
import com.uservoice.uservoicesdk.model.Article;
import com.uservoice.uservoicesdk.ui.Utils;

public class ArticleActivity extends BaseActivity implements SearchActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.uv_article_layout);
        final Article article = Session.getInstance().getArticle();
        setTitle(article.getTitle());
        WebView webView = (WebView) findViewById(R.id.uv_webview);
        Utils.displayArticle(webView, article, this);
        findViewById(R.id.uv_container).setBackgroundColor(Utils.isDarkTheme(this) ? Color.BLACK : Color.WHITE);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                findViewById(R.id.uv_helpful_section).setVisibility(View.VISIBLE);
            }
        });
        findViewById(R.id.uv_helpful_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Babayaga.track(Babayaga.Event.VOTE_ARTICLE, article.getId());
                Toast.makeText(ArticleActivity.this, R.string.uv_thanks, Toast.LENGTH_SHORT).show();
            }
        });
        findViewById(R.id.uv_unhelpful_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UnhelpfulDialogFragment dialog = new UnhelpfulDialogFragment();
                dialog.show(getSupportFragmentManager(), "UnhelpfulDialogFragment");
            }
        });

        Babayaga.track(Babayaga.Event.VIEW_ARTICLE, article.getId());
    }

    @Override
    @SuppressLint("NewApi")
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.uv_portal, menu);
        setupScopedSearch(menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (item.getItemId() == R.id.uv_action_contact) {
            startActivity(new Intent(this, ContactActivity.class));
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    public void finish() {
        // This is what you have to do to make it stop the flash player
        WebView webview = (WebView) findViewById(R.id.uv_webview);
        webview.loadData("", "text/html", "utf-8");
        super.finish();
    }
}
