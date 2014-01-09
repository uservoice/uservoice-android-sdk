package com.uservoice.uservoicesdk.rest;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import oauth.signpost.OAuthConsumer;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;

import com.uservoice.uservoicesdk.Session;
import com.uservoice.uservoicesdk.UserVoice;
import com.uservoice.uservoicesdk.model.AccessToken;

public class RestTask extends AsyncTask<String, String, RestResult> {
    private String urlPath;
    private RestMethod method;
    private List<BasicNameValuePair> params;
    private RestTaskCallback callback;
    private HttpUriRequest request;

    public RestTask(RestMethod method, String urlPath, Map<String, String> params, RestTaskCallback callback) {
        this(method, urlPath, params == null ? null : paramsToList(params), callback);
    }

    public RestTask(RestMethod method, String urlPath, List<BasicNameValuePair> params, RestTaskCallback callback) {
        this.method = method;
        this.urlPath = urlPath;
        this.callback = callback;
        this.params = params;
    }

    @Override
    protected RestResult doInBackground(String... args) {
        AndroidHttpClient client = null;
        try {
            request = createRequest();
            if (isCancelled())
                throw new InterruptedException();
            OAuthConsumer consumer = Session.getInstance().getOAuthConsumer();
            if (consumer != null) {
                AccessToken accessToken = Session.getInstance().getAccessToken();
                if (accessToken != null) {
                    consumer.setTokenWithSecret(accessToken.getKey(), accessToken.getSecret());
                }
                consumer.sign(request);
            }
            request.setHeader("Accept-Language", Locale.getDefault().getLanguage());
            request.setHeader("API-Client", String.format("uservoice-android-%s", UserVoice.getVersion()));
            client = AndroidHttpClient.newInstance(String.format("uservoice-android-%s", UserVoice.getVersion()), Session.getInstance().getContext());
            if (isCancelled())
                throw new InterruptedException();
            // TODO it would be nice to find a way to abort the request on cancellation
            HttpResponse response = client.execute(request);
            if (isCancelled())
                throw new InterruptedException();
            HttpEntity responseEntity = response.getEntity();
            StatusLine responseStatus = response.getStatusLine();
            int statusCode = responseStatus != null ? responseStatus.getStatusCode() : 0;
            String body = responseEntity != null ? EntityUtils.toString(responseEntity) : null;
            if (isCancelled())
                throw new InterruptedException();
            return new RestResult(statusCode, new JSONObject(body));
        } catch (Exception e) {
            return new RestResult(e);
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }

    private HttpUriRequest createRequest() throws URISyntaxException, UnsupportedEncodingException {
        String host = Session.getInstance().getConfig().getSite();
        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.scheme(host.contains(".us.com") ? "http" : "https");
        uriBuilder.encodedAuthority(host);
        uriBuilder.path(urlPath);
        if (method == RestMethod.GET)
            return requestWithQueryString(new HttpGet(), uriBuilder);
        else if (method == RestMethod.DELETE)
            return requestWithQueryString(new HttpDelete(), uriBuilder);
        else if (method == RestMethod.POST)
            return requestWithEntity(new HttpPost(), uriBuilder);
        else if (method == RestMethod.PUT)
            return requestWithEntity(new HttpPut(), uriBuilder);
        else
            throw new IllegalArgumentException("Method must be one of [GET, POST, PUT, DELETE], but was " + method);
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

    private HttpUriRequest requestWithQueryString(HttpRequestBase request, Uri.Builder uriBuilder) throws URISyntaxException {
        if (params != null) {
            for (BasicNameValuePair param : params) {
                uriBuilder.appendQueryParameter(param.getName(), param.getValue());
            }
        }
        request.setURI(new URI(uriBuilder.build().toString()));
        return request;
    }

    private HttpUriRequest requestWithEntity(HttpEntityEnclosingRequestBase request, Uri.Builder uriBuilder) throws UnsupportedEncodingException, URISyntaxException {
        if (params != null) {
            request.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
        }
        request.setURI(new URI(uriBuilder.build().toString()));
        return request;
    }

    public static List<BasicNameValuePair> paramsToList(Map<String, String> params) {
        ArrayList<BasicNameValuePair> formList = new ArrayList<BasicNameValuePair>(params.size());
        for (String key : params.keySet()) {
            String value = params.get(key);
            if (value != null)
                formList.add(new BasicNameValuePair(key, value));
        }
        return formList;
    }
}
