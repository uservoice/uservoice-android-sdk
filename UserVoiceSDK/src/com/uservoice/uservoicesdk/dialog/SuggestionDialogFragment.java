package com.uservoice.uservoicesdk.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.uservoice.uservoicesdk.R;
import com.uservoice.uservoicesdk.Session;
import com.uservoice.uservoicesdk.activity.ForumActivity;
import com.uservoice.uservoicesdk.activity.InstantAnswersActivity;
import com.uservoice.uservoicesdk.babayaga.Babayaga;
import com.uservoice.uservoicesdk.deflection.Deflection;
import com.uservoice.uservoicesdk.flow.SigninManager;
import com.uservoice.uservoicesdk.image.ImageCache;
import com.uservoice.uservoicesdk.model.Comment;
import com.uservoice.uservoicesdk.model.Suggestion;
import com.uservoice.uservoicesdk.rest.Callback;
import com.uservoice.uservoicesdk.ui.DefaultCallback;
import com.uservoice.uservoicesdk.ui.PaginatedAdapter;
import com.uservoice.uservoicesdk.ui.PaginationScrollListener;
import com.uservoice.uservoicesdk.ui.Utils;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@SuppressLint("ValidFragment")
public class SuggestionDialogFragment extends DialogFragmentBugfixed {
	private Suggestion suggestion;
	private PaginatedAdapter<Comment> adapter;
	private View headerView;
	private View view;

