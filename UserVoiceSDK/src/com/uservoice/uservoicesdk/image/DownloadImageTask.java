package com.uservoice.uservoicesdk.image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.InputStream;

public class DownloadImageTask extends AsyncTask<Void, Void, Bitmap> {
    private ImageView imageView;
    private final String url;

    public DownloadImageTask(String url, ImageView imageView) {
        this.url = url;
        this.imageView = imageView;
        imageView.setImageBitmap(null);
    }

    protected Bitmap doInBackground(Void... voids) {
        Bitmap bitmap = null;
        InputStream in = null;

        try {
            in = new java.net.URL(url).openStream();
            bitmap = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (Exception ignored) {
            }
        }
        return bitmap;
    }

    protected void onPostExecute(Bitmap bitmap) {
        ImageCache.getInstance().set(url, bitmap);
        imageView.setImageBitmap(bitmap);
    }
}