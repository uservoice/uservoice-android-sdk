package com.uservoice.helpcenter;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.uservoice.uservoicesdk.activity.BaseListActivity;

public class MainActivity extends BaseListActivity {

    private MainAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getListView().setDividerHeight(0);
        View footer = getLayoutInflater().inflate(R.layout.footer, null);
        TextView textView = (TextView) footer.findViewById(R.id.text);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
		getListView().addFooterView(footer, null, false);
		adapter = new MainAdapter(this);
		setListAdapter(adapter);
		getListView().setOnItemClickListener(adapter);
        getListView().setOnItemLongClickListener(adapter);
        getListView().setItemsCanFocus(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (item.getItemId() == R.id.add_account) {
            AccountDialogFragment dialog = new AccountDialogFragment(adapter);
            dialog.show(getSupportFragmentManager(), "AccountDialogFragment");
            return true;
        } else if (item.getItemId() == R.id.about) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.uservoice.com/android")));
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }
}
