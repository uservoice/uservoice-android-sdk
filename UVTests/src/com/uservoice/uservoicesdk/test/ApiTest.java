package com.uservoice.uservoicesdk.test;

import java.util.List;

import android.test.AndroidTestCase;

import com.uservoice.uservoicesdk.model.Article;
import com.uservoice.uservoicesdk.rest.Callback;
import com.uservoice.uservoicesdk.rest.RestResult;

public class ApiTest extends AndroidTestCase {
	
	public void testArticles() throws Exception {
		Article.loadAll(new Callback<List<Article>>() {
			
			@Override
			public void onModel(List<Article> list) {
				System.out.println(list);
			}
			
			@Override
			public void onError(RestResult error) {
				System.out.println(error);
			}
			
		});
	}

}
