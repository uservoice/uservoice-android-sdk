package com.uservoice.uservoicesdk.rest;

import okhttp3.Request;

import oauth.signpost.AbstractOAuthConsumer;
import oauth.signpost.http.HttpRequest;

public class OkOAuthConsumer extends AbstractOAuthConsumer {

    public OkOAuthConsumer(String consumerKey, String consumerSecret) {
        super(consumerKey, consumerSecret);
    }

    @Override
    protected HttpRequest wrap(Object request) {
        return new OkRequestAdapter((Request) request);
    }
}
