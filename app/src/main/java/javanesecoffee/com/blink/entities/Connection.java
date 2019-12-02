package javanesecoffee.com.blink.entities;

import android.graphics.Bitmap;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import javanesecoffee.com.blink.api.BLinkApiException;
import javanesecoffee.com.blink.api.ImageLoadObserver;
import javanesecoffee.com.blink.api.LoadImageTask;
import javanesecoffee.com.blink.constants.Endpoints;

public class Connection{
    private String username;
    private String connection_id;
    private String image_id;

    public Connection(String username) {
        this.username = username;
    }
    /**
     *
     * @param data input response.data json object for connection
     */
    public Connection(JSONObject data) throws BLinkApiException {
        try {
            this.username = data.getString("username");

            try {
                this.connection_id = data.getString("connection_id");
                this.image_id = data.getString("image_id");
            }  catch (JSONException e) {
                e.printStackTrace();
            }

        } catch (JSONException e) {
            e.printStackTrace();
            throw BLinkApiException.MALFORMED_DATA_EXCEPTION();
        }
    }

    public String getUsername() {
        return username;
    }

    public String getConnection_id() {
        return connection_id;
    }

    public String getImage_id() {
        return image_id;
    }
}
