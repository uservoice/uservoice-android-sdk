package com.uservoice.uservoicesdk.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.uservoice.uservoicesdk.Config;
import com.uservoice.uservoicesdk.R;
import com.uservoice.uservoicesdk.Session;
import com.uservoice.uservoicesdk.babayaga.Babayaga;
import com.uservoice.uservoicesdk.dialog.SuggestionDialogFragment;
import com.uservoice.uservoicesdk.flow.InitManager;
import com.uservoice.uservoicesdk.model.Forum;
import com.uservoice.uservoicesdk.model.Suggestion;
import com.uservoice.uservoicesdk.rest.Callback;
import com.uservoice.uservoicesdk.rest.RestResult;
import com.uservoice.uservoicesdk.rest.RestTask;
import com.uservoice.uservoicesdk.ui.DefaultCallback;
import com.uservoice.uservoicesdk.ui.PaginatedAdapter;
import com.uservoice.uservoicesdk.ui.PaginationScrollListener;
import com.uservoice.uservoicesdk.ui.SearchAdapter;
import com.uservoice.uservoicesdk.ui.SearchExpandListener;
import com.uservoice.uservoicesdk.ui.SearchQueryListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ForumActivity extends SearchActivity {

    private Forum forum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.uv_feedback_forum);

        List<Suggestion> suggestions = new ArrayList<Suggestion>();

        getListView().setDivider(null);
        setListAdapter(new PaginatedAdapter<Suggestion>(this, R.layout.uv_suggestion_item, suggestions) {
            boolean initializing = true;
            List<Integer> staticRows;

            @Override
            public void loadMore() {
                // Need to notify data set change as initializing flag
                // will impact count below
                if (initializing) {
                    notifyDataSetChanged();
                }
                initializing = false;
                super.loadMore();
            }

            private void computeStaticRows() {
                if (staticRows == null) {
                    staticRows = new ArrayList<Integer>();
                    Config config = Session.getInstance().getConfig();
                    if (config.shouldShowPostIdea())
                        staticRows.add(2);
                    staticRows.add(3);
                }
            }

            @Override
            public int getViewTypeCount() {
                return super.getViewTypeCount() + 2;
            }

            @Override
            public boolean isEnabled(int position) {
                return getItemViewType(position) == 2 || super.isEnabled(position);
            }

            @Override
            public int getCount() {
                computeStaticRows();
                return super.getCount() + staticRows.size() + (initializing ? 1 : 0);
            }

            @Override
            public int getItemViewType(int position) {
                computeStaticRows();
                if (position < staticRows.size())
                    return staticRows.get(position);
                if (position == staticRows.size() && initializing)
                    return LOADING;
                return super.getItemViewType(position - staticRows.size());
            }

            @Override
            public Object getItem(int position) {
                computeStaticRows();
                return super.getItem(position - staticRows.size());
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                int type = getItemViewType(position);
                if (type == 2 || type == 3) {
                    View view = convertView;
                    if (view == null) {
                        if (type == 2) {
                            view = getLayoutInflater().inflate(R.layout.uv_text_item, null);
                            TextView text = (TextView) view.findViewById(R.id.uv_text);
                            text.setText(R.string.uv_post_an_idea);
                            view.findViewById(R.id.uv_divider).setVisibility(View.GONE);
                            view.findViewById(R.id.uv_text2).setVisibility(View.GONE);
                        } else {
                            view = getLayoutInflater().inflate(R.layout.uv_header_item_light, null);
                            TextView text = (TextView) view.findViewById(R.id.uv_header_text);
                            text.setText(R.string.uv_idea_text_heading);
                        }
                    }
                    return view;
                } else {
                    return super.getView(position, convertView, parent);
                }
            }

            @Override
            protected void customizeLayout(View view, Suggestion model) {
                TextView textView = (TextView) view.findViewById(R.id.uv_suggestion_title);
                textView.setText(model.getTitle());

                textView = (TextView) view.findViewById(R.id.uv_subscriber_count);
                if (Session.getInstance().getClientConfig().shouldDisplaySuggestionsByRank()) {
                    textView.setText(model.getRankString());
                } else {
                    textView.setText(String.valueOf(model.getNumberOfSubscribers()));
                }

                textView = (TextView) view.findViewById(R.id.uv_suggestion_status);
                View colorView = view.findViewById(R.id.uv_suggestion_status_color);
                if (model.getStatus() == null) {
                    textView.setVisibility(View.GONE);
                    colorView.setVisibility(View.GONE);
                } else {
                    int color = Color.parseColor(model.getStatusColor());
                    textView.setVisibility(View.VISIBLE);
                    textView.setTextColor(color);
                    textView.setText(model.getStatus().toUpperCase(Locale.getDefault()));
                    colorView.setVisibility(View.VISIBLE);
                    colorView.setBackgroundColor(color);
                }
            }

            @Override
            public void loadPage(int page, Callback<List<Suggestion>> callback) {
                Suggestion.loadSuggestions(forum, page, callback);
            }

            @Override
            public RestTask search(final String query, final Callback<List<Suggestion>> callback) {
                if (forum == null) {
                    return null;
                }
                return Suggestion.searchSuggestions(forum, query, new Callback<List<Suggestion>>() {

                    @Override
                    public void onModel(List<Suggestion> model) {
                        Babayaga.track(Babayaga.Event.SEARCH_IDEAS, query, model);
                        callback.onModel(model);
                    }

                    @Override
                    public void onError(RestResult error) {
                        callback.onError(error);
                    }
                });
            }

            @Override
            public int getTotalNumberOfObjects() {
                return forum.getNumberOfOpenSuggestions();
            }
        });

        getListView().setOnScrollListener(new PaginationScrollListener(getModelAdapter()) {
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (forum != null)
                    super.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
            }
        });

        getListView().setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    startActivity(new Intent(ForumActivity.this, PostIdeaActivity.class));
                } else if (position != 1) {
                    Suggestion suggestion = (Suggestion) getModelAdapter().getItem(position);
                    SuggestionDialogFragment dialog = new SuggestionDialogFragment(suggestion, null);
                    dialog.show(getSupportFragmentManager(), "SuggestionDialogFragment");
                }
            }
        });

        new InitManager(this, new Runnable() {
            @Override
            public void run() {
                loadForum();
                Session.getInstance().setSignInListener(new Runnable() {
                    @Override
                    public void run() {
                        if (forum != null) {
                            getModelAdapter().reload();
                        }
                    }
                });
            }
        }).init();
    }

    @SuppressLint("NewApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.uv_forum, menu);
        MenuItem searchItem = menu.findItem(R.id.uv_menu_search);
        if (hasActionBar()) {
            MenuItemCompat.setOnActionExpandListener(searchItem, new SearchExpandListener(this));
            SearchView search = (SearchView) MenuItemCompat.getActionView(searchItem);
            search.setOnQueryTextListener(new SearchQueryListener(this));
        } else {
            searchItem.setVisible(false);
        }
        menu.findItem(R.id.uv_new_idea).setVisible(Session.getInstance().getConfig().shouldShowPostIdea());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.uv_new_idea) {
            startActivity(new Intent(this, PostIdeaActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        Session.getInstance().setSignInListener(null);
        super.onStop();
    }

    private void loadForum() {
        if (Session.getInstance().getForum() != null) {
            forum = Session.getInstance().getForum();
            Babayaga.track(Babayaga.Event.VIEW_FORUM, forum.getId());
            setTitle(forum.getName());
            getModelAdapter().loadMore();
            return;
        }
        Forum.loadForum(Session.getInstance().getConfig().getForumId(), new DefaultCallback<Forum>(this) {
            @Override
            public void onModel(Forum model) {
                Session.getInstance().setForum(model);
                forum = model;
                setTitle(forum.getName());
                getModelAdapter().loadMore();
            }
        });
    }

    @Override
    public SearchAdapter<?> getSearchAdapter() {
        return getModelAdapter();
    }

    @SuppressWarnings("unchecked")
    public PaginatedAdapter<Suggestion> getModelAdapter() {
        return (PaginatedAdapter<Suggestion>) getListAdapter();
    }

    @Override
    public void showSearch() {
    }

    @Override
    public void hideSearch() {
    }

    public void suggestionUpdated(Suggestion model) {
        getModelAdapter().notifyDataSetChanged();
    }
}
