package com.uservoice.uservoicesdk.activity;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.uservoice.uservoicesdk.R;
import com.uservoice.uservoicesdk.Session;
import com.uservoice.uservoicesdk.babayaga.Babayaga;
import com.uservoice.uservoicesdk.compatibility.FragmentListActivity;
import com.uservoice.uservoicesdk.dialog.SubscribeDialogFragment;
import com.uservoice.uservoicesdk.flow.SigninManager;
import com.uservoice.uservoicesdk.image.ImageCache;
import com.uservoice.uservoicesdk.model.Comment;
import com.uservoice.uservoicesdk.model.Suggestion;
import com.uservoice.uservoicesdk.rest.Callback;
import com.uservoice.uservoicesdk.ui.PaginatedAdapter;
import com.uservoice.uservoicesdk.ui.PaginationScrollListener;
import com.uservoice.uservoicesdk.ui.Utils;

@SuppressLint("DefaultLocale")
public class SuggestionActivity extends FragmentListActivity {

	private static int POST_COMMENT = 1;
	
	private View headerView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
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
						SubscribeDialogFragment dialog = new SubscribeDialogFragment();
						dialog.show(getSupportFragmentManager(), "SubscribeDialogFragment");
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
		Utils.displaySuggestion(headerView, suggestion);
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

	@SuppressWarnings("unchecked")
	protected PaginatedAdapter<Comment> getModelAdapter() {
		return (PaginatedAdapter<Comment>) getListAdapter();
	}

}
