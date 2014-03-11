package com.uservoice.uservoicesdk.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.uservoice.uservoicesdk.Config;
import com.uservoice.uservoicesdk.R;
import com.uservoice.uservoicesdk.Session;
import com.uservoice.uservoicesdk.UserVoice;
import com.uservoice.uservoicesdk.activity.ContactActivity;
import com.uservoice.uservoicesdk.activity.ForumActivity;
import com.uservoice.uservoicesdk.activity.SearchActivity;
import com.uservoice.uservoicesdk.flow.InitManager;
import com.uservoice.uservoicesdk.model.Article;
import com.uservoice.uservoicesdk.model.BaseModel;
import com.uservoice.uservoicesdk.model.Forum;
import com.uservoice.uservoicesdk.model.Suggestion;
import com.uservoice.uservoicesdk.model.Topic;

public class PortalAdapter extends SearchAdapter<BaseModel> implements AdapterView.OnItemClickListener {

    public static int SCOPE_ALL = 0;
    public static int SCOPE_ARTICLES = 1;
    public static int SCOPE_IDEAS = 2;

    private static int KB_HEADER = 0;
    private static int FORUM = 1;
    private static int TOPIC = 2;
    private static int LOADING = 3;
    private static int CONTACT = 4;
    private static int ARTICLE = 5;
    private static int POWERED_BY = 6;

    private LayoutInflater inflater;
    private final FragmentActivity context;
    private boolean configLoaded = false;
    private List<Integer> staticRows;

    public PortalAdapter(FragmentActivity context) {
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        new InitManager(context, new Runnable() {
            @Override
            public void run() {
                configLoaded = true;
                notifyDataSetChanged();
                loadForum();
                loadTopics();
            }
        }).init();
    }

    private List<Topic> getTopics() {
        return Session.getInstance().getTopics();
    }

    private List<Article> getArticles() {
        return Session.getInstance().getArticles();
    }

    private boolean shouldShowArticles() {
        return Session.getInstance().getConfig().getTopicId() != -1 || (getTopics() != null && getTopics().isEmpty());
    }

    private void loadForum() {
        Forum.loadForum(Session.getInstance().getConfig().getForumId(), new DefaultCallback<Forum>(context) {
            @Override
            public void onModel(Forum model) {
                Session.getInstance().setForum(model);
                notifyDataSetChanged();
            }
        });
    }

    private void loadTopics() {
        final DefaultCallback<List<Article>> articlesCallback = new DefaultCallback<List<Article>>(context) {
            @Override
            public void onModel(List<Article> model) {
                Session.getInstance().setTopics(new ArrayList<Topic>());
                Session.getInstance().setArticles(model);
                notifyDataSetChanged();
            }
        };

        if (Session.getInstance().getConfig().getTopicId() != -1) {
            Article.loadForTopic(Session.getInstance().getConfig().getTopicId(), articlesCallback);
        } else {
            Topic.loadTopics(new DefaultCallback<List<Topic>>(context) {
                @Override
                public void onModel(List<Topic> model) {
                    if (model.isEmpty()) {
                        Session.getInstance().setTopics(model);
                        Article.loadAll(articlesCallback);
                    } else {
                        ArrayList<Topic> topics = new ArrayList<Topic>(model);
                        topics.add(Topic.ALL_ARTICLES);
                        Session.getInstance().setTopics(topics);
                        notifyDataSetChanged();
                    }
                }
            });
        }
    }

    private void computeStaticRows() {
        if (staticRows == null) {
            staticRows = new ArrayList<Integer>();
            Config config = Session.getInstance().getConfig();
            if (config.shouldShowContactUs())
                staticRows.add(CONTACT);
            if (config.shouldShowForum())
                staticRows.add(FORUM);
            if (config.shouldShowKnowledgeBase())
                staticRows.add(KB_HEADER);
        }
    }

