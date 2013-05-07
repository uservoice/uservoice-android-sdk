package com.uservoice.uservoicesdk.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.uservoice.uservoicesdk.R;
import com.uservoice.uservoicesdk.Session;
import com.uservoice.uservoicesdk.model.Article;
import com.uservoice.uservoicesdk.model.BaseModel;
import com.uservoice.uservoicesdk.model.CustomField;
import com.uservoice.uservoicesdk.model.Suggestion;

public class ContactAdapter extends BaseAdapter {
	
	private int TEXT = 0;
	private int BUTTON = 1;
	private int HEADING = 2;
	private int LOADING = 3;
	private int INSTANT_ANSWER = 4;
	private int MORE_RESULTS = 5;
	private int EMAIL_FIELD = 6;
	private int NAME_FIELD = 7;
	private int CUSTOM_TEXT_FIELD = 8;
	private int CUSTOM_PREDEFINED_FIELD = 9;
	
	private enum State {
		INIT, INIT_LOADING, INSTANT_ANSWERS, DETAILS, DETAILS_LOADING
	}
	
	private State state = State.INIT;
	private List<BaseModel> instantAnswers;
	private Context context;
	private LayoutInflater inflater;
	private EditText textField;
	private Map<String,String> customFieldValues;
	
	public ContactAdapter(Context context) {
		this.context = context;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		customFieldValues = new HashMap<String,String>(Session.getInstance().getConfig().getCustomFields());
	}
	
	private List<Integer> getRows() {
		List<Integer> rows = new ArrayList<Integer>();
		rows.add(TEXT);
		if (state == State.DETAILS_LOADING || (state != State.INIT && state != State.INIT_LOADING && !instantAnswers.isEmpty()))
			rows.add(HEADING);
		if (state == State.DETAILS_LOADING)
			rows.add(LOADING);
		if (state == State.INSTANT_ANSWERS || state == State.DETAILS) {
			if (instantAnswers.size() > 0)
				rows.add(INSTANT_ANSWER);
			if (instantAnswers.size() > 1)
				rows.add(INSTANT_ANSWER);
			if (instantAnswers.size() > 2)
				rows.add(MORE_RESULTS);
		}
		if (state == State.DETAILS || state == State.DETAILS_LOADING) {
			rows.add(EMAIL_FIELD);
			rows.add(NAME_FIELD);
			for (CustomField customField : Session.getInstance().getClientConfig().getCustomFields()) {
				if (customField.isPredefined())
					rows.add(CUSTOM_PREDEFINED_FIELD);
				else
					rows.add(CUSTOM_TEXT_FIELD);
			}
		}
		rows.add(BUTTON);
		return rows;
	}
	
	@Override
	public int getCount() {
		return getRows().size();
	}

