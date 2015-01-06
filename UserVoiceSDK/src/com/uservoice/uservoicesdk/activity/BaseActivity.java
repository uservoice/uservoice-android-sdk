package com.uservoice.uservoicesdk.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

import com.uservoice.uservoicesdk.Session;
import com.uservoice.uservoicesdk.babayaga.Babayaga;
import com.uservoice.uservoicesdk.ui.MixedSearchAdapter;

public class BaseActivity extends ActionBarActivity {

    protected ActionBar.Tab allTab;
    protected ActionBar.Tab articlesTab;
    protected ActionBar.Tab ideasTab;
    protected MixedSearchAdapter searchAdapter;
    protected ActionBar actionBar;

    @Override
    @SuppressLint("NewApi")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (hasActionBar()) {
            actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        Session.getInstance().setContext(getApplicationContext());
        Babayaga.setContext(getApplicationContext());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("NewApi")
    public boolean hasActionBar() {
        return getSupportActionBar() != null;
    }
}
