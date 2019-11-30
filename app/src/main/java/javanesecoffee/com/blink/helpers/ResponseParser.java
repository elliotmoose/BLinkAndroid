package javanesecoffee.com.blink.helpers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javanesecoffee.com.blink.api.BLinkApiException;

public class ResponseParser {
    public static boolean ResponseIsSuccess(JSONObject response) throws BLinkApiException
    {
        String status = "";
        try {
            status = response.getString("status");
        } catch (JSONException e) {
            e.printStackTrace();
            throw BLinkApiException.MALFORMED_DATA_EXCEPTION();
        }

        if(status != null)
        {
            if(status.equals("SUCCESS"))
            {
                return true;
            }
            else
            {
                //throws server given error
                throw ExceptionFromResponse(response);
            }
        }
        else
        {
            throw BLinkApiException.MALFORMED_DATA_EXCEPTION();
        }
    }

    public static JSONObject DataFromResponse(JSONObject response) throws BLinkApiException
    {
        try {
            JSONObject data = response.getJSONObject("data");
            return data;
        } catch (JSONException e) {
            e.printStackTrace();
            throw BLinkApiException.MALFORMED_DATA_EXCEPTION();
        }
    }

    public static JSONArray ArrayDataFromResponse(JSONObject response) throws BLinkApiException
    {
        try {
            JSONArray data = response.getJSONArray("data");
            return data;
        } catch (JSONException e) {
            e.printStackTrace();
            throw BLinkApiException.MALFORMED_DATA_EXCEPTION();
        }
    }

    public static BLinkApiException ExceptionFromData(JSONObject data) throws BLinkApiException
    {
        try {
            String status = data.getString("status");
            String statusText = data.getString("statusText");
            String message = data.getString("message");
            return new BLinkApiException(status, statusText, message);

        } catch (JSONException e) {
            e.printStackTrace();
            throw BLinkApiException.MALFORMED_DATA_EXCEPTION();
        }
    }

    public static BLinkApiException ExceptionFromResponse(JSONObject response) throws BLinkApiException
    {
        return ExceptionFromData(DataFromResponse(response));
    }

}
