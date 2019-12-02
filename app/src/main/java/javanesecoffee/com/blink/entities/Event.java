package javanesecoffee.com.blink.entities;

import android.graphics.Bitmap;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import javanesecoffee.com.blink.api.BLinkApiException;
import javanesecoffee.com.blink.api.ImageLoadObserver;
import javanesecoffee.com.blink.api.LoadImageTask;
import javanesecoffee.com.blink.constants.Endpoints;


public class Event {
//    private ArrayList<ImageLoadObserver> observers = new ArrayList<>();
    private String name;
    private String organiser;
    private String description;
    private String address;
    private String date;
    private String time;
    private String price;
    private String event_id;
    private ArrayList<User> participantList = new ArrayList<>();
    private ArrayList<String> tags = new ArrayList<>();

    //TODO: for testing only
    public Event(String name, String organiser, String description, String address, String date, String time, String price, String event_id, ArrayList<String> tag) {
        this.name = name;
        this.organiser = organiser;
        this.description = description;
        this.address = address;
        this.date = date;
        this.time = time;
        this.price = price;
        this.event_id = event_id;
        this.tags = tag;
    }


    public Event(JSONObject data) throws BLinkApiException{
        try {
            this.event_id = data.getString("event_id");
            this.name = data.getString("event_name");
            this.date = data.getString("date");
            this.organiser = data.getString("org_username");
            this.description = data.getString("description");
            this.address = data.getString("address");
            this.time = data.getString("time");
            this.price = data.getString("price");

            JSONArray participantArray = data.getJSONArray("participants");


            for(int i=0; i<participantArray.length(); i++) {
                JSONObject userObject = participantArray.getJSONObject(i);
                User user = new User(userObject);
                participantList.add(user);
            }
            JSONArray tagArray = data.getJSONArray("tags");
            for(int i =0; i<tagArray.length(); i++){
                JSONObject userObject = tagArray.getJSONObject(i);
                tags.add(userObject.toString());
            }

//            Log.d("EVENT", "Event has " + participantList.size() + " participants");

        } catch (JSONException e) {
            e.printStackTrace();
            throw BLinkApiException.MALFORMED_DATA_EXCEPTION();
        }
    }

    public String getDescription() { return description; }

    public String getAddress() { return address; }

    public String getDate() { return date; }

    public String getEvent_id() { return event_id; }

    public String getName() { return name; }

    public String getOrganiser() { return organiser; }

    public String getPrice() { return price; }

    public String getTime() { return time; }

    public ArrayList<User> getParticipantList() {
        return participantList;
    }

    public ArrayList<String> getTags(){ return tags;}
}
