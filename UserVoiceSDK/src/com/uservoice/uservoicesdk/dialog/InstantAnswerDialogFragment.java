package com.uservoice.uservoicesdk.dialog;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;

import com.uservoice.uservoicesdk.R;
import com.uservoice.uservoicesdk.activity.ContactActivity;
import com.uservoice.uservoicesdk.ui.ContactAdapter;

public class InstantAnswerDialogFragment extends DialogFragment {

	protected void addButtons(AlertDialog.Builder builder) {
		builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				ContactActivity activity = (ContactActivity) getActivity();
				ContactAdapter adapter = (ContactAdapter) activity.getListAdapter();
				adapter.notHelpful();
			}
		});
		
		builder.setPositiveButton(R.string.very_yes, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				HelpfulDialogFragment helpfulDialog = new HelpfulDialogFragment();
				helpfulDialog.show(getActivity().getFragmentManager(), "HelpfulDialogFragment");
			}
		});
	}

}