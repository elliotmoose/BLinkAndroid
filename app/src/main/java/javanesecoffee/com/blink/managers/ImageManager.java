package javanesecoffee.com.blink.managers;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

import javanesecoffee.com.blink.R;
import javanesecoffee.com.blink.api.BLinkApiException;
import javanesecoffee.com.blink.api.ImageEntityObserver;
import javanesecoffee.com.blink.api.ImageLoadObserver;
import javanesecoffee.com.blink.api.LoadImageTask;
import javanesecoffee.com.blink.constants.Endpoints;

public class ImageManager implements ImageLoadObserver {
    public static HashMap<String, Bitmap> images = new HashMap<>();
    public static ArrayList<String> loadingImages = new ArrayList<>(); //a list of images that are in the process of being loaded
    public static ImageManager singleton = new ImageManager();

    public static Bitmap dpPlaceholder;
    public static Bitmap eventPlaceholder;

    public static HashMap<String, ArrayList<ImageEntityObserver>> observers = new HashMap<>();

    public static ImageManager getInstance() {
        return singleton;
    }

    public enum ImageType {
        PROFILE_IMAGE,
        EVENT_IMAGE,
        CONNECTION_IMAGE
    }

    public static void initPlaceholders(Context context, Resources resources) {
        dpPlaceholder = BitmapFactory.decodeResource(resources, R.drawable.dp);
        eventPlaceholder = BitmapFactory.decodeResource(resources, R.drawable.placeholderimg);
    }

    /**
     * Requests to retrieve image or load it. If it is loading, subscribe as an observer
     * @param key
     * @param observer
     * @param type
     * @return
     */
    public static Bitmap getImageOrLoadIfNeeded(String key, ImageEntityObserver observer, ImageType type) {


        Bitmap image;
        image = images.get(key);

        if(image == null) {
            //if haven't been loaded, load and subscribe
            if(!loadingImages.contains(key)) {

                //if haven't had any observers
                if(observers.get(key) == null) {
                    ArrayList<ImageEntityObserver> imageObservers = new ArrayList<>();
                    imageObservers.add(observer);
                    observers.put(key, imageObservers);
                }
                else { //register if not registered yet
                    if(!observers.get(key).contains(observer)) {
                        observers.get(key).add(observer);
                    }
                }

//                Log.d("IMAGE_MANAGER", "Loading image for key: " + key);
                loadingImages.add(key);
                loadImageForKey(key, type);
            }
            return null;
        }
        else {
            return image;
        }
    }

    @Override
    public void onImageLoad(Bitmap bitmap, String key) {
        Log.d("IMAGE_MANAGER", "Image loaded for key: " + key);

        //cache image for key
        images.put(key, bitmap);
        loadingImages.remove(key);

        //notify subscriptions (ui observers)
        ArrayList<ImageEntityObserver> imageObservers = observers.get(key);

        if(imageObservers != null) {
            //notify and unsubscribe after
            for(int i=0; i< imageObservers.size(); i++) {
                ImageEntityObserver o = imageObservers.get(i);
//                Log.d("IMAGE_MANAGER", "Updating observer for key: " + key);
                o.onImageUpdated(bitmap);
                imageObservers.remove(o); //won't be updated anymore
            }
        }
    }

    @Override
    public void onImageLoadFailed(BLinkApiException exception, String key) {

    }

    public static void loadImageForKey(String key, ImageType type) {
        LoadImageTask task = new LoadImageTask(getInstance());
        String endpoint = (type == ImageType.CONNECTION_IMAGE ? Endpoints.GET_CONNECTION_IMAGE : (type == ImageType.EVENT_IMAGE ? Endpoints.GET_EVENT_IMAGE : Endpoints.GET_PROFILE_IMAGE));
        task.execute(endpoint, key);
    }

}
