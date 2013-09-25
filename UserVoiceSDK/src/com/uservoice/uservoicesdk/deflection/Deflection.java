package com.uservoice.uservoicesdk.deflection;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.uservoice.uservoicesdk.babayaga.Babayaga;
import com.uservoice.uservoicesdk.model.Article;
import com.uservoice.uservoicesdk.model.BaseModel;
import com.uservoice.uservoicesdk.model.Suggestion;
import com.uservoice.uservoicesdk.rest.RestMethod;
import com.uservoice.uservoicesdk.rest.RestResult;
import com.uservoice.uservoicesdk.rest.RestTask;
import com.uservoice.uservoicesdk.rest.RestTaskCallback;

public class Deflection {
	
	private static int interactionIdentifier = Integer.parseInt(String.valueOf(new Date().getTime()).substring(4));
	private static String searchText;
	
	public static void trackDeflection(String kind, BaseModel deflector) {
		Map<String,String> params = deflectionParams();
		params.put("kind", kind);
		params.put("deflector_id", String.valueOf(deflector.getId()));
		params.put("deflector_type", (deflector instanceof Article) ? "Faq" : "Suggestion");
		new RestTask(RestMethod.GET, "/clients/omnibox/deflections/upsert.json", params, getCallback()).execute();
	}
	
	public static void trackSearchDeflection(List<BaseModel> results) {
		Map<String,String> params = deflectionParams();
		params.put("kind", "list");
		List<BasicNameValuePair> list = RestTask.paramsToList(params);
		for (BaseModel model : results) {
			if (model instanceof Suggestion)
				list.add(new BasicNameValuePair("suggestion_ids[]", String.valueOf(model.getId())));
			else if (model instanceof Article)
				list.add(new BasicNameValuePair("faq_ids[]", String.valueOf(model.getId())));
		}
		new RestTask(RestMethod.GET, "/clients/omnibox/deflections/list_view.json", list, getCallback()).execute();
	}
	
	public static void setSearchText(String query) {
		if (query.equals(searchText))
			return;
		searchText = query;
		interactionIdentifier += 1;
	}
	
	private static RestTaskCallback getCallback() {
		return new RestTaskCallback(null) {
			@Override
			public void onError(RestResult result) {
				Log.e("UV", "Failed sending deflection: " + result.getMessage());
			}
			
			@Override
			public void onComplete(JSONObject result) throws JSONException {
				Log.d("UV", result.toString());
			}
		};
	}

	private static Map<String, String> deflectionParams() {
		Map<String,String> params = new HashMap<String,String>();
		params.put("uvts", Babayaga.getUvts());
		params.put("channel", "android");
		params.put("search_term", searchText);
		params.put("interaction_identifier", String.valueOf(interactionIdentifier));
		return params;
	}
}
