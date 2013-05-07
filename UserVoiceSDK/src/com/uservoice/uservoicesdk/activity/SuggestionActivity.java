package com.uservoice.uservoicesdk.activity;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.uservoice.uservoicesdk.R;
import com.uservoice.uservoicesdk.Session;
import com.uservoice.uservoicesdk.babayaga.Babayaga;
import com.uservoice.uservoicesdk.dialog.VoteDialogFragment;
import com.uservoice.uservoicesdk.flow.SigninManager;
import com.uservoice.uservoicesdk.image.ImageCache;
import com.uservoice.uservoicesdk.model.Comment;
import com.uservoice.uservoicesdk.model.Suggestion;
import com.uservoice.uservoicesdk.rest.Callback;
import com.uservoice.uservoicesdk.ui.PaginatedAdapter;
import com.uservoice.uservoicesdk.ui.PaginationScrollListener;

@SuppressLint("DefaultLocale")
public class SuggestionActivity extends ListActivity {

	private static int POST_COMMENT = 1;
	
	private View headerView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		headerView = getLayoutInflater().inflate(R.layout.suggestion_layout, null);
		getListView().addHeaderView(headerView);
		
		setTitle(R.string.title_idea);
		
		setListAdapter(new PaginatedAdapter<Comment>(this, R.layout.comment_item, new ArrayList<Comment>()) {

			@Override
			protected void search(String query, Callback<List<Comment>> callback) {
			}

			@Override
			protected int getTotalNumberOfObjects() {
				return Session.getInstance().getSuggestion().getNumberOfComments();
			}

			@Override
			protected void customizeLayout(View view, Comment model) {
				TextView textView = (TextView) view.findViewById(R.id.comment_text);
				textView.setText(model.getText());

				textView = (TextView) view.findViewById(R.id.comment_name);
				textView.setText(model.getUserName());

				textView = (TextView) view.findViewById(R.id.comment_date);
				textView.setText(DateFormat.getDateInstance().format(model.getCreatedAt()));
				
				ImageView avatar = (ImageView) view.findViewById(R.id.comment_avatar);
				ImageCache.getInstance().loadImage(model.getAvatarUrl(), avatar);
			}
			
			@Override
			public boolean isEnabled(int position) {
				return false;
			}

			@Override
			protected void loadPage(int page, Callback<List<Comment>> callback) {
				Comment.loadComments(Session.getInstance().getSuggestion(), page, callback);
			}
		});
		
		updateView();
		getListView().setOnScrollListener(new PaginationScrollListener(getModelAdapter()));
		
		findViewById(R.id.comment_button).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				SigninManager.signIn(SuggestionActivity.this, new Runnable() {
					@Override
					public void run() {
						startActivityForResult(new Intent(SuggestionActivity.this, CommentActivity.class), POST_COMMENT);
					}
				});
			}
		});
		
		findViewById(R.id.vote_button).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				SigninManager.signIn(SuggestionActivity.this, new Runnable() {
					@Override
					public void run() {
						VoteDialogFragment dialog = new VoteDialogFragment();
						dialog.show(getFragmentManager(), "VoteDialogFragment");
					}
				});
			}
		});
		
		Babayaga.track(Babayaga.Event.VIEW_IDEA);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
	    }
	    return super.onOptionsItemSelected(item);
	}
	
	public void updateView() {
		Suggestion suggestion = Session.getInstance().getSuggestion();
		
		getTextView(R.id.suggestion_details_title).setText(suggestion.getTitle());
		
		TextView status = getTextView(R.id.suggestion_details_status);
		if (suggestion.getStatus() != null) {
			status.setVisibility(View.VISIBLE);
			status.setText(Html.fromHtml(String.format("<font color='%s'>%s</font>", suggestion.getStatusColor(), suggestion.getStatus())));
		} else {
			status.setVisibility(View.GONE);
		}
		
		getTextView(R.id.suggestion_details_text).setText(suggestion.getText());
		getTextView(R.id.suggestion_details_creator).setText(String.format(getString(R.string.posted_by_format), suggestion.getCreatorName(), DateFormat.getDateInstance().format(suggestion.getCreatedAt())));
		
		if (suggestion.getAdminResponseText() == null) {
			headerView.findViewById(R.id.suggestion_details_admin_response).setVisibility(View.GONE);
		} else {
			headerView.findViewById(R.id.suggestion_details_admin_response).setVisibility(View.VISIBLE);
			getTextView(R.id.suggestion_details_admin_name).setText(suggestion.getAdminResponseUserName());
			getTextView(R.id.suggestion_details_admin_response_date).setText(DateFormat.getDateInstance().format(suggestion.getAdminResponseCreatedAt()));
			getTextView(R.id.suggestion_details_admin_response_text).setText(suggestion.getAdminResponseText());
		}
		
		ImageView avatar = (ImageView) headerView.findViewById(R.id.suggestion_details_admin_avatar);
		ImageCache.getInstance().loadImage(suggestion.getAdminResponseAvatarUrl(), avatar);
		updateVotes();
	}
	
	public void updateVotes() {
		Suggestion suggestion = Session.getInstance().getSuggestion();
		getTextView(R.id.suggestion_details_vote_count).setText(String.format("%s ¥ %s", getQuantityString(R.plurals.votes, suggestion.getNumberOfVotes()), getQuantityString(R.plurals.comments, suggestion.getNumberOfComments())));
		Button button = (Button) findViewById(R.id.vote_button);
		button.setText(buttonName(suggestion.getNumberOfVotesByCurrentUser(), R.string.vote_verb, R.plurals.votes_capitalized));
	}
	
	private String getQuantityString(int id, int count) {
		return String.format("%d %s", count, getResources().getQuantityString(id, count));
	}
	
	private String buttonName(int count, int verb, int plural) {
		if (count == 0)
			return getString(verb);
		else
			return getQuantityString(plural, count);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == POST_COMMENT) {
			Comment comment = Session.getInstance().getComment();
			if (comment != null) {
				Session.getInstance().setComment(null);
				getModelAdapter().add(0, comment);
			}
		}
	}
	
	private TextView getTextView(int id) {
		return (TextView) headerView.findViewById(id);
	}


	@SuppressWarnings("unchecked")
	protected PaginatedAdapter<Comment> getModelAdapter() {
		return (PaginatedAdapter<Comment>) getListAdapter();
	}

}
