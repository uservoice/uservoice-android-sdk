package com.uservoice.uservoicesdk.ui;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.uservoice.uservoicesdk.R;
import com.uservoice.uservoicesdk.Session;
import com.uservoice.uservoicesdk.babayaga.Babayaga;
import com.uservoice.uservoicesdk.babayaga.Babayaga.Event;
import com.uservoice.uservoicesdk.flow.SigninManager;
import com.uservoice.uservoicesdk.model.Category;
import com.uservoice.uservoicesdk.model.Suggestion;
import com.uservoice.uservoicesdk.rest.RestResult;

public class PostIdeaAdapter extends InstantAnswersAdapter {

    private static int DESCRIPTION = 8;
    private static int CATEGORY = 9;
    private static int HELP = 10;
    private static int TEXT_HEADING = 11;

    private Spinner categorySelect;
    private EditText descriptionField;

    public PostIdeaAdapter(FragmentActivity context) {
        super(context);
        continueButtonMessage = R.string.uv_post_idea_continue_button;
    }

    @Override
    public int getViewTypeCount() {
        return super.getViewTypeCount() + 4;
    }

    @Override
    protected List<Integer> getDetailRows() {
        List<Integer> rows = new ArrayList<Integer>();
        rows.add(DESCRIPTION);
        if (Session.getInstance().getForum().getCategories().size() > 0)
            rows.add(CATEGORY);
        rows.add(SPACE);
        rows.add(EMAIL_FIELD);
        rows.add(NAME_FIELD);
        return rows;
    }

    @Override
    protected List<Integer> getRows() {
        List<Integer> rows = super.getRows();
        rows.add(0, TEXT_HEADING);
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
                view = inflater.inflate(R.layout.uv_text_field_item, null);
                TextView title = (TextView) view.findViewById(R.id.uv_header_text);
                title.setText(R.string.uv_idea_description_heading);
                EditText field = (EditText) view.findViewById(R.id.uv_text_field);
                restoreEnteredText(descriptionField, field, "");
                descriptionField = field;
                descriptionField.setHint(R.string.uv_idea_description_hint);
            } else if (type == CATEGORY) {
                view = inflater.inflate(R.layout.uv_select_field_item, null);
                TextView title = (TextView) view.findViewById(R.id.uv_header_text);
                categorySelect = (Spinner) view.findViewById(R.id.uv_select_field);
                categorySelect.setAdapter(new SpinnerAdapter<Category>(context, Session.getInstance().getForum().getCategories()));
                title.setText(R.string.uv_category);
            } else if (type == HELP) {
                view = inflater.inflate(R.layout.uv_idea_help_item, null);
            } else if (type == TEXT_HEADING) {
                view = inflater.inflate(R.layout.uv_header_item, null);
                TextView textView = (TextView) view.findViewById(R.id.uv_header_text);
                textView.setText(R.string.uv_idea_text_heading);
            } else {
                view = super.getView(position, convertView, parent);
            }
        }

        if (type == DESCRIPTION || type == CATEGORY || type == HELP || type == TEXT_HEADING) {
            // just skip the else
        } else if (type == TEXT) {
            TextView textView = (TextView) view.findViewById(R.id.uv_text);
            textView.setHint(R.string.uv_idea_text_hint);
            textView.setMinLines(1);
        } else {
            return super.getView(position, view, parent);
        }
        return view;
    }

    @Override
    protected void doSubmit() {
        isPosting = false;
        SigninManager.signIn(context, emailField.getText().toString(), nameField.getText().toString(), new Runnable() {
            @Override
            public void run() {
                isPosting = true;
                Category category = categorySelect == null ? null : (Category) categorySelect.getSelectedItem();
                Suggestion.createSuggestion(Session.getInstance().getForum(), category, textField.getText().toString(), descriptionField.getText().toString(), 1, new DefaultCallback<Suggestion>(context) {
                    @Override
                    public void onModel(Suggestion model) {
                        Babayaga.track(Event.SUBMIT_IDEA);
                        Toast.makeText(context, R.string.uv_msg_idea_created, Toast.LENGTH_SHORT).show();
                        context.finish();
                    }

                    @Override
                    public void onError(RestResult error) {
                        isPosting = false;
                        super.onError(error);
                    }
                });
            }
        });
    }

    @Override
    protected String getSubmitString() {
        return context.getString(R.string.uv_submit_idea);
    }

}
