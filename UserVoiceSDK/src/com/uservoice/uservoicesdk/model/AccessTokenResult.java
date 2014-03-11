package com.uservoice.uservoicesdk.model;

public class AccessTokenResult<T> {
    private T model;
    private AccessToken accessToken;

    public AccessTokenResult(T model, AccessToken accessToken) {
        this.model = model;
        this.accessToken = accessToken;
    }

    public T getModel() {
        return model;
    }

    public AccessToken getAccessToken() {
        return accessToken;
    }
}
