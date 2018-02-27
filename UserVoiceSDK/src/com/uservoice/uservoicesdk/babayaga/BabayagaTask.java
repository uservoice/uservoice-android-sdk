package com.uservoice.uservoicesdk.babayaga;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Map;

import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import com.uservoice.uservoicesdk.Session;
import com.uservoice.uservoicesdk.UserVoice;

public class BabayagaTask extends AsyncTask<String, String, Void> {

    private final String event;
    private final Map<String, Object> eventProps;
    private final String uvts;
    private final Context context;

    public BabayagaTask(Context context, String event, String uvts, Map<String, Object> eventProps) {
        this.event = event;
        this.uvts = uvts;
        this.eventProps = eventProps;
        this.context = context.getApplicationContext();
    }

    @Override
    protected Void doInBackground(String... args) {
        try {
            JSONObject data = new JSONObject();
            Map<String, Object> traits = Session.getInstance().getConfig(context).getUserTraits();
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
                subdomain = Session.getInstance().getConfig(context).getSite().split("\\.")[0];
                route = "t/k";
            }
            String channel = event.equals(Babayaga.Event.VIEW_APP.toString()) ? Babayaga.EXTERNAL_CHANNEL : Babayaga.CHANNEL;
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

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .get()
                    .url(url.toString())
                    .addHeader("User-Agent", String.format("uservoice-android-%s", UserVoice.getVersion()))
                    .build();

            Response response = client.newCall(request).execute();
            int statusCode = response.code();
            if (statusCode != 200)
                return null;
            String body = response.body().string();
            if (body.length() > 0) {
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
