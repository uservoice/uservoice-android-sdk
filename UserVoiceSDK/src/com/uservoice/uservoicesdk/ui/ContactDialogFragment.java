package com.uservoice.uservoicesdk.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.uservoice.uservoicesdk.R;

public class ContactDialogFragment extends DialogFragment {
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
	    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    
	    // TODO I think I need to go ahead and subclass Dialog for this to work properly
	    ListView listView = new ListView(getActivity());

	    LayoutInflater inflater = getActivity().getLayoutInflater();
	    View view = inflater.inflate(R.layout.contact_layout, null);
	    listView.addHeaderView(view);
	    
	    listView.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, new String[] {"Browse Knowledge Base", "Browse Ideas"}));
	    
		builder.setView(listView);
		
	    builder.setTitle("Help & Feedback");
//	    builder.setNegativeButton("Cancel", null);
//	    builder.setPositiveButton("Go", null);
	    Dialog dialog = builder.create();
	    return dialog;
	}
	
}
