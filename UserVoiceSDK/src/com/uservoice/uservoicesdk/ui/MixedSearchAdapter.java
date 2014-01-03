package com.uservoice.uservoicesdk.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.uservoice.uservoicesdk.R;
import com.uservoice.uservoicesdk.activity.SearchActivity;
import com.uservoice.uservoicesdk.babayaga.Babayaga;
import com.uservoice.uservoicesdk.model.Article;
import com.uservoice.uservoicesdk.model.BaseModel;
import com.uservoice.uservoicesdk.model.Suggestion;
import com.uservoice.uservoicesdk.rest.Callback;
import com.uservoice.uservoicesdk.rest.RestResult;
import com.uservoice.uservoicesdk.rest.RestTask;

public class MixedSearchAdapter extends SearchAdapter<BaseModel> implements AdapterView.OnItemClickListener {

    protected static int SEARCH_RESULT = 0;
    protected static int LOADING = 1;

    public static int SCOPE_ALL = 0;
    public static int SCOPE_ARTICLES = 1;
    public static int SCOPE_IDEAS = 2;

    protected LayoutInflater inflater;
    protected final FragmentActivity context;

    public MixedSearchAdapter(FragmentActivity context) {
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public boolean isEnabled(int position) {
        return !loading;
    }

    public List<BaseModel> getScopedSearchResults() {
        if (scope == SCOPE_ALL) {
            return searchResults;
        } else if (scope == SCOPE_ARTICLES) {
            List<BaseModel> articles = new ArrayList<BaseModel>();
            for (BaseModel model : searchResults) {
                if (model instanceof Article)
                    articles.add(model);
            }
            return articles;
        } else if (scope == SCOPE_IDEAS) {
            List<BaseModel> ideas = new ArrayList<BaseModel>();
            for (BaseModel model : searchResults) {
                if (model instanceof Suggestion)
                    ideas.add(model);
            }
            return ideas;
        }
        return null;
    }

    @Override
    protected void searchResultsUpdated() {
        int articleResults = 0;
        int ideaResults = 0;
        for (BaseModel model : searchResults) {
            if (model instanceof Article)
                articleResults += 1;
            else
                ideaResults += 1;
        }
        ((SearchActivity) context).updateScopedSearch(searchResults.size(), articleResults, ideaResults);
    }

    @Override
    public int getCount() {
        return loading ? 1 : getScopedSearchResults().size();
    }

    @Override
    public Object getItem(int position) {
        return loading ? null : getScopedSearchResults().get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        return loading ? LOADING : SEARCH_RESULT;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    protected RestTask search(final String query, final Callback<List<BaseModel>> callback) {
        currentQuery = query;
        return Article.loadInstantAnswers(query, new Callback<List<BaseModel>>() {
            @Override
            public void onModel(List<BaseModel> list) {
                List<Article> articles = new ArrayList<Article>();
                List<Suggestion> suggestions = new ArrayList<Suggestion>();
                for (BaseModel model : list) {
                    if (model instanceof Article)
                        articles.add((Article) model);
                    else if (model instanceof Suggestion)
                        suggestions.add((Suggestion) model);
                }
                Babayaga.track(Babayaga.Event.SEARCH_ARTICLES, query, articles);
                Babayaga.track(Babayaga.Event.SEARCH_IDEAS, query, suggestions);
                callback.onModel(list);
            }

            @Override
            public void onError(RestResult error) {
                callback.onError(error);
            }
        });
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        int type = getItemViewType(position);
        if (view == null) {
            if (type == SEARCH_RESULT)
                view = inflater.inflate(R.layout.uv_instant_answer_item, null);
            else if (type == LOADING)
                view = inflater.inflate(R.layout.uv_loading_item, null);
        }

        if (type == SEARCH_RESULT) {
            Utils.displayInstantAnswer(view, (BaseModel) getItem(position));
        }
        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int type = getItemViewType(position);
        if (type == SEARCH_RESULT)
            Utils.showModel(context, (BaseModel) getItem(position));
    }

}
