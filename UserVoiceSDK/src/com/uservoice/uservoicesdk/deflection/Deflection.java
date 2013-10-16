package com.uservoice.uservoicesdk.deflection;

import java.util.*;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.uservoice.uservoicesdk.Session;
import com.uservoice.uservoicesdk.babayaga.Babayaga;
import com.uservoice.uservoicesdk.model.Article;
import com.uservoice.uservoicesdk.model.BaseModel;
import com.uservoice.uservoicesdk.model.ClientConfig;
import com.uservoice.uservoicesdk.model.Suggestion;
import com.uservoice.uservoicesdk.rest.RestMethod;
import com.uservoice.uservoicesdk.rest.RestResult;
import com.uservoice.uservoicesdk.rest.RestTask;
import com.uservoice.uservoicesdk.rest.RestTaskCallback;

public class Deflection {
	
	private static int interactionIdentifier = Integer.parseInt(String.valueOf(new Date().getTime()).substring(4));
	private static String searchText;
	
	public static void trackDeflection(String kind, String deflectingType, BaseModel deflector) {
		Map<String,String> params = deflectionParams();
		params.put("kind", kind);
		params.put("deflecting_type", deflectingType);
		params.put("deflector_id", String.valueOf(deflector.getId()));
		params.put("deflector_type", (deflector instanceof Article) ? "Faq" : "Suggestion");
		new RestTask(RestMethod.GET, "/clients/omnibox/deflections/upsert.json", params, getCallback()).execute();
	}
	
	public static void trackSearchDeflection(List<BaseModel> results, String deflectingType) {
		Map<String,String> params = deflectionParams();
		params.put("kind", "list");
		if (results.isEmpty()) {
			params.put("is_empty", "true");
		} else {
			int i = 0;
			for (BaseModel model : results) {
				String keyRoot = "results[" + String.valueOf(i) + "]";
				Map<String,String> result = new HashMap<String,String>();
				result.put(keyRoot + "[position]", String.valueOf(i));
				result.put(keyRoot + "[deflector_id]", String.valueOf(model.getId()));
				if (model instanceof Suggestion) {
					result.put(keyRoot + "[deflection_type]", "Suggestion");
					result.put(keyRoot + "[weight]", String.valueOf(((Suggestion)model).getWeight()));
				} else if (model instanceof Article) {
					result.put(keyRoot + "[deflection_type]", "Faq");
					result.put(keyRoot + "[weight]", String.valueOf(((Article)model).getWeight()));
				}
				i++;
			}
		}
		new RestTask(RestMethod.GET, "/clients/omnibox/deflections/list_view.json", params, getCallback()).execute();
	}

	public static int getInteractionIdentifier() {
		return interactionIdentifier;
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
		params.put("subdomain_id", String.valueOf(Session.getInstance().getClientConfig().getSubdomainId()));
		params.put("channel", "android");
		params.put("search_term", searchText);
		params.put("interaction_identifier", String.valueOf(interactionIdentifier));
		return params;
	}
}
