package com.uservoice.uservoicesdk.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.uservoice.uservoicesdk.R;
import com.uservoice.uservoicesdk.babayaga.Babayaga;
import com.uservoice.uservoicesdk.ui.PortalAdapter;

public class PortalActivity extends BaseListActivity implements SearchActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.uv_portal_title);
        getListView().setDivider(null);
        setListAdapter(new PortalAdapter(this));
        getListView().setOnItemClickListener(getModelAdapter());

        Babayaga.track(Babayaga.Event.VIEW_KB);
    }

    @Override
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

    public PortalAdapter getModelAdapter() {
        return (PortalAdapter) getListAdapter();
    }

}
