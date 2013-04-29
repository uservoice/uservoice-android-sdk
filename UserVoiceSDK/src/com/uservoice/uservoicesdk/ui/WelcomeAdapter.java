package com.uservoice.uservoicesdk.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.uservoice.uservoicesdk.Config;
import com.uservoice.uservoicesdk.InitManager;
import com.uservoice.uservoicesdk.R;
import com.uservoice.uservoicesdk.Session;
import com.uservoice.uservoicesdk.model.Article;
import com.uservoice.uservoicesdk.model.BaseModel;
import com.uservoice.uservoicesdk.model.Forum;
import com.uservoice.uservoicesdk.model.Suggestion;
import com.uservoice.uservoicesdk.model.Topic;
import com.uservoice.uservoicesdk.rest.Callback;

public class WelcomeAdapter extends SearchAdapter<BaseModel> {
	
	private static int FEEDBACK_HEADER = 0;
	private static int KB_HEADER = 1;
	private static int FORUM = 2;
	private static int TOPIC = 3;
	private static int LOADING = 4;
	private static int CONTACT = 5;
	private static int RESULT_ARTICLE = 6;
	private static int RESULT_IDEA = 7;
	
	private List<Topic> topics;
	private LayoutInflater inflater;
	private final Context context;
	private List<Integer> staticRows;
	
	public WelcomeAdapter(Context context) {
		this.context = context;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		new InitManager(context, new Runnable() {
			@Override
			public void run() {
				notifyDataSetChanged();
				// this has to be deferred only because we fall back to clientconfig forum_id 
				loadForum();
			}
		}).init();
		loadTopics();
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
		Topic.loadTopics(new DefaultCallback<List<Topic>>(context) {
			@Override
			public void onModel(List<Topic> model) {
				topics = model;
				notifyDataSetChanged();
			}
		});
	}
	
	private void computeStaticRows() {
		if (staticRows == null) {
			staticRows = new ArrayList<Integer>();
			Config config = Session.getInstance().getConfig();
			if (config.shouldShowContactUs() || config.shouldShowForum())
				staticRows.add(FEEDBACK_HEADER);
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
		if (Session.getInstance().getClientConfig() == null) {
			return 1;
		} else if (searchActive) {
			return loading ? 1 : searchResults.size();
		} else {
			computeStaticRows();
			return staticRows.size() + (Session.getInstance().getConfig().shouldShowKnowledgeBase() ? (topics == null ? 1 : topics.size() + 1) : 0);
		}
	}

	@Override
	public Object getItem(int position) {
		if (searchActive)
			return loading ? null : searchResults.get(position);
		computeStaticRows();
		if (position < staticRows.size() && staticRows.get(position) == FORUM)
			return Session.getInstance().getForum();
		else if (topics != null && position >= staticRows.size() && position - staticRows.size() < topics.size()) 
			return topics.get(position - staticRows.size());
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public boolean isEnabled(int position) {
		if (searchActive)
			return !loading;
		if (Session.getInstance().getClientConfig() == null)
			return false;
		computeStaticRows();
		if (position < staticRows.size()) {
			int type = staticRows.get(position);
			if (type == FEEDBACK_HEADER || type == KB_HEADER || type == LOADING)
				return false;
		}
		return true;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		int type = getItemViewType(position);
		if (view == null) {
			if (type == LOADING)
				view = inflater.inflate(R.layout.loading_item, null);
			else if (type == FORUM)
				view = inflater.inflate(android.R.layout.simple_list_item_1, null);
			else if (type == FEEDBACK_HEADER || type == KB_HEADER)
				view = inflater.inflate(R.layout.header_item, null);
			else if (type == TOPIC)
				view = inflater.inflate(android.R.layout.simple_list_item_1, null);
			else if (type == CONTACT)
				view = inflater.inflate(android.R.layout.simple_list_item_1, null);
			else if (type == RESULT_ARTICLE)
				view = inflater.inflate(android.R.layout.simple_list_item_1, null);
			else if (type == RESULT_IDEA)
				view = inflater.inflate(android.R.layout.simple_list_item_1, null);
		}
		
		if (type == FORUM) {
			TextView textView = (TextView) view.findViewById(android.R.id.text1);
			textView.setText("Feedback Forum");
		} else if (type == FEEDBACK_HEADER) {
			TextView textView = (TextView) view.findViewById(R.id.text);
			textView.setText("FEEDBACK & SUPPORT");
		} else if (type == KB_HEADER) {
			TextView textView = (TextView) view.findViewById(R.id.text);
			textView.setText("KNOWLEDGE BASE");
		} else if (type == TOPIC) {
			Topic topic = (Topic) getItem(position);
			TextView textView = (TextView) view.findViewById(android.R.id.text1);
			textView.setText(topic == null ? "All Articles" : topic.getName());
		} else if (type == CONTACT) {
			TextView textView = (TextView) view.findViewById(android.R.id.text1);
			textView.setText("Contact Us");
		} else if (type == RESULT_ARTICLE) {
			TextView textView = (TextView) view.findViewById(android.R.id.text1);
			Article article = (Article) searchResults.get(position);
			textView.setText(article.getQuestion());
		} else if (type == RESULT_IDEA) {
			TextView textView = (TextView) view.findViewById(android.R.id.text1);
			Suggestion suggestion = (Suggestion) searchResults.get(position);
			textView.setText(suggestion.getTitle());
		}
		return view;
	}
	
	@Override
	public int getViewTypeCount() {
		return 8;
	}
	
	@Override
	public int getItemViewType(int position) {
		if (Session.getInstance().getClientConfig() == null)
			return LOADING;
		if (searchActive) {
			if (loading)
				return LOADING;
			BaseModel model = searchResults.get(position);
			if (model instanceof Article)
				return RESULT_ARTICLE;
			else if (model instanceof Suggestion)
				return RESULT_IDEA;
			else
				return LOADING;
		}
		computeStaticRows();
		if (position < staticRows.size()) {
			int type = staticRows.get(position);
			if (type == FORUM && Session.getInstance().getForum() == null)
				return LOADING;
			return type;
		}
		return topics == null ? LOADING : TOPIC;
	}

	@Override
	protected void search(String query, Callback<List<BaseModel>> callback) {
		Article.loadInstantAnswers(query, callback);
	}

}
