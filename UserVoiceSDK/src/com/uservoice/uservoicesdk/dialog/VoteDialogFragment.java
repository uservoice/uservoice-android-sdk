package com.uservoice.uservoicesdk.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Html;

import com.uservoice.uservoicesdk.R;
import com.uservoice.uservoicesdk.Session;
import com.uservoice.uservoicesdk.activity.SuggestionActivity;
import com.uservoice.uservoicesdk.model.Suggestion;
import com.uservoice.uservoicesdk.model.User;
import com.uservoice.uservoicesdk.ui.DefaultCallback;

@SuppressLint("DefaultLocale")
public class VoteDialogFragment extends DialogFragment {
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.vote_dialog_title);
		final Suggestion suggestion = Session.getInstance().getSuggestion();
		CharSequence[] options;
		if (suggestion.getNumberOfVotesByCurrentUser() == 0) {
			options = new CharSequence[] {voteOption(1), voteOption(2), voteOption(3)};
		} else {
			options = new CharSequence[] {voteOption(1), voteOption(2), voteOption(3), Html.fromHtml(String.format("<font color='red'>%s</font>", getString(R.string.remove_votes)))};
		}

		builder.setSingleChoiceItems(options, suggestion.getNumberOfVotesByCurrentUser() - 1, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, int which) {
				suggestion.vote(which == 3 ? 0 : which + 1, new DefaultCallback<Suggestion>(getActivity()) {
					@Override
					public void onModel(Suggestion model) {
						Session.getInstance().setSuggestion(model);
						dialog.dismiss();
						((SuggestionActivity) getActivity()).updateView();
					}
				});
			}
		});
		return builder.create();
	}
	
	private CharSequence voteOption(int votes) {
		User user = Session.getInstance().getUser();
		String string = String.format("%d %s", votes, getResources().getQuantityString(R.plurals.votes_capitalized, votes));
		if (user.getNumberOfVotesRemaining() < votes)
			return Html.fromHtml(String.format("<font color='#CCC'>%s</font>", string));
		else
			return string;
	}

}
