package com.uservoice.uservoicesdk.babayaga;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import com.uservoice.uservoicesdk.Session;
import com.uservoice.uservoicesdk.UserVoice;

public class BabayagaTask extends AsyncTask<String, String, Void> {

    private final String event;
    private final Map<String, Object> traits;
    private final Map<String, Object> eventProps;
    private final String uvts;

    public BabayagaTask(String event, String uvts, Map<String, Object> traits, Map<String, Object> eventProps) {
        this.event = event;
        this.uvts = uvts;
        this.traits = traits;
        this.eventProps = eventProps;
    }

    @Override
    protected Void doInBackground(String... args) {
        AndroidHttpClient client = null;
        try {
            JSONObject data = new JSONObject();
            if (traits != null && !traits.isEmpty()) {
                data.put("u", new JSONObject(traits));
            }
            if (eventProps != null && !eventProps.isEmpty()) {
                data.put("e", eventProps);
            }
            String subdomain;
            String route;
            if (Session.getInstance().getClientConfig() != null) {
                subdomain = Session.getInstance().getClientConfig().getSubdomainId();
                route = "t";
            } else {
                subdomain = Session.getInstance().getConfig().getSite().split("\\.")[0];
                route = "t/k";
            }
            String channel = event.equals(Babayaga.Event.VIEW_APP) ? Babayaga.EXTERNAL_CHANNEL : Babayaga.CHANNEL;
            StringBuilder url = new StringBuilder(String.format("https://%s/%s/%s/%s/%s", Babayaga.DOMAIN, route, subdomain, channel, event));
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
            Log.d("UV", url.toString());

            HttpRequestBase request = new HttpGet();
            request.setURI(new URI(url.toString()));
            client = AndroidHttpClient.newInstance(String.format("uservoice-android-%s", UserVoice.getVersion()), Session.getInstance().getContext());
            HttpResponse response = client.execute(request);
            HttpEntity responseEntity = response.getEntity();
            StatusLine responseStatus = response.getStatusLine();
            int statusCode = responseStatus != null ? responseStatus.getStatusCode() : 0;
            if (statusCode != 200)
                return null;
            String body = responseEntity != null ? EntityUtils.toString(responseEntity) : null;
            if (body != null && body.length() > 0) {
                String payload = body.substring(2, body.length() - 2);
                JSONObject responseData = new JSONObject(payload);
                String uvts = responseData.getString("uvts");
                Babayaga.setUvts(uvts);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("UV", String.format("%s: %s", e.getClass().getName(), e.getMessage()));
        } finally {
            if (client != null) {
                client.close();
            }
        }
        return null;
    }
}
