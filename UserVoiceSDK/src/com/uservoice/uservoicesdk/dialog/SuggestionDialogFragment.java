package com.uservoice.uservoicesdk.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.TextView;

import com.uservoice.uservoicesdk.R;
import com.uservoice.uservoicesdk.model.Suggestion;
import com.uservoice.uservoicesdk.ui.Utils;

@SuppressLint("ValidFragment")
public class SuggestionDialogFragment extends DialogFragment {
	private final Suggestion suggestion;

	public SuggestionDialogFragment(Suggestion suggestion) {
		this.suggestion = suggestion;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		setStyle(STYLE_NO_TITLE, getTheme());

		View view = getActivity().getLayoutInflater().inflate(R.layout.idea_dialog, null);
		TextView statusView = (TextView) view.findViewById(R.id.status);
		TextView titleView = (TextView) view.findViewById(R.id.title);
		View divider = view.findViewById(R.id.divider);
		
		if (suggestion.getStatus() == null) {
			statusView.setVisibility(View.GONE);
			int defaultColor = Color.DKGRAY;
			titleView.setTextColor(defaultColor);
			divider.setBackgroundColor(defaultColor);
		} else {
			int color = Color.parseColor(suggestion.getStatusColor());
			statusView.setBackgroundColor(color);
			statusView.setText(String.format(getString(R.string.status_format), suggestion.getStatus()));
			titleView.setTextColor(color);
			divider.setBackgroundColor(color);
		}
		titleView.setText(suggestion.getTitle());
		
		Utils.displaySuggestion(view, suggestion);
		view.findViewById(R.id.suggestion_details_buttons).setVisibility(View.GONE);
		view.findViewById(R.id.suggestion_details_title).setVisibility(View.GONE);
		view.findViewById(R.id.suggestion_details_status).setVisibility(View.GONE);
		view.findViewById(R.id.suggestion_details_text).setPadding(0, 0, 0, 0);
		view.findViewById(R.id.idea_content).setBackgroundColor(Color.WHITE);
		builder.setView(view);
		builder.setNegativeButton(R.string.close, null);
		return builder.create();
	}
}
