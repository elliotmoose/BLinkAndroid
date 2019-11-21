package javanesecoffee.com.blink.entities;

import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import javanesecoffee.com.blink.api.BLinkApiException;


public class Event {
    private String name;
    private String organiser;
    private String description;
    private String address;
    private String date;
    private String time;
    private String price;
    private String event_id;

    public Event (JSONObject data) throws BLinkApiException{
        try {
            this.name = data.getString("name");
            this.organiser = data.getString("organiser");
            this.description = data.getString("description");
            this.address = data.getString("address");
            this.date = data.getString("date");
            this.time = data.getString("time");
            this.price = data.getString("price");
            this.event_id = data.getString("event_id");
            this.price = data.getString("price");
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
}
