package com.uservoice.uservoicesdk.babayaga;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Map;

import android.net.http.AndroidHttpClient;
import com.uservoice.uservoicesdk.UserVoice;
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
import android.util.Base64;
import android.util.Log;

import com.uservoice.uservoicesdk.Session;

public class BabayagaTask extends AsyncTask<String,String,Void> {
	
    private final String event;
	private final Map<String, Object> traits;
	private final Map<String, Object> eventProps;
	private final String uvts;

	public BabayagaTask(String event, String uvts, Map<String,Object> traits, Map<String,Object> eventProps){
		this.event = event;
		this.uvts = uvts;
		this.traits = traits;
		this.eventProps = eventProps;
    }
    
	@Override
	protected Void doInBackground(String... args) {
	    try {
			JSONObject data = new JSONObject();
			if (traits != null && !traits.isEmpty()) {
				data.put("u", new JSONObject(traits));
			}
			if (eventProps != null && !eventProps.isEmpty()) {
				data.put("e", eventProps);
			}
			String subdomainId = Session.getInstance().getClientConfig().getSubdomainId();
			StringBuilder url = new StringBuilder(String.format("https://%s/t/%s/%s/%s", Babayaga.DOMAIN, subdomainId, Babayaga.CHANNEL, event));
			if (uvts != null) {
				url.append("/");
				url.append(uvts);
			}
			url.append("/track.js?_=");
			url.append(new Date().getTime());
			url.append("&c=_");
			if (data.length() != 0) {
				url.append("&d=");
				try {
					url.append(URLEncoder.encode(Base64.encodeToString(data.toString().getBytes(), Base64.NO_WRAP), "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					throw new RuntimeException(e);
				}
			}
			
	    	HttpRequestBase request = new HttpGet();
			request.setURI(new URI(url.toString()));
            AndroidHttpClient client = AndroidHttpClient.newInstance(String.format("uservoice-android-%s", UserVoice.getVersion()), Session.getInstance().getContext());
            HttpResponse response = client.execute(request);
            HttpEntity responseEntity = response.getEntity();
            StatusLine responseStatus = response.getStatusLine();
            int statusCode = responseStatus != null ? responseStatus.getStatusCode() : 0;
            if (statusCode != 200)
            	return null;
            String body = responseEntity != null ? EntityUtils.toString(responseEntity) : null;
            client.close();
            if (body != null && body.length() > 0) {
                String payload = body.substring(2, body.length() - 2);
                JSONObject responseData = new JSONObject(payload);
                String uvts = responseData.getString("uvts");
                Babayaga.setUvts(uvts);
            }
	    } catch (Exception e) {
	    	e.printStackTrace();
	    	Log.e("UV", String.format("%s: %s", e.getClass().getName(), e.getMessage()));
		}
	    return null;
	}
}
