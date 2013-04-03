package com.uservoice.uservoicesdk.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;

import com.uservoice.uservoicesdk.rest.Callback;
import com.uservoice.uservoicesdk.rest.RestResult;

public abstract class DefaultCallback<T> extends Callback<T> {
	
	private static final String TAG = "com.uservoice.uservoicesdk";
	
	private final Context context;

	public DefaultCallback(Context context) {
		this.context = context;
	}

	@Override
	public void onError(RestResult error) {
		Log.e(TAG, error.getMessage());
		new AlertDialog.Builder(context).setTitle("There was an error connecting to UserVoice").show();
	}

}
