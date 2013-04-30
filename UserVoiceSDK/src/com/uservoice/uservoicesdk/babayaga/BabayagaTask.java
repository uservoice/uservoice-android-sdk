package com.uservoice.uservoicesdk.babayaga;

import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.os.AsyncTask;

public class BabayagaTask extends AsyncTask<String,String,Void> {
	private String url;
	
    public BabayagaTask(String url){
        this.url = url;
    }

	@Override
	protected Void doInBackground(String... args) {
	    try {
	    	HttpRequestBase request = new HttpGet();
			request.setURI(new URI(url));
			HttpClient client = new DefaultHttpClient();
            HttpResponse response = client.execute(request);
            HttpEntity responseEntity = response.getEntity();
            StatusLine responseStatus = response.getStatusLine();
            int statusCode = responseStatus != null ? responseStatus.getStatusCode() : 0;
            if (statusCode != 200)
            	return null;
            String body = responseEntity != null ? EntityUtils.toString(responseEntity) : null;
            String payload = body.substring(2, body.length() - 2);
			JSONObject data = new JSONObject(payload);
            String uvts = data.getString("uvts");
            Babayaga.setUvts(uvts);
	    } catch (Exception e) {
		}
	    return null;
	}
}
