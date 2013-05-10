package com.uservoice.uservoicesdk.ui;

import java.util.Arrays;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.uservoice.uservoicesdk.R;
import com.uservoice.uservoicesdk.Session;
import com.uservoice.uservoicesdk.babayaga.Babayaga;
import com.uservoice.uservoicesdk.babayaga.Babayaga.Event;
import com.uservoice.uservoicesdk.model.Category;
import com.uservoice.uservoicesdk.model.Suggestion;

public class PostIdeaAdapter extends InstantAnswersAdapter {
	
	private static int DESCRIPTION = 8;
	private static int CATEGORY = 9;
	private static int HELP = 10;
	
	private Spinner categorySelect;
	private EditText descriptionField;

	public PostIdeaAdapter(Activity context) {
		super(context);
	}
	
	@Override
	public int getViewTypeCount() {
		return super.getViewTypeCount() + 3;
	}
	
	@Override
	protected List<Integer> getDetailRows() {
		return Arrays.asList(DESCRIPTION, CATEGORY, SPACE, EMAIL_FIELD, NAME_FIELD);
	}
	
	@Override
	protected List<Integer> getRows() {
		List<Integer> rows = super.getRows();
		rows.add(0, HEADING);
		if (state == State.DETAILS)
			rows.add(HELP);
		return rows;
	}
	
	@Override
	@SuppressLint("CutPasteId")
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		int type = getItemViewType(position);
		if (view == null) {
			if (type == DESCRIPTION) {
				view = inflater.inflate(R.layout.text_field_item, null);
				TextView title = (TextView) view.findViewById(R.id.header_text);
				descriptionField = (EditText) view.findViewById(R.id.text_field);
				title.setText(R.string.idea_description_heading);
				descriptionField.setHint(R.string.idea_description_hint);
			} else if (type == CATEGORY) {
				view = inflater.inflate(R.layout.select_field_item, null);
				TextView title = (TextView) view.findViewById(R.id.header_text);
				categorySelect = (Spinner) view.findViewById(R.id.select_field);
				categorySelect.setAdapter(new ArrayAdapter<Category>(context, android.R.layout.simple_list_item_1, Session.getInstance().getForum().getCategories()));
				title.setText(R.string.category);
			} else if (type == HELP) {
				view = inflater.inflate(R.layout.idea_help_item, null);
			} else {
				view = super.getView(position, convertView, parent);
			}
		}

		if (type == DESCRIPTION || type == CATEGORY || type == HELP) {
			// just skip the else
		} else if (type == HEADING) {
			TextView textView = (TextView) view.findViewById(R.id.header_text);
			textView.setText(position == 0 ? R.string.idea_text_heading : R.string.do_any_of_these_help);
		} else if (type == TEXT) {
			TextView textView = (TextView) view.findViewById(R.id.text);
			textView.setHint(R.string.idea_text_hint);
		} else {
			return super.getView(position, convertView, parent);
		}
		return view;
	}

	@Override
	protected void doSubmit() {
		Category category = (Category) categorySelect.getSelectedItem();
		Suggestion.createSuggestion(Session.getInstance().getForum(), category, textField.getText().toString(), descriptionField.getText().toString(), 1, new DefaultCallback<Suggestion>(context) {
			@Override
			public void onModel(Suggestion model) {
				Babayaga.track(Event.SUBMIT_IDEA);
				Toast.makeText(context, R.string.msg_idea_created, Toast.LENGTH_SHORT).show();
				context.finish();
			}
		});
	}

	@Override
	protected String getSubmitString() {
		return context.getString(R.string.submit_idea);
	}

}
