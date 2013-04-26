package com.uservoice.uservoicesdk.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;

import com.uservoice.uservoicesdk.Session;
import com.uservoice.uservoicesdk.activity.SuggestionActivity;
import com.uservoice.uservoicesdk.model.Suggestion;
import com.uservoice.uservoicesdk.model.User;

public class VoteDialogFragment extends DialogFragment {
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("Vote");
		final Suggestion suggestion = Session.getInstance().getSuggestion();
		CharSequence[] options;
		if (suggestion.getNumberOfVotesByCurrentUser() == 0) {
			options = new CharSequence[] {voteOption(1, "1 Vote"), voteOption(2, "2 Votes"), voteOption(3, "3 Votes")};
		} else {
			options = new CharSequence[] {voteOption(1, "1 Vote"), voteOption(2, "2 Votes"), voteOption(3, "3 Votes"), Html.fromHtml("<font color='red'>Remove Votes</font>")};
		}

		builder.setSingleChoiceItems(options, suggestion.getNumberOfVotesByCurrentUser() - 1, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, int which) {
				suggestion.vote(which == 3 ? 0 : which + 1, new DefaultCallback<Suggestion>(getActivity()) {
					@Override
					public void onModel(Suggestion model) {
						Session.getInstance().setSuggestion(model);
						dialog.dismiss();
						((SuggestionActivity)getActivity()).updateVotes();
					}
				});
			}
		});
		return builder.create();
	}
	
	private CharSequence voteOption(int votes, String string) {
		User user = Session.getInstance().getUser();
		if (user.getNumberOfVotesRemaining() < votes)
			return Html.fromHtml(String.format("<font color='#CCC'>%s</font>", string));
		else
			return string;
	}

}
