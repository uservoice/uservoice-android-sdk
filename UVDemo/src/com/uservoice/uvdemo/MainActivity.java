package com.uservoice.uvdemo;

import java.lang.reflect.Field;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;

import com.uservoice.uservoicesdk.Config;
import com.uservoice.uservoicesdk.UserVoice;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// hack to always show the overflow menu in the action bar
	    try {
	        ViewConfiguration config = ViewConfiguration.get(this);
	        Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
	        if(menuKeyField != null) {
	            menuKeyField.setAccessible(true);
	            menuKeyField.setBoolean(config, false);
	        }
	    } catch (Exception ex) {
	        // Ignore
	    }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void launchFeedback() {
//		Config config = new Config("feedback.uservoice.com", "8L66hVQUQ45TOLl0dA70YA", "a5e6da1827538a27755a4f4de0602e82533aff73");
		Config config = new Config("demo.uservoice.com", "pZJocTBPbg5FN4bAwczDLQ", "Q7UKcxRYLlSJN4CxegUYI6t0uprdsSAGthRIDvYmI");
//		config.setTopicId(9579);
		UserVoice.launchUserVoice(config, this);
	}
	
	public void launchFeedback(MenuItem menuItem) {
		launchFeedback();
	}

	public void launchFeedback(View view) {
		launchFeedback();
	}

}
