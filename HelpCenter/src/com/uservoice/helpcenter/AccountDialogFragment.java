package com.uservoice.helpcenter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.EditText;

@SuppressLint("ValidFragment")
public class AccountDialogFragment extends DialogFragment {
	
	private final MainAdapter adapter;

	public AccountDialogFragment(MainAdapter adapter) {
		this.adapter = adapter;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.account_dialog_title);
		View view = getActivity().getLayoutInflater().inflate(R.layout.account_dialog, null);
		final EditText subdomainField = (EditText) view.findViewById(R.id.subdomain);
		builder.setView(view);
		builder.setNegativeButton(android.R.string.cancel, null);
		builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, int which) {
				String subdomain = subdomainField.getText().toString();
				if (subdomain.indexOf('.') == -1) {
					subdomain += ".uservoice.com";
				}
				adapter.addAccount(subdomain);
			}
		});
		return builder.create();
	}

}
