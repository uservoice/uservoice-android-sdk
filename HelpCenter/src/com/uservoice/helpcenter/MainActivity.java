package com.uservoice.helpcenter;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

import com.uservoice.uservoicesdk.activity.BaseListActivity;

public class MainActivity extends BaseListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getListView().setDividerHeight(0);
		TextView footer = new TextView(this);
		footer.setText(R.string.help);
		footer.setPadding(25, 10, 25, 10);
		footer.setTextColor(Color.rgb(119, 119, 119));
		footer.setTextSize(13);
		getListView().addFooterView(footer);
		MainAdapter adapter = new MainAdapter(this);
		setListAdapter(adapter);
		getListView().setOnItemClickListener(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
