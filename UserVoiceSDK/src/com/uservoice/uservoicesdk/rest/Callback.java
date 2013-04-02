package com.uservoice.uservoicesdk.rest;

public abstract class Callback<T> {
	public abstract void onModel(T model);
	public abstract void onError(RestResult error);
}
