package com.uservoice.uservoicesdk.ui;

import com.uservoice.uservoicesdk.rest.RestResult;

public interface SigninCallback {
	void onSignIn();
	void onError(RestResult error);
}
