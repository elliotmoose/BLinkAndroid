package javanesecoffee.com.blink.api;

import android.graphics.Bitmap;

import javanesecoffee.com.blink.managers.ImageManager;

public interface ImageEntityObserver {
    void onImageUpdated(Bitmap bitmap);
}
