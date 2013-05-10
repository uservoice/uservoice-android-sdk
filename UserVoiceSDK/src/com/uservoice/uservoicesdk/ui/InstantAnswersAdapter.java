package com.uservoice.uservoicesdk.ui;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.uservoice.uservoicesdk.R;
import com.uservoice.uservoicesdk.dialog.ArticleDialogFragment;
import com.uservoice.uservoicesdk.dialog.SuggestionDialogFragment;
import com.uservoice.uservoicesdk.model.Article;
import com.uservoice.uservoicesdk.model.BaseModel;
import com.uservoice.uservoicesdk.model.Suggestion;

public abstract class InstantAnswersAdapter extends BaseAdapter implements ViewGroup.OnHierarchyChangeListener, OnItemClickListener {

	protected enum State {
		INIT, INIT_LOADING, INSTANT_ANSWERS, DETAILS
	}

	protected int TEXT = 0;
	protected int BUTTON = 1;
	protected int HEADING = 2;
	protected int LOADING = 3;
	protected int INSTANT_ANSWER = 4;
	protected int EMAIL_FIELD = 5;
	protected int NAME_FIELD = 6;
	protected int SPACE = 7;

	protected State state = State.INIT;
	protected List<BaseModel> instantAnswers;
	protected Activity context;
	protected LayoutInflater inflater;
	protected EditText textField;
	protected EditText emailField;
	protected EditText nameField;

	public InstantAnswersAdapter(Activity context) {
		this.context = context;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	protected abstract List<Integer> getDetailRows();

	protected abstract void doSubmit();

	protected abstract String getSubmitString();
	
	@Override
	public int getViewTypeCount() {
		return 8;
	}
	
	protected List<Integer> getRows() {
		List<Integer> rows = new ArrayList<Integer>();
		rows.add(TEXT);
		if (state != State.INIT && state != State.INIT_LOADING && !instantAnswers.isEmpty()) {
			rows.add(SPACE);
			rows.add(HEADING);
		}
		if (state == State.INSTANT_ANSWERS || state == State.DETAILS) {
			if (instantAnswers.size() > 0)
				rows.add(INSTANT_ANSWER);
			if (instantAnswers.size() > 1)
				rows.add(INSTANT_ANSWER);
			if (instantAnswers.size() > 2)
				rows.add(INSTANT_ANSWER);
		}
		if (state == State.DETAILS) {
			rows.add(SPACE);
			rows.addAll(getDetailRows());
		}
		rows.add(BUTTON);
		return rows;
	}

	@Override
	public int getCount() {
		return getRows().size();
	}

	@Override
	public int getItemViewType(int position) {
		return getRows().get(position);
	}

	public void notHelpful() {
		if (state == State.INSTANT_ANSWERS) {
			state = State.DETAILS;
			notifyDataSetChanged();
		}
	}

	@Override
	public boolean isEnabled(int position) {
		int type = getItemViewType(position);
		return type == INSTANT_ANSWER;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		int type = getItemViewType(position);
		if (type == INSTANT_ANSWER) {
			BaseModel instantAnswer = (BaseModel) getItem(position);
			if (instantAnswer instanceof Article) {
				ArticleDialogFragment fragment = new ArticleDialogFragment((Article) instantAnswer);
				fragment.show(context.getFragmentManager(), "ArticleDialogFragment");
			} else if (instantAnswer instanceof Suggestion) {
				SuggestionDialogFragment fragment = new SuggestionDialogFragment((Suggestion) instantAnswer);
				fragment.show(context.getFragmentManager(), "SuggestionDialogFragment");
			}
		}
	}

	@Override
	public void onChildViewAdded(View parent, View child) {
		if (state == State.DETAILS && emailField != null)
			emailField.requestFocus();
		else
			textField.requestFocus();
	}

	@Override
	public void onChildViewRemoved(View parent, View child) {
		if (state == State.DETAILS && emailField != null)
			emailField.requestFocus();
		else
			textField.requestFocus();
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
			} else if (type == SPACE) {
				view = new LinearLayout(context);
				view.setPadding(0, 40, 0, 0);
			} else if (type == TEXT) {
				view = inflater.inflate(R.layout.contact_text_item, null);
				textField = (EditText) view.findViewById(R.id.text);
				textField.addTextChangedListener(new TextWatcher() {
					@Override
					public void onTextChanged(CharSequence s, int start, int before, int count) {
						if (state != State.INIT) {
							state = State.INIT;
							notifyDataSetChanged();
						}
					}

					@Override
					public void beforeTextChanged(CharSequence s, int start, int count, int after) {
					}

					@Override
					public void afterTextChanged(Editable s) {
					}
				});
			} else if (type == EMAIL_FIELD || type == NAME_FIELD) {
				view = inflater.inflate(R.layout.text_field_item, null);
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
				button.setText(getSubmitString());
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
		} else if (type == EMAIL_FIELD || type == NAME_FIELD) {
			TextView title = (TextView) view.findViewById(R.id.header_text);
			final EditText field = (EditText) view.findViewById(R.id.text_field);
			if (type == EMAIL_FIELD) {
				title.setText(R.string.your_email_address);
				emailField = field;
				field.setHint(R.string.email_address_hint);
				field.setInputType(EditorInfo.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
				// TODO set saved value
			} else if (type == NAME_FIELD) {
				title.setText(R.string.your_name);
				nameField = field;
				field.setHint(R.string.name_hint);
				field.setInputType(EditorInfo.TYPE_TEXT_VARIATION_PERSON_NAME);
				// TODO set saved value
			}
		}
		return view;
	}
	
	@Override
	public Object getItem(int position) {
		int type = getItemViewType(position);
		if (type == INSTANT_ANSWER) {
			return instantAnswers.get(position - getRows().indexOf(INSTANT_ANSWER) + 1);
		}
		return null;
	}

	protected void onButtonTapped() {
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
		} else if (state == State.DETAILS) {
			doSubmit();
		}
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

}