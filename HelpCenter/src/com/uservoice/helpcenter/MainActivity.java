package com.uservoice.helpcenter;

import android.graphics.Color;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.uservoice.uservoicesdk.activity.BaseListActivity;

public class MainActivity extends BaseListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getListView().setDividerHeight(0);
        View footer = getLayoutInflater().inflate(R.layout.footer, null);
        TextView textView = (TextView) footer.findViewById(R.id.text);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
		getListView().addFooterView(footer, null, false);
		MainAdapter adapter = new MainAdapter(this);
		setListAdapter(adapter);
		getListView().setOnItemClickListener(adapter);
        getListView().setItemsCanFocus(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
