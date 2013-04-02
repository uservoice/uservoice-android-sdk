package com.uservoice.uservoicesdk.test;

import java.util.List;

import android.test.AndroidTestCase;
import android.util.Log;

import com.uservoice.uservoicesdk.Config;
import com.uservoice.uservoicesdk.Session;
import com.uservoice.uservoicesdk.model.Article;
import com.uservoice.uservoicesdk.model.RequestToken;
import com.uservoice.uservoicesdk.rest.Callback;
import com.uservoice.uservoicesdk.rest.RestResult;

public class ApiTest extends AndroidTestCase {
	
	static boolean done;
	
	public void testArticles() throws Exception {
		
		Session.getInstance().setConfig(new Config("demo.uservoice.com", "pZJocTBPbg5FN4bAwczDLQ", "Q7UKcxRYLlSJN4CxegUYI6t0uprdsSAGthRIDvYmI"));
		
//		RequestToken.getRequestToken(new Callback<RequestToken>() {
//			@Override
//			public void onModel(RequestToken model) {
//				Session.getInstance().setRequestToken(model);
//			}
//			
//			@Override
//			public void onError(RestResult error) {
//				if (error.getException() != null)
//					Log.d("test", "request token " + error.getException().toString());
//				if (error.getObject() != null)
//					Log.d("test", "request token " + error.getObject().toString());
//			}
//		});
		
		Article.loadAll(new Callback<List<Article>>() {
			
			@Override
			public void onModel(List<Article> list) {
				Log.d("test", list.toString());
				done = true;
			}
			
			@Override
			public void onError(RestResult error) {
				if (error.getException() != null)
					Log.d("test", error.getException().toString());
				if (error.getObject() != null)
					Log.d("test", error.getObject().toString());
				done = true;
			}
			
		});
		
		while (!done) {
			Thread.sleep(200);
		}
	}

}