	public SuggestionDialogFragment(Suggestion suggestion) {
		this.suggestion = suggestion;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		setStyle(STYLE_NO_TITLE, getTheme());
        if (!Utils.isDarkTheme(getActivity())) {
            builder.setInverseBackgroundForced(true);
        }
        view = getActivity().getLayoutInflater().inflate(R.layout.uv_idea_dialog, null);
		headerView = getActivity().getLayoutInflater().inflate(R.layout.uv_idea_dialog_header, null);
		headerView.findViewById(R.id.uv_subscribe).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final DefaultCallback<Suggestion> callback = new DefaultCallback<Suggestion>(getActivity()) {
					@Override
					public void onModel(Suggestion model) {
						if (getActivity() instanceof InstantAnswersActivity)
							Deflection.trackDeflection("subscribed", model);
						suggestionSubscriptionUpdated(model);
					}
				};
				if (suggestion.isSubscribed()) {
					suggestion.unsubscribe(callback);
				} else {
					if (Session.getInstance().getEmail() != null) {
						SigninManager.signinForSubscribe(getActivity(), Session.getInstance().getEmail(), new Runnable() {
							@Override
							public void run() {
								suggestion.subscribe(callback);
							}
						});
					} else {
						SubscribeDialogFragment dialog = new SubscribeDialogFragment(suggestion, SuggestionDialogFragment.this);
						dialog.show(getFragmentManager(), "SubscribeDialogFragment");
					}
				}
			}
		});
		headerView.findViewById(R.id.uv_post_comment).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				CommentDialogFragment dialog = new CommentDialogFragment(suggestion, SuggestionDialogFragment.this);
				dialog.show(getActivity().getSupportFragmentManager(), "CommentDialogFragment");
			}
		});
		ListView listView = (ListView) view.findViewById(R.id.uv_list);
		listView.addHeaderView(headerView);
		displaySuggestion(view, suggestion);
		adapter = getListAdapter();
		listView.setAdapter(adapter);
		listView.setDivider(null);
		listView.setOnScrollListener(new PaginationScrollListener(adapter));
		builder.setView(view);
		builder.setNegativeButton(R.string.uv_close, null);
		Babayaga.track(Babayaga.Event.VIEW_IDEA, suggestion.getId());
		return builder.create();
	}
	
	public void suggestionSubscriptionUpdated(Suggestion model) {
		CheckBox checkbox = (CheckBox) headerView.findViewById(R.id.uv_subscribe_checkbox);
		if (suggestion.isSubscribed()) {
			Toast.makeText(getActivity(), R.string.uv_msg_subscribe_success, Toast.LENGTH_SHORT).show();
			checkbox.setChecked(true);
		} else {
			Toast.makeText(getActivity(), R.string.uv_msg_unsubscribe, Toast.LENGTH_SHORT).show();
			checkbox.setChecked(false);
		}
		displaySuggestion(view, suggestion);
		if (getActivity() instanceof ForumActivity)
			((ForumActivity) getActivity()).suggestionUpdated(model);
	}

	private PaginatedAdapter<Comment> getListAdapter() {
		return new PaginatedAdapter<Comment>(getActivity(), R.layout.uv_comment_item, new ArrayList<Comment>()) {
			@Override
			protected int getTotalNumberOfObjects() {
				return suggestion.getNumberOfComments();
			}

			@Override
			protected void customizeLayout(View view, Comment model) {
				TextView textView = (TextView) view.findViewById(R.id.uv_text);
				textView.setText(model.getText());

				textView = (TextView) view.findViewById(R.id.uv_name);
				textView.setText(model.getUserName());

				textView = (TextView) view.findViewById(R.id.uv_date);
				textView.setText(DateFormat.getDateInstance().format(model.getCreatedAt()));

				ImageView avatar = (ImageView) view.findViewById(R.id.uv_avatar);
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
		suggestion.commentPosted(comment);
		displaySuggestion(view, suggestion);
	}

	private void displaySuggestion(View view, Suggestion suggestion) {
		TextView status = (TextView) view.findViewById(R.id.uv_status);
		TextView responseStatus = (TextView) view.findViewById(R.id.uv_response_status);
		View responseDivider = view.findViewById(R.id.uv_response_divider);
		TextView title = (TextView) view.findViewById(R.id.uv_title);
		
		if (suggestion.isSubscribed()) {
			((CheckBox)view.findViewById(R.id.uv_subscribe_checkbox)).setChecked(true);
		}

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
			responseStatus.setText(String.format(getString(R.string.uv_admin_response_format), suggestion.getStatus().toUpperCase(Locale.getDefault())));
			responseDivider.setBackgroundColor(color);
		}

		title.setText(suggestion.getTitle());
		((TextView) view.findViewById(R.id.uv_text)).setText(suggestion.getText());
		((TextView) view.findViewById(R.id.uv_creator)).setText(String.format(view.getContext().getString(R.string.uv_posted_by_format), suggestion.getCreatorName(), DateFormat.getDateInstance().format(suggestion.getCreatedAt())));

		if (suggestion.getAdminResponseText() == null) {
			view.findViewById(R.id.uv_admin_response).setVisibility(View.GONE);
		} else {
			view.findViewById(R.id.uv_admin_response).setVisibility(View.VISIBLE);
			((TextView) view.findViewById(R.id.uv_admin_name)).setText(suggestion.getAdminResponseUserName());
			((TextView) view.findViewById(R.id.uv_response_date)).setText(DateFormat.getDateInstance().format(suggestion.getAdminResponseCreatedAt()));
			((TextView) view.findViewById(R.id.uv_response_text)).setText(suggestion.getAdminResponseText());
			ImageView avatar = (ImageView) view.findViewById(R.id.uv_admin_avatar);
			ImageCache.getInstance().loadImage(suggestion.getAdminResponseAvatarUrl(), avatar);
		}

		((TextView) view.findViewById(R.id.uv_comment_count)).setText(Utils.getQuantityString(view, R.plurals.uv_comments, suggestion.getNumberOfComments()).toUpperCase(Locale.getDefault()));
		((TextView) view.findViewById(R.id.uv_subscriber_count)).setText(String.format( view.getContext().getResources().getQuantityString (R.plurals.uv_number_of_subscribers_format, suggestion.getNumberOfSubscribers()),
                                                                                        Utils.getQuantityString(view, R.plurals.uv_subscribers, suggestion.getNumberOfSubscribers())));
	}

}
