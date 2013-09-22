package com.uservoice.uservoicesdk.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.uservoice.uservoicesdk.R;
import com.uservoice.uservoicesdk.Session;
import com.uservoice.uservoicesdk.activity.InstantAnswersActivity;
import com.uservoice.uservoicesdk.deflection.Deflection;
import com.uservoice.uservoicesdk.flow.SigninManager;
import com.uservoice.uservoicesdk.model.Suggestion;
import com.uservoice.uservoicesdk.ui.DefaultCallback;
import com.uservoice.uservoicesdk.ui.Utils;

@SuppressLint("ValidFragment")
public class SubscribeDialogFragment extends DialogFragmentBugfixed {
	
	private final Suggestion suggestion;
	private final SuggestionDialogFragment suggestionDialog;

	public SubscribeDialogFragment(Suggestion suggestion, SuggestionDialogFragment suggestionDialog) {
		this.suggestion = suggestion;
		this.suggestionDialog = suggestionDialog;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.uv_subscribe_dialog_title);
        if (!Utils.isDarkTheme(getActivity())) {
            builder.setInverseBackgroundForced(true);
        }
		View view = getActivity().getLayoutInflater().inflate(R.layout.uv_subscribe_dialog, null);
		final EditText emailField = (EditText) view.findViewById(R.id.uv_email);
		emailField.setText(Session.getInstance().getEmail());
		builder.setView(view);
		builder.setNegativeButton(R.string.uv_nevermind, null);
		builder.setPositiveButton(R.string.uv_subscribe, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, int which) {
				Session.getInstance().persistIdentity(Session.getInstance().getName(), emailField.getText().toString());
				SigninManager.signinForSubscribe(getActivity(), Session.getInstance().getEmail(), new Runnable() {
					@Override
					public void run() {
						suggestion.subscribe(new DefaultCallback<Suggestion>(getActivity()) {
							@Override
							public void onModel(Suggestion model) {
								if (getActivity() instanceof InstantAnswersActivity)
									Deflection.trackDeflection("subscribed", model);
								suggestionDialog.suggestionSubscriptionUpdated(model);
								dialog.dismiss();
							};
						});
					}
				});
			}
		});
        AlertDialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        return dialog;
	}


}
