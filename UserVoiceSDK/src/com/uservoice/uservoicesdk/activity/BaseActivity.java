package com.uservoice.uservoicesdk.activity;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.ViewFlipper;

import com.uservoice.uservoicesdk.R;
import com.uservoice.uservoicesdk.ui.MixedSearchAdapter;
import com.uservoice.uservoicesdk.ui.PortalAdapter;
import com.uservoice.uservoicesdk.ui.SearchAdapter;
import com.uservoice.uservoicesdk.ui.SearchExpandListener;
import com.uservoice.uservoicesdk.ui.SearchQueryListener;

public class BaseActivity extends FragmentActivity {

    protected Tab allTab;
    protected Tab articlesTab;
    protected Tab ideasTab;
    private int originalNavigationMode = -1;
    protected MixedSearchAdapter searchAdapter;

    @Override
    @SuppressLint("NewApi")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (hasActionBar()) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public SearchAdapter<?> getSearchAdapter() {
        return searchAdapter;
    }

    @SuppressLint("NewApi")
    protected void setupScopedSearch(Menu menu) {
        if (hasActionBar()) {
            menu.findItem(R.id.uv_action_search).setOnActionExpandListener(new SearchExpandListener((SearchActivity) this));
            SearchView search = (SearchView) menu.findItem(R.id.uv_action_search).getActionView();
            search.setOnQueryTextListener(new SearchQueryListener((SearchActivity) this));
            searchAdapter = new MixedSearchAdapter(this);
            ListView searchView = new ListView(this);
            searchView.setAdapter(searchAdapter);
            searchView.setOnItemClickListener(searchAdapter);
            ViewFlipper viewFlipper = (ViewFlipper) findViewById(R.id.uv_view_flipper);
            viewFlipper.addView(searchView, 1);

            ActionBar.TabListener listener = new ActionBar.TabListener() {
                @Override
                public void onTabUnselected(Tab tab, FragmentTransaction ft) {
                }

                @Override
                public void onTabSelected(Tab tab, FragmentTransaction ft) {
                    searchAdapter.setScope((Integer) tab.getTag());
                }

                @Override
                public void onTabReselected(Tab tab, FragmentTransaction ft) {
                }
            };
            allTab = getActionBar().newTab().setText(getString(R.string.uv_all_results_filter)).setTabListener(listener).setTag(PortalAdapter.SCOPE_ALL);
            getActionBar().addTab(allTab);
            articlesTab = getActionBar().newTab().setText(getString(R.string.uv_articles_filter)).setTabListener(listener).setTag(PortalAdapter.SCOPE_ARTICLES);
            getActionBar().addTab(articlesTab);
            ideasTab = getActionBar().newTab().setText(getString(R.string.uv_ideas_filter)).setTabListener(listener).setTag(PortalAdapter.SCOPE_IDEAS);
            getActionBar().addTab(ideasTab);
        } else {
            menu.findItem(R.id.uv_action_search).setVisible(false);
        }
    }

    @SuppressLint("NewApi")
    public void updateScopedSearch(int results, int articleResults, int ideaResults) {
        if (hasActionBar()) {
            allTab.setText(String.format("%s (%d)", getString(R.string.uv_all_results_filter), results));
            articlesTab.setText(String.format("%s (%d)", getString(R.string.uv_articles_filter), articleResults));
            ideasTab.setText(String.format("%s (%d)", getString(R.string.uv_ideas_filter), ideaResults));
        }
    }

    @SuppressLint("NewApi")
    public void showSearch() {
        ViewFlipper viewFlipper = (ViewFlipper) findViewById(R.id.uv_view_flipper);
        viewFlipper.setDisplayedChild(1);
        if (hasActionBar()) {
            if (originalNavigationMode == -1)
                originalNavigationMode = getActionBar().getNavigationMode();
            getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        }
    }

    @SuppressLint("NewApi")
    public void hideSearch() {
        ViewFlipper viewFlipper = (ViewFlipper) findViewById(R.id.uv_view_flipper);
        viewFlipper.setDisplayedChild(0);
        if (hasActionBar()) {
            getActionBar().setNavigationMode(originalNavigationMode == -1 ? ActionBar.NAVIGATION_MODE_STANDARD : originalNavigationMode);
        }
    }

    @SuppressLint("NewApi")
    public boolean hasActionBar() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB && getActionBar() != null;
    }
}
