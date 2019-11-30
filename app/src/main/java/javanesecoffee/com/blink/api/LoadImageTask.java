package javanesecoffee.com.blink.api;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import javanesecoffee.com.blink.helpers.RequestHandler;

public class LoadImageTask extends AsyncTask<String, Void, Bitmap> {

    BLinkApiException exception;
    ImageLoadObserver delegate;
    String key;

    public LoadImageTask(ImageLoadObserver delegate)
    {
        this.delegate = delegate;
    }


    @Override
    protected Bitmap doInBackground(String... params) {
        String endpoint = params[0];
        key = params[1];
        String url_extension = endpoint + key;

        Bitmap image = null;
        try {
            image = RequestHandler.getImage(url_extension);
        } catch (BLinkApiException e) {
            this.exception = e;
        }
        return image;
    }


    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);

        if(delegate != null)
        {
            if(exception == null)
            {
                delegate.onImageLoad(bitmap, key);
            }
            else
            {
                delegate.onImageLoadFailed(this.exception, key);
            }
        }
    }
}
