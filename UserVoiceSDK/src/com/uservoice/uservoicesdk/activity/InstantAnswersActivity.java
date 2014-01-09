package com.uservoice.uservoicesdk.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ViewGroup;

import android.view.WindowManager;
import com.uservoice.uservoicesdk.R;
import com.uservoice.uservoicesdk.flow.InitManager;
import com.uservoice.uservoicesdk.ui.InstantAnswersAdapter;

public abstract class InstantAnswersActivity extends BaseListActivity {

    private InstantAnswersAdapter adapter;

    public InstantAnswersActivity() {
        super();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getListView().setDivider(null);

        adapter = createAdapter();
        setListAdapter(adapter);
        getListView().setOnHierarchyChangeListener(adapter);
        getListView().setOnItemClickListener(adapter);
        getListView().setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
        new InitManager(this, new Runnable() {
            @Override
            public void run() {
                onInitialize();
            }
        }).init();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN | WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    protected void onInitialize() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        if (adapter.hasText()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.uv_confirm);
            builder.setMessage(getDiscardDialogMessage());
            builder.setPositiveButton(R.string.uv_yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.setNegativeButton(R.string.uv_no, null);
            builder.show();
        } else {
            super.onBackPressed();
        }
    }

    protected abstract InstantAnswersAdapter createAdapter();

    protected abstract int getDiscardDialogMessage();

}
