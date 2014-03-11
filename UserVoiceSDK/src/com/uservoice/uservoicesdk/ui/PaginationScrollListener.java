package com.uservoice.uservoicesdk.ui;

import android.widget.AbsListView;

public class PaginationScrollListener implements AbsListView.OnScrollListener {

    private final PaginatedAdapter<?> adapter;

    public PaginationScrollListener(PaginatedAdapter<?> adapter) {
        this.adapter = adapter;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (firstVisibleItem + visibleItemCount >= totalItemCount) {
            adapter.loadMore();
        }
    }

}
