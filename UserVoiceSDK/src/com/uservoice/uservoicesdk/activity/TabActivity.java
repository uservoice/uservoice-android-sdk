package com.uservoice.uservoicesdk.activity;

import com.uservoice.uservoicesdk.R;
import com.uservoice.uservoicesdk.ui.ForumFragment;
import com.uservoice.uservoicesdk.ui.KnowledgeBaseFragment;
import com.uservoice.uservoicesdk.ui.TabListener;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

public class TabActivity extends Activity {


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    // setup action bar for tabs
	    ActionBar actionBar = getActionBar();
	    actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

	    Tab tab = actionBar.newTab()
	            .setText("Knowledge Base")
	            .setTabListener(new TabListener<KnowledgeBaseFragment>(this, "kb", KnowledgeBaseFragment.class));
	    actionBar.addTab(tab);
	    
	    tab = actionBar.newTab().setText("Feedback Forum").setTabListener(new TabListener<ForumFragment>(this, "forum", ForumFragment.class));
	    actionBar.addTab(tab);
	    
	    setTitle("Help");
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.uv_main, menu);
		return true;
	}
}
