package com.uservoice.uservoicesdk.activity;

import android.annotation.SuppressLint;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.ViewFlipper;

import com.uservoice.uservoicesdk.R;
import com.uservoice.uservoicesdk.compatibility.FragmentListActivity;
import com.uservoice.uservoicesdk.ui.MixedSearchAdapter;
import com.uservoice.uservoicesdk.ui.PortalAdapter;
import com.uservoice.uservoicesdk.ui.SearchAdapter;
import com.uservoice.uservoicesdk.ui.SearchExpandListener;
import com.uservoice.uservoicesdk.ui.SearchQueryListener;

@SuppressLint("NewApi")
public abstract class SearchActivity extends FragmentListActivity {
    private int originalNavigationMode = -1;

    public SearchAdapter<?> getSearchAdapter() {
        return searchAdapter;
    }

    public void updateScopedSearch(int results, int articleResults, int ideaResults) {
        if (hasActionBar()) {
            allTab.setText(String.format("%s (%d)", getString(R.string.uv_all_results_filter), results));
            articlesTab.setText(String.format("%s (%d)", getString(R.string.uv_articles_filter), articleResults));
            ideasTab.setText(String.format("%s (%d)", getString(R.string.uv_ideas_filter), ideaResults));
        }
    }

    public void showSearch() {
        ViewFlipper viewFlipper = (ViewFlipper) findViewById(R.id.uv_view_flipper);
        viewFlipper.setDisplayedChild(1);
        if (hasActionBar()) {
            if (originalNavigationMode == -1)
                originalNavigationMode = actionBar.getNavigationMode();
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        }
    }

    public void hideSearch() {
        ViewFlipper viewFlipper = (ViewFlipper) findViewById(R.id.uv_view_flipper);
        viewFlipper.setDisplayedChild(0);
        if (hasActionBar()) {
            actionBar.setNavigationMode(originalNavigationMode == -1 ? ActionBar.NAVIGATION_MODE_STANDARD : originalNavigationMode);
        }
    }

    protected void setupScopedSearch(Menu menu) {
        MenuItem searchItem = menu.findItem(R.id.uv_action_search);
        if (hasActionBar()) {
            MenuItemCompat.setOnActionExpandListener(searchItem, new SearchExpandListener(this));
            SearchView search = (SearchView) MenuItemCompat.getActionView(searchItem);
            search.setOnQueryTextListener(new SearchQueryListener(this));
            searchAdapter = new MixedSearchAdapter(this);
            ListView searchView = new ListView(this);
            searchView.setAdapter(searchAdapter);
            searchView.setOnItemClickListener(searchAdapter);

            // ensure that the viewflipper is set up
            getListView();

            ViewFlipper viewFlipper = (ViewFlipper) findViewById(R.id.uv_view_flipper);
            viewFlipper.addView(searchView, 1);

            ActionBar.TabListener listener = new ActionBar.TabListener() {
                @Override
                public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
                }

                @Override
                public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                    searchAdapter.setScope((Integer) tab.getTag());
                }

                @Override
                public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
                }
            };
            allTab = actionBar.newTab().setText(getString(R.string.uv_all_results_filter)).setTabListener(listener).setTag(PortalAdapter.SCOPE_ALL);
            actionBar.addTab(allTab);
            articlesTab = actionBar.newTab().setText(getString(R.string.uv_articles_filter)).setTabListener(listener).setTag(PortalAdapter.SCOPE_ARTICLES);
            actionBar.addTab(articlesTab);
            ideasTab = actionBar.newTab().setText(getString(R.string.uv_ideas_filter)).setTabListener(listener).setTag(PortalAdapter.SCOPE_IDEAS);
            actionBar.addTab(ideasTab);
        } else {
            searchItem.setVisible(false);
        }
    }
}
