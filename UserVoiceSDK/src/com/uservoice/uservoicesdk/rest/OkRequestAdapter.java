package com.uservoice.uservoicesdk.rest;

import android.util.Log;

import okhttp3.Headers;
import okhttp3.Request;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import oauth.signpost.http.HttpRequest;
import okio.Buffer;

public class OkRequestAdapter implements HttpRequest {

    private Request request;

    public OkRequestAdapter(Request request) {
        this.request = request;
    }

    @Override
    public String getMethod() {
        return request.method();
    }

    @Override
    public String getRequestUrl() {
        return request.url().toString();
    }

    @Override
    public void setRequestUrl(String url) {
        request = new Request.Builder()
                .method(request.method(), request.body())
                .url(url)
                .headers(request.headers())
                .build();
    }

    @Override
    public void setHeader(String name, String value) {
        Headers newHeaders = request.headers().newBuilder().add(name, value).build();
        request = new Request.Builder()
                .headers(newHeaders)
                .url(request.url())
                .method(request.method(), request.body())
                .build();
    }

    @Override
    public String getHeader(String name) {
        return request.header(name);
    }

    @Override
    public Map<String, String> getAllHeaders() {
        Map<String, String>headers = new HashMap<>();
        for (String name : request.headers().names()) {
            headers.put(name, request.header(name));
        }
        return headers;
    }

    @Override
    public InputStream getMessagePayload() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream((int) request.body().contentLength());
        Buffer sink = new Buffer();
        request.body().writeTo(sink);
        return sink.inputStream();
    }

    @Override
    public String getContentType() {
        if (request.body() != null) {
            return request.body().contentType().toString();
        }
        return null;
    }

    @Override
    public Object unwrap() {
        return request;
    }
}