	@Override
	public Object getItem(int position) {
		int type = getItemViewType(position);
		if (type == INSTANT_ANSWER) {
			return instantAnswers.get(position - 2);
		} else if (type == CUSTOM_PREDEFINED_FIELD || type == CUSTOM_TEXT_FIELD) {
			int offset = state == State.DETAILS_LOADING ? 5 : ((instantAnswers.isEmpty() ? 3 : 4) + Math.min(3, instantAnswers.size()));
			return Session.getInstance().getClientConfig().getCustomFields().get(position - offset);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public int getItemViewType(int position) {
		return getRows().get(position);
	}
	
	@Override
	public int getViewTypeCount() {
		return 10;
	}
	
	private void onButtonTapped() {
		if (state == State.INIT) {
			if (textField.getText().toString().trim().isEmpty())
				return;
			state = State.INIT_LOADING;
			notifyDataSetChanged();
			Article.loadInstantAnswers(textField.getText().toString().trim(), new DefaultCallback<List<BaseModel>>(context) {
				@Override
				public void onModel(List<BaseModel> model) {
					instantAnswers = model;
					if (instantAnswers.isEmpty())
						state = State.DETAILS;
					else
						state = State.INSTANT_ANSWERS;
					notifyDataSetChanged();
				}
			});
		} else if (state == State.INSTANT_ANSWERS) {
			state = State.DETAILS;
			notifyDataSetChanged();
		} else {
			// submit ticket
		}
	}
	
	@Override
	public boolean isEnabled(int position) {
		int type = getItemViewType(position);
		return type == INSTANT_ANSWER || type == MORE_RESULTS;
	}

	@SuppressLint("CutPasteId")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		int type = getItemViewType(position);
		if (view == null) {
			if (type == LOADING) {
				view = inflater.inflate(R.layout.loading_item, null);
			} else if (type == BUTTON) {
				view = inflater.inflate(R.layout.contact_button_item, null);
				Button button = (Button) view.findViewById(R.id.contact_button);
				button.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						onButtonTapped();
					}
				});
			} else if (type == HEADING) {
				view = inflater.inflate(R.layout.header_item, null);
				TextView textView = (TextView) view.findViewById(R.id.header_text);
				textView.setText(R.string.do_any_of_these_help);
			} else if (type == INSTANT_ANSWER) {
				view = inflater.inflate(R.layout.instant_answer_item, null);
			} else if (type == TEXT) {
				view = inflater.inflate(R.layout.contact_text_item, null);
				textField = (EditText) view.findViewById(R.id.text);
				// TODO maybe attach listener to reload IAs after edit
			} else if (type == MORE_RESULTS) {
				view = inflater.inflate(android.R.layout.simple_list_item_1, null);
				TextView textView = (TextView) view.findViewById(android.R.id.text1);
				textView.setText(R.string.more_results);
			} else if (type == EMAIL_FIELD || type == NAME_FIELD || type == CUSTOM_TEXT_FIELD) {
				view = inflater.inflate(R.layout.text_field_item, null);
			} else if (type == CUSTOM_PREDEFINED_FIELD) {
				view = inflater.inflate(R.layout.select_field_item, null);
			}
		}
		
		if (type == BUTTON) {
			Button button = (Button) view.findViewById(R.id.contact_button);
			button.setEnabled(state != State.INIT_LOADING);
			switch (state) {
			case INIT:
				button.setText(R.string.next);
				break;
			case INIT_LOADING:
				button.setText(R.string.loading);
				break;
			case INSTANT_ANSWERS:
				button.setText(R.string.none_of_these_help);
				break;
			case DETAILS:
			case DETAILS_LOADING:
				button.setText(R.string.send_message);
				break;
			}
		} else if (type == INSTANT_ANSWER) {
			TextView title = (TextView) view.findViewById(R.id.title);
			TextView detail = (TextView) view.findViewById(R.id.detail);
			BaseModel model = (BaseModel) getItem(position);
			String[] details = null;
			if (model instanceof Article) {
				Article article = (Article) model;
				title.setText(article.getTitle());
				if (article.getTopicName() != null)
					details = new String[] { context.getString(R.string.article), article.getTopicName() };
				else
					details = new String[] { context.getString(R.string.article) };
				detail.setText(String.format("%s - %s", context.getString(R.string.article), article.getTopicName()));
			} else if (model instanceof Suggestion) {
				Suggestion suggestion = (Suggestion) model;
				title.setText(suggestion.getTitle());
				if (suggestion.getCategory() != null)
					details = new String[] { context.getString(R.string.idea), suggestion.getCategory().getName() };
				else
					details = new String[] { context.getString(R.string.idea) };
			}
			if (details.length == 2)
				detail.setText(String.format("%s - %s", details[0], details[1]));
			else
				detail.setText(details[0]);
		} else if (type == EMAIL_FIELD || type == NAME_FIELD || type == CUSTOM_TEXT_FIELD) {
			TextView title = (TextView) view.findViewById(R.id.header_text);
			final EditText field = (EditText) view.findViewById(R.id.text_field);
			if (type == EMAIL_FIELD) {
				title.setText(R.string.your_email_address);
				field.setHint(R.string.email_address);
				field.setInputType(EditorInfo.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
			} else if (type == NAME_FIELD) {
				title.setText(R.string.your_name);
				field.setHint(R.string.name);
				field.setInputType(EditorInfo.TYPE_TEXT_VARIATION_PERSON_NAME);
				// TODO set saved value
			} else if (type == CUSTOM_TEXT_FIELD) {
				final CustomField customField = (CustomField) getItem(position);
				String value = customFieldValues.get(customField.getName());
				title.setText(customField.getName());
				field.setHint(R.string.value);
				field.setInputType(EditorInfo.TYPE_TEXT_VARIATION_SHORT_MESSAGE);
				field.setText(value);
				field.setOnFocusChangeListener(new View.OnFocusChangeListener() {
					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						if (!hasFocus) {
							customFieldValues.put(customField.getName(), field.getText().toString());
						}
					}
				});
			}
		} else if (type == CUSTOM_PREDEFINED_FIELD) {
			final CustomField customField = (CustomField) getItem(position);
			String value = customFieldValues.get(customField.getName());
			TextView title = (TextView) view.findViewById(R.id.header_text);
			title.setText(customField.getName());
			Spinner field = (Spinner) view.findViewById(R.id.select_field);
			field.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
					customFieldValues.put(customField.getName(), customField.getPredefinedValues().get(position));
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {}
			});
			field.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, customField.getPredefinedValues()));
			if (value != null && customField.getPredefinedValues().contains(value))
				field.setSelection(customField.getPredefinedValues().indexOf(value));
		}
		return view;
	}

}