    @Override
    public int getCount() {
        if (!configLoaded) {
            return 1;
        } else {
            computeStaticRows();
            int rows = staticRows.size();
            if (Session.getInstance().getConfig().shouldShowKnowledgeBase()) {
                if (getTopics() == null || (shouldShowArticles() && getArticles() == null)) {
                    rows += 1;
                } else {
                    rows += shouldShowArticles() ? getArticles().size() : getTopics().size();
                }
            }
            if (!Session.getInstance().getClientConfig().isWhiteLabel()) {
            	rows += 1;
            }
            return rows;
        }
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
    public Object getItem(int position) {
        computeStaticRows();
        if (position < staticRows.size() && staticRows.get(position) == FORUM)
            return Session.getInstance().getForum();
        else if (getTopics() != null && !shouldShowArticles() && position >= staticRows.size() && position - staticRows.size() < getTopics().size())
            return getTopics().get(position - staticRows.size());
        else if (getArticles() != null && shouldShowArticles() && position >= staticRows.size() && position - staticRows.size() < getArticles().size())
            return getArticles().get(position - staticRows.size());
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean isEnabled(int position) {
        if (!configLoaded)
            return false;
        computeStaticRows();
        if (position < staticRows.size()) {
            int type = staticRows.get(position);
            if (type == KB_HEADER || type == LOADING)
                return false;
        }
        return true;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        int type = getItemViewType(position);
        if (view == null) {
            if (type == LOADING)
                view = inflater.inflate(R.layout.uv_loading_item, null);
            else if (type == FORUM)
                view = inflater.inflate(R.layout.uv_text_item, null);
            else if (type == KB_HEADER)
                view = inflater.inflate(R.layout.uv_header_item_light, null);
            else if (type == TOPIC)
                view = inflater.inflate(R.layout.uv_text_item, null);
            else if (type == CONTACT)
                view = inflater.inflate(R.layout.uv_text_item, null);
            else if (type == ARTICLE)
                view = inflater.inflate(R.layout.uv_text_item, null);
            else if (type == POWERED_BY)
            	view = inflater.inflate(R.layout.uv_powered_by_item, null);
        }

        if (type == FORUM) {
            TextView textView = (TextView) view.findViewById(R.id.uv_text);
            textView.setText(R.string.uv_feedback_forum);
            TextView text2 = (TextView) view.findViewById(R.id.uv_text2);
            text2.setText(Utils.getQuantityString(text2, R.plurals.uv_ideas, Session.getInstance().getForum().getNumberOfOpenSuggestions()));
        } else if (type == KB_HEADER) {
            TextView textView = (TextView) view.findViewById(R.id.uv_header_text);
            textView.setText(R.string.uv_knowledge_base);
        } else if (type == TOPIC) {
            Topic topic = (Topic) getItem(position);
            TextView textView = (TextView) view.findViewById(R.id.uv_text);
            textView.setText(topic.getName());
            textView = (TextView) view.findViewById(R.id.uv_text2);
            if (topic == Topic.ALL_ARTICLES) {
                textView.setVisibility(View.GONE);
            } else {
                textView.setVisibility(View.VISIBLE);
                textView.setText(String.format("%d %s", topic.getNumberOfArticles(), context.getResources().getQuantityString(R.plurals.uv_articles, topic.getNumberOfArticles())));
            }
        } else if (type == CONTACT) {
            TextView textView = (TextView) view.findViewById(R.id.uv_text);
            textView.setText(R.string.uv_contact_us);
            view.findViewById(R.id.uv_text2).setVisibility(View.GONE);
        } else if (type == ARTICLE) {
            TextView textView = (TextView) view.findViewById(R.id.uv_text);
            Article article = (Article) getItem(position);
            textView.setText(article.getTitle());
        } else if (type == POWERED_BY) {
        	TextView textView = (TextView) view.findViewById(R.id.uv_version);
        	textView.setText(context.getString(R.string.uv_android_sdk) + " v" + UserVoice.getVersion());
        }

        View divider = view.findViewById(R.id.uv_divider);
        if (divider != null)
            divider.setVisibility((position == getCount() - 2 && getItemViewType(getCount() - 1) == POWERED_BY) || position == getCount() - 1 ? View.GONE : View.VISIBLE);
        if (type == FORUM)
            divider.setVisibility(View.GONE);

        return view;
    }

    @Override
    public int getViewTypeCount() {
        return 7;
    }

    @Override
    public int getItemViewType(int position) {
        if (!configLoaded)
            return LOADING;
        computeStaticRows();
        if (position < staticRows.size()) {
            int type = staticRows.get(position);
            if (type == FORUM && Session.getInstance().getForum() == null)
                return LOADING;
            return type;
        }
        if (Session.getInstance().getConfig().shouldShowKnowledgeBase()) {
	        if (getTopics() == null || (shouldShowArticles() && getArticles() == null)) {
	        	if (position - staticRows.size() == 0)
	        		return LOADING;
	        } else if (shouldShowArticles() && position - staticRows.size() < getArticles().size()) {
	        	return ARTICLE;
	        } else if (!shouldShowArticles() && position - staticRows.size() < getTopics().size()) {
	        	return TOPIC;
	        }
        }
        return POWERED_BY;
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int type = getItemViewType(position);
        if (type == CONTACT) {
            context.startActivity(new Intent(context, ContactActivity.class));
        } else if (type == FORUM) {
            context.startActivity(new Intent(context, ForumActivity.class));
        } else if (type == TOPIC || type == ARTICLE) {
            Utils.showModel(context, (BaseModel) getItem(position));
        }
    }

}
