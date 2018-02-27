package com.uservoice.uservoicesdk.rest;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.Map;

import oauth.signpost.OAuthConsumer;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import com.uservoice.uservoicesdk.Session;
import com.uservoice.uservoicesdk.UserVoice;
import com.uservoice.uservoicesdk.model.AccessToken;

public class RestTask extends AsyncTask<String, String, RestResult> {
    private String urlPath;
    private RestMethod method;
    private Map<String, String> params;
    private RestTaskCallback callback;
    private Context context;

    public RestTask(Context context, RestMethod method, String urlPath, Map<String, String> params, RestTaskCallback callback) {
        this.context = context.getApplicationContext();
        this.method = method;
        this.urlPath = urlPath;
        this.callback = callback;
        this.params = params;
    }

    @Override
    protected RestResult doInBackground(String... args) {
        try {
            Request request = createRequest();
            if (isCancelled())
                throw new InterruptedException();
            OkHttpClient client = new OkHttpClient();
            OAuthConsumer consumer = Session.getInstance().getOAuthConsumer(context);
            if (consumer != null) {
                AccessToken accessToken = Session.getInstance().getAccessToken();
                if (accessToken != null) {
                    consumer.setTokenWithSecret(accessToken.getKey(), accessToken.getSecret());
                }
                request = (Request) consumer.sign(request).unwrap();
            }
            Log.d("UV", urlPath);
            if (isCancelled())
                throw new InterruptedException();
            // TODO it would be nice to find a way to abort the request on cancellation
            Response response = client.newCall(request).execute();
            if (isCancelled())
                throw new InterruptedException();
            int statusCode = response.code();
            String body = response.body().string();
            if (statusCode >= 400) {
                Log.d("UV", body);
            }
            if (isCancelled())
                throw new InterruptedException();
            return new RestResult(statusCode, new JSONObject(body));
        } catch (Exception e) {
            return new RestResult(e);
        }
    }

    private Request createRequest() throws URISyntaxException, UnsupportedEncodingException {
        Request.Builder builder = new Request.Builder()
                .addHeader("Accept-Language", Locale.getDefault().getLanguage())
                .addHeader("API-Client", String.format("uservoice-android-%s", UserVoice.getVersion()))
                .addHeader("User-Agent", String.format("uservoice-android-%s", UserVoice.getVersion()));

        String host = Session.getInstance().getConfig(context).getSite();
        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.scheme(host.contains(".us.com") ? "http" : "https");
        uriBuilder.encodedAuthority(host);
        uriBuilder.path(urlPath);

        if (method == RestMethod.GET || method == RestMethod.DELETE) {
            builder.method(method.toString(), null);
            addParamsToQueryString(builder, uriBuilder);
        } else {
            builder.url(uriBuilder.build().toString());
            addParamsToBody(builder);
        }
        return builder.build();
    }

    @Override
    protected void onPostExecute(RestResult result) {
        if (result.isError()) {
            callback.onError(result);
        } else {
            try {
                callback.onComplete(result.getObject());
            } catch (JSONException e) {
                callback.onError(new RestResult(e, result.getStatusCode(), result.getObject()));
            }
        }
        super.onPostExecute(result);
    }

    private void addParamsToQueryString(Request.Builder builder, Uri.Builder uriBuilder) throws URISyntaxException {
        if (params != null) {
            for (Map.Entry<String,String> param : params.entrySet()) {
                uriBuilder.appendQueryParameter(param.getKey(), param.getValue());
            }
        }
        builder.url(uriBuilder.build().toString());
    }

    private void addParamsToBody(Request.Builder builder) throws UnsupportedEncodingException, URISyntaxException {
        if (params != null) {
            FormBody.Builder paramsBuilder = new FormBody.Builder();
            for (Map.Entry<String,String> param : params.entrySet()) {
                paramsBuilder.add(param.getKey(), param.getValue());
            }
            builder.method(method.toString(), paramsBuilder.build());
        }
    }

}
