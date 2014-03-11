package com.uservoice.uservoicesdk.ui;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public abstract class LoadAllAdapter<T> extends ModelAdapter<T> {

    public LoadAllAdapter(Context context, int layoutId, List<T> objects) {
        super(context, layoutId, objects);
        loadAll();
    }

    private void loadAll() {
        loading = true;
        notifyDataSetChanged();
        loadPage(1, new DefaultCallback<List<T>>(context) {
            @Override
            public void onModel(List<T> model) {
                objects.addAll(model);
                loading = false;
                notifyDataSetChanged();
            }
        });
    }

    public void reload() {
        if (loading)
            return;
        objects = new ArrayList<T>();
        loadAll();
    }
}
