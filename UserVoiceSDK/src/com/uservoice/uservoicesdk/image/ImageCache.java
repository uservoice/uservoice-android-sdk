package com.uservoice.uservoicesdk.image;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.graphics.Bitmap;
import android.widget.ImageView;

public class ImageCache {
	
	private static ImageCache instance;
	public static ImageCache getInstance() {
		if (instance == null) {
			instance = new ImageCache();
		}
		return instance;
	}
	
	private int capacity = 20;
	private Map<String,Bitmap> cache = new HashMap<String,Bitmap>(capacity);
	private List<String> mru = new ArrayList<String>();
	
	public void loadImage(String url, ImageView imageView) {
		if (cache.containsKey(url)) {
			imageView.setImageBitmap(cache.get(url));
			mru.remove(url);
			mru.add(url);
		} else {
			new DownloadImageTask(url, imageView).execute();
		}
	}
	
	public void set(String url, Bitmap bitmap) {
		if (cache.containsKey(url)) {
			cache.put(url, bitmap);
			mru.remove(url);
			mru.add(url);
		} else {
			if (cache.size() == capacity) {
				String lru = mru.get(0);
				cache.remove(lru);
				mru.remove(0);
			}
			cache.put(url, bitmap);
			mru.add(url);
		}
	}
	
	public void purge() {
		cache.clear();
		mru.clear();
	}

}
