package com.uservoice.uservoicesdk.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;

import com.uservoice.uservoicesdk.R;
import com.uservoice.uservoicesdk.model.Suggestion;
import com.uservoice.uservoicesdk.ui.Utils;

@SuppressLint("ValidFragment")
public class SuggestionDialogFragment extends InstantAnswerDialogFragment {
	private final Suggestion suggestion;

	public SuggestionDialogFragment(Suggestion suggestion) {
		this.suggestion = suggestion;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.suggestion_instant_answer_question);
		View view = getActivity().getLayoutInflater().inflate(R.layout.suggestion_layout, null);
		view.findViewById(R.id.suggestion_details_buttons).setVisibility(View.GONE);
		Utils.displaySuggestion(view, suggestion);
		builder.setView(view);
		addButtons(builder);
		return builder.create();
	}
}
