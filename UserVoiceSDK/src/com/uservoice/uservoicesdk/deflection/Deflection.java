package com.uservoice.uservoicesdk.deflection;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.uservoice.uservoicesdk.Session;
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

    public static void trackDeflection(String kind, String deflectingType, BaseModel deflector) {
        Map<String, String> params = deflectionParams();
        params.put("kind", kind);
        params.put("deflecting_type", deflectingType);
        params.put("deflector_id", String.valueOf(deflector.getId()));
        params.put("deflector_type", (deflector instanceof Article) ? "Faq" : "Suggestion");
        new RestTask(RestMethod.GET, "/clients/omnibox/deflections/upsert.json", params, getCallback()).execute();
    }

    public static void trackSearchDeflection(List<BaseModel> results, String deflectingType) {
        Map<String, String> params = deflectionParams();
        params.put("kind", "list");
        params.put("deflecting_type", deflectingType);
        int articleResults = 0;
        int suggestionResults = 0;
        int index = 0;
        for (BaseModel model : results) {
            String prefix = "results[" + String.valueOf(index) + "]";
            params.put(prefix + "[position]", String.valueOf(index++));
            params.put(prefix + "[deflector_id]", String.valueOf(model.getId()));
            if (model instanceof Suggestion) {
                suggestionResults++;
                Suggestion suggestion = (Suggestion) model;
                params.put(prefix + "[weight]", String.valueOf(suggestion.getWeight()));
                params.put(prefix + "[deflector_type]", "Suggestion");
            } else if (model instanceof Article) {
                articleResults++;
                Article article = (Article) model;
                params.put(prefix + "[weight]", String.valueOf(article.getWeight()));
                params.put(prefix + "[deflector_type]", "Faq");
            }
        }
        params.put("faq_results", String.valueOf(articleResults));
        params.put("suggestion_results", String.valueOf(suggestionResults));
        new RestTask(RestMethod.GET, "/clients/omnibox/deflections/list_view.json", params, getCallback()).execute();
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
        Map<String, String> params = new HashMap<String, String>();
        if (Babayaga.getUvts() != null) {
            params.put("uvts", Babayaga.getUvts());
        }
        params.put("channel", "android");
        params.put("search_term", searchText);
        params.put("interaction_identifier", String.valueOf(interactionIdentifier));
        params.put("subdomain_id", String.valueOf(Session.getInstance().getClientConfig().getSubdomainId()));
        return params;
    }
}
