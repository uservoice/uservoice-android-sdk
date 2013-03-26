package com.uservoice.uvdemo;

import com.uservoice.uservoicesdk.Config;
import com.uservoice.uservoicesdk.UserVoice;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void launchFeedback(View view) {
		Config config = new Config("feedback.uservoice.com", "8L66hVQUQ45TOLl0dA70YA", "a5e6da1827538a27755a4f4de0602e82533aff73");
		UserVoice.launchUserVoice(config, this);
	}

}
