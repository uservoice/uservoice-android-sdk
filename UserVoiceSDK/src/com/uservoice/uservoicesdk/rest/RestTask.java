package com.uservoice.uservoicesdk.rest;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import android.os.AsyncTask;

public class RestTask extends AsyncTask<String,String,RestResult> {
	private String url;
	private RestMethod method;
	private Map<String,String> params;
	private RestTaskCallback callback;
	
    public RestTask(RestMethod method, String url, Map<String,String> params, RestTaskCallback callback){
    	this.method = method;
        this.url = url;
        this.callback = callback;
        this.params = params;
    }

	@Override
	protected RestResult doInBackground(String... args) {
	    try {            
	        HttpRequestBase request = createRequest();
	        
	        if (request != null) {
	            HttpClient client = new DefaultHttpClient();
	            HttpResponse response = client.execute(request);
	            HttpEntity responseEntity = response.getEntity();
	            StatusLine responseStatus = response.getStatusLine();
	            int statusCode = responseStatus != null ? responseStatus.getStatusCode() : 0;
	            String body = responseEntity != null ? EntityUtils.toString(responseEntity) : null;
	            try {
					return new RestResult(statusCode, new JSONObject(body));
				} catch (JSONException e) {
					return new RestResult(e);
				}
	        } else {
	        	return new RestResult(new Exception("Could not create an http request"));
	        }
	    } catch (URISyntaxException e) {
	    	return new RestResult(e);
	    } catch (UnsupportedEncodingException e) {
	    	return new RestResult(e);
	    } catch (ClientProtocolException e) {
	    	return new RestResult(e);
	    } catch (IOException e) {
	    	return new RestResult(e);
	    }
	}

	private HttpRequestBase createRequest() throws URISyntaxException, UnsupportedEncodingException {
		if (method == RestMethod.GET) {
	        HttpGet request = new HttpGet();
	        attachUriWithQuery(request, url, params);
	        return request;
		} else if (method == RestMethod.DELETE) {
	        HttpDelete request = new HttpDelete();
	        attachUriWithQuery(request, url, params);
	        return request;
		} else if (method == RestMethod.POST) {
	        HttpPost request = new HttpPost();
	        request.setURI(new URI(url));
	        if (params != null) {
	            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(paramsToList(params));
	            request.setEntity(formEntity);
	        }
	        return request;
		} else if (method == RestMethod.PUT) {
	        HttpPut request = new HttpPut();
	        request.setURI(new URI(url));
	        if (params != null) {
	            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(paramsToList(params));
	            request.setEntity(formEntity);
	        }
	        return request;
		} else {
			throw new IllegalArgumentException("Method must be one of [GET, POST, PUT, DELETE], but was " + method);
		}
	}

    @Override
    protected void onPostExecute(RestResult result) {
    	if (result.isError()) {
    		callback.onError(result);
    	} else {
    		callback.onComplete(result.getObject());
    	}
        super.onPostExecute(result);
    }
    
    private static void attachUriWithQuery(HttpRequestBase request, String url, Map<String,String> params) {
        try {
            if (params == null) {
                request.setURI(new URI(url));
            } else {
                Uri.Builder uriBuilder = Uri.parse(url).buildUpon();
                for (BasicNameValuePair param : paramsToList(params)) {
                    uriBuilder.appendQueryParameter(param.getName(), param.getValue());
                }
                request.setURI(new URI(uriBuilder.build().toString()));
            }
        } catch (URISyntaxException e) {
        }
    }
    
    private static List<BasicNameValuePair> paramsToList(Map<String,String> params) {
        ArrayList<BasicNameValuePair> formList = new ArrayList<BasicNameValuePair>(params.size());
        for (String key : params.keySet()) {
            String value = params.get(key);
            if (value != null)
            	formList.add(new BasicNameValuePair(key, value));
        }
        return formList;
    }
}
