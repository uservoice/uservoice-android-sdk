package com.uservoice.uservoicesdk.test;

import java.util.List;

import android.test.AndroidTestCase;
import android.util.Log;

import com.uservoice.uservoicesdk.Config;
import com.uservoice.uservoicesdk.Session;
import com.uservoice.uservoicesdk.model.Article;
import com.uservoice.uservoicesdk.model.RequestToken;
import com.uservoice.uservoicesdk.model.Ticket;
import com.uservoice.uservoicesdk.model.User;
import com.uservoice.uservoicesdk.rest.Callback;
import com.uservoice.uservoicesdk.rest.RestResult;

public class ApiTest extends AndroidTestCase {
	
	private boolean done;
	private List<Article> articles;
	private Ticket ticket;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		Session.getInstance().setConfig(new Config("demo.uservoice.com", "pZJocTBPbg5FN4bAwczDLQ", "Q7UKcxRYLlSJN4CxegUYI6t0uprdsSAGthRIDvYmI"));
	} 

	public void testArticles() throws Exception {
		Article.loadAll(new TestCallback<List<Article>>() {
			@Override
			public void onModel(List<Article> list) {
				articles = list;
				done = true;
			}
		});
		
		waitForTask();
		
		assertNotNull(articles);
		assertTrue(articles.size() > 0);
	}
	
	private void createTicket() {
		Ticket.createTicket("Test message", null, new TestCallback<Ticket>() {
			@Override
			public void onModel(Ticket model) {
				ticket = model;
				done = true;
			}
		});
	}
	
	private void findUser() {
		User.findOrCreate("test@example.com", "Testy McTest", new TestCallback<User>() {
			@Override
			public void onModel(User user) {
				Session.getInstance().setUser(user);
				createTicket();
			};
		});
	}
	
	private void loadRequestToken() {
		RequestToken.getRequestToken(new TestCallback<RequestToken>() {
			@Override
			public void onModel(RequestToken model) {
				Session.getInstance().setRequestToken(model);
				findUser();
			}
		});
	}
	
	public void testUsers() throws Exception {
		loadRequestToken();
		waitForTask();
		assertNotNull(Session.getInstance().getRequestToken());
		assertNotNull(Session.getInstance().getAccessToken());
		assertNotNull(Session.getInstance().getUser());
		assertNotNull(ticket);
		Log.d("test", String.format("ticket id: %d", ticket.getId()));
	}
	
	private void waitForTask() throws InterruptedException {
		while (!done) {
			Thread.sleep(200);
		}
	}
	
	private abstract class TestCallback<T> extends Callback<T> {
		@Override
		public void onError(RestResult error) {
			if (error.getException() != null)
				Log.d("test", error.getException().toString());
			if (error.getObject() != null)
				Log.d("test", error.getObject().toString());
			done = true;
		}
	}
}
