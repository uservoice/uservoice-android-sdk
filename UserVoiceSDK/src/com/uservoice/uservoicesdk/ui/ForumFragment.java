package com.uservoice.uservoicesdk.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.uservoice.uservoicesdk.R;
import com.uservoice.uservoicesdk.Session;
import com.uservoice.uservoicesdk.activity.SuggestionActivity;
import com.uservoice.uservoicesdk.model.ClientConfig;
import com.uservoice.uservoicesdk.model.Forum;
import com.uservoice.uservoicesdk.model.Suggestion;
import com.uservoice.uservoicesdk.rest.Callback;

public class ForumFragment extends ListFragment {

	protected Forum forum;
	
	public ForumFragment() {
		loadClientConfig();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setListAdapter(new PaginatedAdapter<Suggestion>(getActivity(), R.layout.suggestion_item, new ArrayList<Suggestion>()) {
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

			@Override
			public void loadPage(int page, Callback<List<Suggestion>> callback) {
				Suggestion.loadSuggestions(forum, page, callback);
			}
			
			@Override
			public void search(String query, Callback<List<Suggestion>> callback) {
				Suggestion.searchSuggestions(forum, query, callback);
			}

			@Override
			public int getTotalNumberOfObjects() {
				return forum.getNumberOfOpenSuggestions();
			}
		});
	}
	
	@Override
	public void onStart() {
		super.onStart();
		getListView().setOnScrollListener(new AbsListView.OnScrollListener() {
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				if (firstVisibleItem + visibleItemCount >= totalItemCount && forum != null && Session.getInstance().getClientConfig() != null) {
					getModelAdapter().loadMore();
				}
			}

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}
		});
		
		getListView().setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Suggestion suggestion = (Suggestion) getModelAdapter().getItem(position);
				Session.getInstance().setSuggestion(suggestion);
				Intent intent = new Intent(getActivity(), SuggestionActivity.class);
				getActivity().startActivity(intent);
			}
		});
	}
	
	private void loadClientConfig() {
		if (Session.getInstance().getClientConfig() != null) {
			loadForum();
			return;
		}
		ClientConfig.loadClientConfig(new DefaultCallback<ClientConfig>(getActivity()) {
			@Override
			public void onModel(ClientConfig model) {
				Session.getInstance().setClientConfig(model);
				loadForum();
			}
		});
	}
	
	private void loadForum() {
		if (forum != null) return;
		Forum.loadForum(Session.getInstance().getConfig().getForumId(), new DefaultCallback<Forum>(getActivity()) {
			@Override
			public void onModel(Forum model) {
				forum = model;
				if (getView() != null)
					getModelAdapter().loadMore();
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	protected PaginatedAdapter<Suggestion> getModelAdapter() {
		return (PaginatedAdapter<Suggestion>) getListAdapter();
	}



}
