package com.uservoice.uservoicesdk.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.uservoice.uservoicesdk.R;

public class ContactDialog extends AlertDialog {
	
	public ContactDialog(Context context) {
		super(context);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	    ListView listView = new ListView(getContext());

	    LayoutInflater inflater = getLayoutInflater();
	    View view = inflater.inflate(R.layout.contact_layout, null);
	    listView.addHeaderView(view);
	    
	    final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, new String[] {"Browse Knowledge Base", "Browse Ideas"});
		listView.setAdapter(adapter);
	    
		setContentView(listView);
		
	    setTitle("Help & Feedback");
//	    
//	    EditText editText = (EditText) view.findViewById(R.id.contact_us_text);
//	    editText.setOnKeyListener(new View.OnKeyListener() {
//			
//			@Override
//			public boolean onKey(View v, int keyCode, KeyEvent event) {
//				EditText e = (EditText) v;
//				if (e.getText().length() == 0) {
//					adapter.addAll("Browse Knowledge Base", "Browse Ideas");
//					
//				}
//				// TODO Auto-generated method stub
//				return false;
//			}
//		});
	}

}
