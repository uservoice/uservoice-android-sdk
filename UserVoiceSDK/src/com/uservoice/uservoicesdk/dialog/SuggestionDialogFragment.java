package com.uservoice.uservoicesdk.dialog;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.uservoice.uservoicesdk.R;
import com.uservoice.uservoicesdk.image.ImageCache;
import com.uservoice.uservoicesdk.model.Comment;
import com.uservoice.uservoicesdk.model.Suggestion;
import com.uservoice.uservoicesdk.rest.Callback;
import com.uservoice.uservoicesdk.ui.PaginatedAdapter;
import com.uservoice.uservoicesdk.ui.PaginationScrollListener;
import com.uservoice.uservoicesdk.ui.Utils;

@SuppressLint("ValidFragment")
public class SuggestionDialogFragment extends DialogFragment {
	private final Suggestion suggestion;
	private PaginatedAdapter<Comment> adapter;

	public SuggestionDialogFragment(Suggestion suggestion) {
		this.suggestion = suggestion;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		setStyle(STYLE_NO_TITLE, getTheme());
		View view = getActivity().getLayoutInflater().inflate(R.layout.idea_dialog, null);
		View headerView = getActivity().getLayoutInflater().inflate(R.layout.idea_dialog_header, null);
		headerView.findViewById(R.id.subscribe).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			}
		});
		headerView.findViewById(R.id.post_comment).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				CommentDialogFragment dialog = new CommentDialogFragment(suggestion, SuggestionDialogFragment.this);
				dialog.show(getActivity().getSupportFragmentManager(), "CommentDialogFragment");
			}
		});
		ListView listView = (ListView) view.findViewById(R.id.list);
		listView.addHeaderView(headerView);
		displaySuggestion(view, suggestion);
		adapter = getListAdapter();
		listView.setAdapter(adapter);
		listView.setDivider(null);
		listView.setOnScrollListener(new PaginationScrollListener(adapter));
		builder.setView(view);
		builder.setNegativeButton(R.string.close, null);
		return builder.create();
	}

	private PaginatedAdapter<Comment> getListAdapter() {
		return new PaginatedAdapter<Comment>(getActivity(), R.layout.comment_item, new ArrayList<Comment>()) {
			@Override
			protected void search(String query, Callback<List<Comment>> callback) {
			}

			@Override
			protected int getTotalNumberOfObjects() {
				return suggestion.getNumberOfComments();
			}

			@Override
			protected void customizeLayout(View view, Comment model) {
				TextView textView = (TextView) view.findViewById(R.id.text);
				textView.setText(model.getText());

				textView = (TextView) view.findViewById(R.id.name);
				textView.setText(model.getUserName());

				textView = (TextView) view.findViewById(R.id.date);
				textView.setText(DateFormat.getDateInstance().format(model.getCreatedAt()));

				ImageView avatar = (ImageView) view.findViewById(R.id.avatar);
				ImageCache.getInstance().loadImage(model.getAvatarUrl(), avatar);
			}

			@Override
			public boolean isEnabled(int position) {
				return false;
			}

			@Override
			protected void loadPage(int page, Callback<List<Comment>> callback) {
				Comment.loadComments(suggestion, page, callback);
			}
		};
	}
	
	public void commentPosted(Comment comment) {
		adapter.add(0, comment);
	}

	private void displaySuggestion(View view, Suggestion suggestion) {
		TextView status = (TextView) view.findViewById(R.id.status);
		TextView responseStatus = (TextView) view.findViewById(R.id.response_status);
		View responseDivider = view.findViewById(R.id.response_divider);
		TextView title = (TextView) view.findViewById(R.id.title);

		if (suggestion.getStatus() == null) {
			status.setVisibility(View.GONE);
			int defaultColor = Color.DKGRAY;
			responseStatus.setTextColor(defaultColor);
			responseDivider.setBackgroundColor(defaultColor);
		} else {
			int color = Color.parseColor(suggestion.getStatusColor());
			status.setBackgroundColor(color);
			status.setText(suggestion.getStatus());
			responseStatus.setTextColor(color);
			responseStatus.setText(String.format(getString(R.string.admin_response_format), suggestion.getStatus().toUpperCase(Locale.getDefault())));
			responseDivider.setBackgroundColor(color);
		}

		title.setText(suggestion.getTitle());
		((TextView) view.findViewById(R.id.text)).setText(suggestion.getText());
		((TextView) view.findViewById(R.id.creator)).setText(String.format(view.getContext().getString(R.string.posted_by_format), suggestion.getCreatorName(), DateFormat.getDateInstance().format(suggestion.getCreatedAt())));

		if (suggestion.getAdminResponseText() == null) {
			view.findViewById(R.id.admin_response).setVisibility(View.GONE);
		} else {
			view.findViewById(R.id.admin_response).setVisibility(View.VISIBLE);
			((TextView) view.findViewById(R.id.admin_name)).setText(suggestion.getAdminResponseUserName());
			((TextView) view.findViewById(R.id.response_date)).setText(DateFormat.getDateInstance().format(suggestion.getAdminResponseCreatedAt()));
			((TextView) view.findViewById(R.id.response_text)).setText(suggestion.getAdminResponseText());
			ImageView avatar = (ImageView) view.findViewById(R.id.admin_avatar);
			ImageCache.getInstance().loadImage(suggestion.getAdminResponseAvatarUrl(), avatar);
		}

		((TextView) view.findViewById(R.id.comment_count)).setText(Utils.getQuantityString(view, R.plurals.comments, suggestion.getNumberOfComments()).toUpperCase(Locale.getDefault()));
		((TextView) view.findViewById(R.id.subscriber_count)).setText(String.format(getString(R.string.number_of_subscribers_format), Utils.getQuantityString(view, R.plurals.subscribers, suggestion.getNumberOfSubscribers())));
	}

}
