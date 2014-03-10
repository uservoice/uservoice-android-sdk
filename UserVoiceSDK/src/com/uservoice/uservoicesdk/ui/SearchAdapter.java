package com.uservoice.uservoicesdk.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import android.content.Context;
import android.widget.BaseAdapter;

import com.uservoice.uservoicesdk.rest.Callback;
import com.uservoice.uservoicesdk.rest.RestTask;

public abstract class SearchAdapter<T> extends BaseAdapter {

    protected List<T> searchResults = new ArrayList<T>();
    protected boolean searchActive = false;
    protected boolean loading;
    protected Context context;
    protected String currentQuery;
    protected String pendingQuery;
    protected int scope;
    protected SearchTask currentSearch;

    public void performSearch(String query) {
        pendingQuery = query;
        if (query.length() == 0) {
            searchResults = new ArrayList<T>();
            loading = false;
            notifyDataSetChanged();
        } else {
            loading = true;
            notifyDataSetChanged();
            if (currentSearch != null) {
                currentSearch.cancel();
            }
            currentSearch = new SearchTask(query);
            currentSearch.run();
        }
    }

    public void setSearchActive(boolean searchActive) {
        this.searchActive = searchActive;
        loading = false;
        notifyDataSetChanged();
    }

    private class SearchTask extends TimerTask {
        private final String query;
        private boolean stop;
        private RestTask task;

        public SearchTask(String query) {
            this.query = query;
        }

        @Override
        public boolean cancel() {
            stop = true;
            if (task != null) {
                task.cancel(true);
            }
            return true;
        }

        @Override
        public void run() {
            currentQuery = query;
            task = search(query, new DefaultCallback<List<T>>(context) {
                @Override
                public void onModel(List<T> model) {
                    if (!stop) {
                        searchResults = model;
                        loading = false;
                        notifyDataSetChanged();
                        searchResultsUpdated();
                    }
                }
            });
            if (task == null) {
                // can't search
                loading = false;
            }
        }
    }

    protected void searchResultsUpdated() {
    }

    protected boolean shouldShowSearchResults() {
        return searchActive && pendingQuery != null && pendingQuery.length() > 0;
    }

    protected RestTask search(String query, Callback<List<T>> callback) {
        return null;
    }

    public void setScope(int scope) {
        this.scope = scope;
        notifyDataSetChanged();
    }
}
