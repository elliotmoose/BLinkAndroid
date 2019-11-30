package javanesecoffee.com.blink.api;

import android.graphics.Bitmap;

public interface ImageLoadObserver {
    void onImageLoad(Bitmap bitmap, String key);
    void onImageLoadFailed(BLinkApiException exception, String key);
}
