package javanesecoffee.com.blink.entities;

import android.graphics.Bitmap;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javanesecoffee.com.blink.api.BLinkApiException;
import javanesecoffee.com.blink.api.ImageLoadObserver;
import javanesecoffee.com.blink.api.LoadImageTask;
import javanesecoffee.com.blink.constants.Endpoints;

public class User implements ImageLoadObserver {

    private ArrayList<ImageLoadObserver> observers = new ArrayList<>();
    private String username;
    private String displayname;
    private String bio;
    private String email;
    private String position;
    private String company;
    private String linkedin;
    private String facebook;
    private String instagram;
    private Bitmap profilepicture;


    public User(String username) {
        this.username = username;
        displayname = "";
        bio = "";
        email = "";
        position = "";
        company = "";
        linkedin = "";
        facebook = "";
        instagram = "";
    }
    /**
     *
     * @param data input response.data json object for user
     */
    public User(JSONObject data) throws BLinkApiException {
        try {
            this.username = data.getString("username");
            this.displayname = data.getString("displayname");
            this.bio = data.getString("bio");
            this.email = data.getString("email");
            this.position = data.getString("position");
            this.company = data.getString("company");
            this.linkedin = data.getString("linkedin");
            this.facebook = data.getString("facebook");
            this.instagram = data.getString("instagram");
        } catch (JSONException e) {
            e.printStackTrace();
            throw BLinkApiException.MALFORMED_DATA_EXCEPTION();
        }

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }
    public String getDisplayname() { return displayname; }
    public String getCompany() { return company; }
    public String getBio() { return bio; }
    public Bitmap getProfilepictureAndLoadIfNeeded(ImageLoadObserver onFinishedObserver) {
        if (this.profilepicture == null) {
            LoadImage(onFinishedObserver);
            return null;
        }
        else {
            return profilepicture;
        }
    }
    public String getPosition() { return position; }
    public String getLinkedin() { return linkedin; }
    public String getFacebook() { return facebook; }
    public String getInstagram() { return instagram; }

    private void LoadImage(ImageLoadObserver onFinishedObserver) {
        registerObserver(onFinishedObserver); //only notify once, so will remove once loaded

        if(this.username != "")
        {
            LoadImageTask task = new LoadImageTask(this);
            task.execute(Endpoints.GET_PROFILE_IMAGE, this.username);
        }
        else
        {
            Log.e("User_Error", "User has no valid username");
        }
    }

    @Override
    public void onImageLoad(Bitmap bitmap) {
        this.profilepicture = bitmap;
        notifyAllObserversImageLoaded(bitmap);
    }

    @Override
    public void onImageLoadFailed(BLinkApiException exception) {
        Log.e("User_Error", exception.message);
    }

    public void registerObserver(ImageLoadObserver o) {
        if(!observers.contains(o)) {
            observers.add(o);
        }
    }

    public void deregisterObserver(ImageLoadObserver o) {
        if(observers.contains(o)) {
            observers.remove(o);
        }
    }

    public void notifyAllObserversImageLoaded(Bitmap bitmap) {
        for (ImageLoadObserver o: observers) {
            o.onImageLoad(bitmap);
            deregisterObserver(o); //only notify once
        }
    }
}
