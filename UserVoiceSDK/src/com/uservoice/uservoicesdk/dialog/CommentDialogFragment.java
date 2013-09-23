package com.uservoice.uservoicesdk.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.uservoice.uservoicesdk.R;
import com.uservoice.uservoicesdk.Session;
import com.uservoice.uservoicesdk.flow.SigninManager;
import com.uservoice.uservoicesdk.model.Comment;
import com.uservoice.uservoicesdk.model.Suggestion;
import com.uservoice.uservoicesdk.ui.DefaultCallback;
import com.uservoice.uservoicesdk.ui.Utils;

@SuppressLint("ValidFragment")
public class CommentDialogFragment extends DialogFragmentBugfixed {

	private final Suggestion suggestion;
	private final SuggestionDialogFragment suggestionDialog;

	public CommentDialogFragment(Suggestion suggestion, SuggestionDialogFragment suggestionDialog) {
		this.suggestion = suggestion;
		this.suggestionDialog = suggestionDialog;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if (!Utils.isDarkTheme(getActivity())) {
            builder.setInverseBackgroundForced(true);
        }
		builder.setTitle(R.string.uv_post_a_comment);

		View view = getActivity().getLayoutInflater().inflate(R.layout.uv_comment_dialog, null);
		final EditText textField = (EditText) view.findViewById(R.id.uv_comment_edit_text);

		View email = view.findViewById(R.id.uv_email);
		View name = view.findViewById(R.id.uv_name);
		final EditText emailField = (EditText) email.findViewById(R.id.uv_text_field);
		final EditText nameField = (EditText) name.findViewById(R.id.uv_text_field);
		if (Session.getInstance().getUser() != null) {
			email.setVisibility(View.GONE);
			name.setVisibility(View.GONE);
		} else {
			emailField.setText(Session.getInstance().getEmail());
			((TextView) email.findViewById(R.id.uv_header_text)).setText(R.string.uv_your_email_address);
			nameField.setText(Session.getInstance().getName());
			((TextView) name.findViewById(R.id.uv_header_text)).setText(R.string.uv_your_name);
		}

		builder.setView(view);

		builder.setNegativeButton(R.string.uv_cancel, null);

		final Activity context = getActivity();
		builder.setPositiveButton(R.string.uv_post_comment, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				final String text = textField.getText().toString();
				if (!text.trim().isEmpty()) {
					SigninManager.signIn(getActivity(), emailField.getText().toString(), nameField.getText().toString(), new Runnable() {
						@Override
						public void run() {
							Comment.createComment(suggestion, text, new DefaultCallback<Comment>(getActivity()) {
								@Override
								public void onModel(Comment model) {
									Toast.makeText(context, R.string.uv_msg_comment_posted, Toast.LENGTH_SHORT).show();
									suggestionDialog.commentPosted(model);
								}
							});
						}
					});
				}
			}
		});
        AlertDialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        return dialog;
	}

}
