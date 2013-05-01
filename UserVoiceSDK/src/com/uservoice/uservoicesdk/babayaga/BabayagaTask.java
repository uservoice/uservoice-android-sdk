package com.uservoice.uservoicesdk.babayaga;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.uservoice.uservoicesdk.Session;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Base64;

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
    
	@SuppressLint("DefaultLocale")
	private static String getTzOffset() {
		int offset = TimeZone.getDefault().getOffset(new Date().getTime());
		return String.format("%s%02d:%02d", offset > 0 ? "-" : "+", Math.floor(Math.abs(offset) / 60.0), Math.abs(offset) % 60);
	}
	
	@Override
	protected Void doInBackground(String... args) {
	    try {
			Map<String,Object> data = new HashMap<String,Object>();
			if (traits != null && !traits.isEmpty()) {
				Map<String,Object> copy = new HashMap<String,Object>(traits);
				copy.put("o", getTzOffset());
				data.put("u", copy);
			}
			if (eventProps != null && !eventProps.isEmpty()) {
				data.put("e", eventProps);
			}
			String subdomain = Session.getInstance().getClientConfig().getSubdomain();
			StringBuilder url = new StringBuilder(String.format("http://%s/t/%s/%s/%s", Babayaga.DOMAIN, subdomain, Babayaga.CHANNEL, event));
			if (uvts != null) {
				url.append("/");
				url.append(uvts);
			}
			url.append("/track.js?_=");
			url.append(new Date().getTime());
			url.append("&c=_");
			if (!data.isEmpty()) {
				url.append("&d=");
				try {
					url.append(URLEncoder.encode(Base64.encodeToString(new JSONObject(data).toString().getBytes(), Base64.DEFAULT), "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					throw new RuntimeException(e);
				}
			}
			
	    	HttpRequestBase request = new HttpGet();
			request.setURI(new URI(url.toString()));
			HttpClient client = new DefaultHttpClient();
            HttpResponse response = client.execute(request);
            HttpEntity responseEntity = response.getEntity();
            StatusLine responseStatus = response.getStatusLine();
            int statusCode = responseStatus != null ? responseStatus.getStatusCode() : 0;
            if (statusCode != 200)
            	return null;
            String body = responseEntity != null ? EntityUtils.toString(responseEntity) : null;
            String payload = body.substring(2, body.length() - 2);
			JSONObject responseData = new JSONObject(payload);
            String uvts = responseData.getString("uvts");
            Babayaga.setUvts(uvts);
	    } catch (Exception e) {
		}
	    return null;
	}
}
