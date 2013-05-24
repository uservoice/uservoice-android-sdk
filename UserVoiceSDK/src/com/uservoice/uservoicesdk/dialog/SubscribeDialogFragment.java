package com.uservoice.uservoicesdk.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.uservoice.uservoicesdk.R;
import com.uservoice.uservoicesdk.Session;
import com.uservoice.uservoicesdk.flow.SigninManager;
import com.uservoice.uservoicesdk.model.Suggestion;
import com.uservoice.uservoicesdk.ui.DefaultCallback;

@SuppressLint("ValidFragment")
public class SubscribeDialogFragment extends DialogFragment {
	
	private final Suggestion suggestion;
	private final SuggestionDialogFragment suggestionDialog;

	public SubscribeDialogFragment(Suggestion suggestion, SuggestionDialogFragment suggestionDialog) {
		this.suggestion = suggestion;
		this.suggestionDialog = suggestionDialog;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.subscribe_dialog_title);
		View view = getActivity().getLayoutInflater().inflate(R.layout.subscribe_dialog, null);
		TextView text = (TextView) view.findViewById(R.id.header_text);
		text.setText(R.string.your_email_address);
		final EditText emailField = (EditText) view.findViewById(R.id.email);
		emailField.setText(Session.getInstance().getEmail());
		builder.setView(view);
		builder.setNegativeButton(R.string.nevermind, null);
		builder.setPositiveButton(R.string.subscribe, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, int which) {
				Session.getInstance().persistIdentity(Session.getInstance().getName(), emailField.getText().toString());
				SigninManager.signIn(getActivity(), new Runnable() {
					@Override
					public void run() {
						suggestion.vote(1, new DefaultCallback<Suggestion>(getActivity()) {
							@Override
							public void onModel(Suggestion model) {
								suggestionDialog.suggestionSubscriptionUpdated(model);
								dialog.dismiss();
							}
						});
					}
				});
//				suggestion.subscribe(emailField.getText().toString(), new DefaultCallback<Suggestion>(getActivity()) {
//					@Override
//					public void onModel(Suggestion model) {
//						suggestionDialog.suggestionSubscriptionUpdated(model);
//						dialog.dismiss();
//					};
//				});
			}
		});
		return builder.create();
	}


}
