package com.uservoice.uservoicesdk.activity;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.uservoice.uservoicesdk.R;
import com.uservoice.uservoicesdk.Session;
import com.uservoice.uservoicesdk.babayaga.Babayaga;
import com.uservoice.uservoicesdk.model.Article;
import com.uservoice.uservoicesdk.model.Topic;
import com.uservoice.uservoicesdk.rest.Callback;
import com.uservoice.uservoicesdk.ui.PaginatedAdapter;
import com.uservoice.uservoicesdk.ui.PaginationScrollListener;

public class TopicActivity extends SearchActivity {

    @SuppressLint({"InlinedApi", "NewApi"})
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Topic topic = getIntent().getParcelableExtra("topic");
        if (hasActionBar()) {
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
            ArrayAdapter<Topic> topicAdapter = new ArrayAdapter<Topic>(actionBar.getThemedContext(),
                    R.layout.support_simple_spinner_dropdown_item, Session.getInstance().getTopics());
            actionBar.setListNavigationCallbacks(topicAdapter, new ActionBar.OnNavigationListener() {
                @Override
                @SuppressWarnings("unchecked")
                public boolean onNavigationItemSelected(int itemPosition, long itemId) {
                    Topic topic = Session.getInstance().getTopics().get(itemPosition);
                    getIntent().putExtra("topic", topic);
                    getModelAdapter().reload();
                    return true;
                }
            });
            topicAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
            for (int i=0; i<Session.getInstance().getTopics().size(); i++) {
                Topic t = Session.getInstance().getTopics().get(i);
                if (t.getId() == topic.getId()) {
                    actionBar.setSelectedNavigationItem(i);
                }
            }
        }

        setTitle(null);
        getListView().setDivider(null);
        setListAdapter(new PaginatedAdapter<Article>(this, R.layout.uv_text_item, new ArrayList<Article>()) {
            @Override
            protected void loadPage(int page, Callback<List<Article>> callback) {
                Topic topic = getIntent().getParcelableExtra("topic");
                if (topic.getId() == -1) {
                    Article.loadPage(page, callback);
                } else {
                    Article.loadPageForTopic(topic.getId(), page, callback);
                }
            }

            @Override
            public int getTotalNumberOfObjects() {
                Topic topic = getIntent().getParcelableExtra("topic");
                if (topic.getId() == -1) {
                    return -1; // we don't know. keep trying to load more.
                } else {
                    return topic.getNumberOfArticles();
                }
            }

            @Override
            protected void customizeLayout(View view, Article model) {
                Topic topic = getIntent().getParcelableExtra("topic");
                TextView text = (TextView) view.findViewById(R.id.uv_text);
                TextView text2 = (TextView) view.findViewById(R.id.uv_text2);
                text.setText(model.getTitle());
                if (topic.getId() == -1 && model.getTopicName() != null) {
                    text2.setVisibility(View.VISIBLE);
                    text2.setText(model.getTopicName());
                } else {
                    text2.setVisibility(View.GONE);
                }
            }
        });

        getListView().setOnScrollListener(new PaginationScrollListener(getModelAdapter()));

        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Article article = (Article) getListAdapter().getItem(position);
                Intent intent = new Intent(TopicActivity.this, ArticleActivity.class);
                intent.putExtra("article", article);
                startActivity(intent);
            }
        });

        Babayaga.track(Babayaga.Event.VIEW_TOPIC, topic.getId());
    }

    @Override
    public void hideSearch() {
        super.hideSearch();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.uv_portal, menu);
        setupScopedSearch(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.uv_action_contact) {
            startActivity(new Intent(this, ContactActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("unchecked")
    public PaginatedAdapter<Article> getModelAdapter() {
        return (PaginatedAdapter<Article>) getListAdapter();
    }
}
