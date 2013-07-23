package com.uservoice.uservoicesdk.ui;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
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
import com.uservoice.uservoicesdk.Session;
import com.uservoice.uservoicesdk.deflection.Deflection;
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
	protected FragmentActivity context;
	protected LayoutInflater inflater;
	protected EditText textField;
	protected EditText emailField;
	protected EditText nameField;
	protected int continueButtonMessage;

	public InstantAnswersAdapter(FragmentActivity context) {
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

	protected boolean isLoading() {
		return Session.getInstance().getClientConfig() == null;
	}

	@Override
	public int getCount() {
		return isLoading() ? 1 : getRows().size();
	}

	@Override
	public int getItemViewType(int position) {
		return isLoading() ? LOADING : getRows().get(position);
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
			Deflection.trackDeflection("show", (BaseModel) getItem(position));
			Utils.showModel(context, (BaseModel) getItem(position));
		}
	}

	@Override
	public void onChildViewAdded(View parent, View child) {
		if (state == State.DETAILS && emailField != null)
			emailField.requestFocus();
		else if (textField != null)
			textField.requestFocus();
	}

	@Override
	public void onChildViewRemoved(View parent, View child) {
		onChildViewAdded(null, null);
	}

	@SuppressLint("CutPasteId")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		int type = getItemViewType(position);
		if (view == null) {
			if (type == LOADING) {
				view = inflater.inflate(R.layout.uv_loading_item, null);
			} else if (type == BUTTON) {
				view = inflater.inflate(R.layout.uv_contact_button_item, null);
				Button button = (Button) view.findViewById(R.id.uv_contact_button);
				button.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						onButtonTapped();
					}
				});
			} else if (type == HEADING) {
				view = inflater.inflate(R.layout.uv_header_item, null);
			} else if (type == INSTANT_ANSWER) {
				view = inflater.inflate(R.layout.uv_instant_answer_item, null);
			} else if (type == SPACE) {
				view = new LinearLayout(context);
				view.setPadding(0, 30, 0, 0);
			} else if (type == TEXT) {
				view = inflater.inflate(R.layout.uv_contact_text_item, null);
				textField = (EditText) view.findViewById(R.id.uv_text);
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
				view = inflater.inflate(R.layout.uv_text_field_item, null);
			}
		}

		if (type == BUTTON) {
			Button button = (Button) view.findViewById(R.id.uv_contact_button);
			button.setEnabled(state != State.INIT_LOADING);
			switch (state) {
				case INIT:
					button.setText(R.string.uv_next);
					break;
				case INIT_LOADING:
					button.setText(R.string.uv_loading);
					break;
				case INSTANT_ANSWERS:
					button.setText(continueButtonMessage);
					break;
				case DETAILS:
					button.setText(getSubmitString());
					break;
			}
		} else if (type == INSTANT_ANSWER) {
			Utils.displayInstantAnswer(view, (BaseModel) getItem(position));
			view.findViewById(R.id.uv_divider).setVisibility(getRows().lastIndexOf(INSTANT_ANSWER) == position ? View.GONE : View.VISIBLE);
		} else if (type == EMAIL_FIELD || type == NAME_FIELD) {
			TextView title = (TextView) view.findViewById(R.id.uv_header_text);
			final EditText field = (EditText) view.findViewById(R.id.uv_text_field);
			if (type == EMAIL_FIELD) {
				title.setText(R.string.uv_your_email_address);
				emailField = field;
				field.setHint(R.string.uv_email_address_hint);
				field.setInputType(EditorInfo.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
				field.setText(Session.getInstance().getEmail());
			} else if (type == NAME_FIELD) {
				title.setText(R.string.uv_your_name);
				nameField = field;
				field.setHint(R.string.uv_name_hint);
				field.setInputType(EditorInfo.TYPE_TEXT_VARIATION_PERSON_NAME);
				field.setText(Session.getInstance().getName());
			}
		} else if (type == HEADING) {
			TextView textView = (TextView) view.findViewById(R.id.uv_header_text);
			boolean hasArticles = false;
			boolean hasIdeas = false;
			for (BaseModel model : instantAnswers) {
				if (model instanceof Article)
					hasArticles = true;
				if (model instanceof Suggestion)
					hasIdeas = true;
			}
			textView.setText(hasArticles ? (hasIdeas ? R.string.uv_matching_articles_and_ideas : R.string.uv_matching_articles) : R.string.uv_matching_ideas);
		}
		return view;
	}

	@Override
	public Object getItem(int position) {
		int type = getItemViewType(position);
		if (type == INSTANT_ANSWER) {
			return instantAnswers.get(position - getRows().indexOf(INSTANT_ANSWER));
		}
		return null;
	}

	protected void onButtonTapped() {
		if (state == State.INIT) {
			String query = textField.getText().toString().trim();
			if (query.length() == 0)
				return;
			state = State.INIT_LOADING;
			notifyDataSetChanged();
			Deflection.setSearchText(query);
			Article.loadInstantAnswers(query, new DefaultCallback<List<BaseModel>>(context) {
				@Override
				public void onModel(List<BaseModel> model) {
					Deflection.trackSearchDeflection(model);
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
			String name = nameField.getText().toString();
			String email = emailField.getText().toString();
			if (email.isEmpty()) {
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setTitle(R.string.uv_error);
				builder.setMessage(R.string.uv_msg_user_identity_validation);
				builder.create().show();
			} else {
				Session.getInstance().persistIdentity(name, email);
				doSubmit();
			}
		}
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	public boolean hasText() {
        return textField != null && textField.getText().toString().length() != 0;
	}

}
