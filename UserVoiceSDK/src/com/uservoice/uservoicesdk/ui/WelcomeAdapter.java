package com.uservoice.uservoicesdk.ui;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.uservoice.uservoicesdk.R;
import com.uservoice.uservoicesdk.Session;
import com.uservoice.uservoicesdk.model.ClientConfig;
import com.uservoice.uservoicesdk.model.Forum;
import com.uservoice.uservoicesdk.model.Topic;

public class WelcomeAdapter extends BaseAdapter {
	
	private static int HEADER = 0;
	private static int FORUM = 1;
	private static int TOPIC = 2;
	private static int LOADING = 3;
	
	private List<Topic> topics;
	private LayoutInflater inflater;
	private final Context context;
	
	public WelcomeAdapter(Context context) {
		this.context = context;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		loadClientConfig();
		loadTopics();
	}
	
	private void loadClientConfig() {
		ClientConfig.loadClientConfig(new DefaultCallback<ClientConfig>(context) {
			@Override
			public void onModel(ClientConfig model) {
				Session.getInstance().setClientConfig(model);
				loadForum();
			}
		});
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

	@Override
	public int getCount() {
		return 4 + (topics == null ? 0 : topics.size());
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
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
			else if (type == HEADER)
				view = inflater.inflate(R.layout.header_item, null);
			else if (type == TOPIC)
				view = inflater.inflate(android.R.layout.simple_list_item_1, null);
		}
		
		if (type == FORUM) {
			TextView textView = (TextView) view.findViewById(android.R.id.text1);
			textView.setText(Session.getInstance().getForum().getName());
		} else if (type == HEADER) {
			TextView textView = (TextView) view.findViewById(R.id.text);
			textView.setText(position == 0 ? "FEEDBACK FORUM" : "KNOWLEDGE BASE");
		} else if (type == TOPIC) {
			Topic topic = position - 3 < topics.size() ? topics.get(position - 3) : null;
			TextView textView = (TextView) view.findViewById(android.R.id.text1);
			textView.setText(topic == null ? "All Articles" : topic.getName());
		}
		return view;
	}
	
	@Override
	public int getViewTypeCount() {
		return 4;
	}
	
	@Override
	public int getItemViewType(int position) {
		if (position == 0 || position == 2)
			return HEADER;
		if (position == 1)
			return Session.getInstance().getForum() == null ? LOADING : FORUM;
		if (topics != null)
			return TOPIC;
		return LOADING;
	}

}
