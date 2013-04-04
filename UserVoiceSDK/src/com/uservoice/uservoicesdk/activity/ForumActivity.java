package com.uservoice.uservoicesdk.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.TextView;

import com.uservoice.uservoicesdk.R;
import com.uservoice.uservoicesdk.Session;
import com.uservoice.uservoicesdk.model.ClientConfig;
import com.uservoice.uservoicesdk.model.Forum;
import com.uservoice.uservoicesdk.model.Suggestion;
import com.uservoice.uservoicesdk.ui.DefaultCallback;
import com.uservoice.uservoicesdk.ui.ModelAdapter;

public class ForumActivity extends ListActivity implements OnScrollListener {
	
	private List<Suggestion> suggestions;
	private Forum forum;
	private int pageToLoad = 1;
	private boolean moreToLoad = true;
	
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
				if (model.getStatus() == null) {
					textView.setVisibility(View.GONE);
				} else {
					textView.setVisibility(View.VISIBLE);
					textView.setText(Html.fromHtml(String.format("<font color='%s'>%s</font>", model.getStatusColor(), model.getStatus())));
				}
			}
		});
		
		getListView().setOnScrollListener(this);
		
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
		if (forum == null || Session.getInstance().getClientConfig() == null) return;
		if (getModelAdapter().isLoading()) return;
		getModelAdapter().setLoading(true);
		Suggestion.loadSuggestions(forum, pageToLoad, new DefaultCallback<List<Suggestion>>(this) {
			@Override
			public void onModel(List<Suggestion> theSuggestions) {
				suggestions.addAll(theSuggestions);
				pageToLoad += 1;
				if (suggestions.size() == forum.getNumberOfOpenSuggestions())
					moreToLoad = false;
				getModelAdapter().setLoading(false);
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	protected ModelAdapter<Suggestion> getModelAdapter() {
		return (ModelAdapter<Suggestion>) getListAdapter();
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		if (moreToLoad && firstVisibleItem + visibleItemCount >= totalItemCount) {
			loadMoreSuggestions();
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
	}
}
