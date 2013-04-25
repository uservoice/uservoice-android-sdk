package com.uservoice.uservoicesdk.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.text.Html;

import com.uservoice.uservoicesdk.Session;
import com.uservoice.uservoicesdk.model.User;

public class VoteDialogFragment extends DialogFragment {
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("Vote");
		// TODO change this to use a list adapter probably
		builder.setSingleChoiceItems(new CharSequence[] {voteOption(1, "1 Vote"), voteOption(2, "2 Votes"), voteOption(3, "3 Votes"), Html.fromHtml("<font color='red'>Remove Votes</font>")}, Session.getInstance().getSuggestion().getNumberOfVotesByCurrentUser() - 1, null);
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
