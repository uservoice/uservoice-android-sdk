package com.uservoice.uservoicesdk.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.uservoice.uservoicesdk.R;
import com.uservoice.uservoicesdk.Session;
import com.uservoice.uservoicesdk.model.ClientConfig;
import com.uservoice.uservoicesdk.model.Forum;
import com.uservoice.uservoicesdk.model.Suggestion;
import com.uservoice.uservoicesdk.ui.DefaultCallback;
import com.uservoice.uservoicesdk.ui.ModelAdapter;

public class ForumActivity extends ListActivity {
	
	private List<Suggestion> suggestions;
	private Forum forum;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// setContentView(R.layout.forum_activity);
		
		suggestions = new ArrayList<Suggestion>();
		
		setListAdapter(new ModelAdapter<Suggestion>(this, R.layout.suggestion_item, suggestions) {
			@Override
			protected void customizeLayout(View view, Suggestion model) {
				TextView textView = (TextView) view.findViewById(R.id.suggestion_title);
				textView.setText(model.getTitle());
				
				textView = (TextView) view.findViewById(R.id.suggestion_vote_count);
				textView.setText(String.valueOf(model.getNumberOfVotes()));
				
				textView = (TextView) view.findViewById(R.id.suggestion_status);
				textView.setText(model.getStatus());
			}
		});
		
		loadClientConfig();
	}
	
	private void loadClientConfig() {
		if (Session.getInstance().getClientConfig() != null) {
			loadForum();
			return;
		}
		ClientConfig.loadClientConfig(new DefaultCallback<ClientConfig>(this) {
			@Override
			public void onModel(ClientConfig model) {
				Session.getInstance().setClientConfig(model);
				loadForum();
			}
		});
	}
	
	private void loadForum() {
		if (forum != null) return;
		Forum.loadForum(Session.getInstance().getConfig().getForumId(), new DefaultCallback<Forum>(this) {
			@Override
			public void onModel(Forum model) {
				forum = model;
				loadMoreSuggestions();
			}
		});
	}
	
	private void loadMoreSuggestions() {
		Suggestion.loadSuggestions(forum, 1, new DefaultCallback<List<Suggestion>>(this) {
			@Override
			public void onModel(List<Suggestion> theSuggestions) {
				suggestions.addAll(theSuggestions);
				getModelAdapter().notifyDataSetChanged();
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	protected ModelAdapter<Suggestion> getModelAdapter() {
		return (ModelAdapter<Suggestion>) getListAdapter();
	}
}
