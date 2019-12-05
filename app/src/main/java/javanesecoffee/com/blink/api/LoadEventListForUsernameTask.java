package javanesecoffee.com.blink.api;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import javanesecoffee.com.blink.constants.ApiCodes;
import javanesecoffee.com.blink.constants.Endpoints;
import javanesecoffee.com.blink.helpers.RequestHandler;

public class LoadEventListForUsernameTask extends BLinkAsyncTask{
    public LoadEventListForUsernameTask(AsyncResponseHandler responseHandler){
        super(responseHandler, ApiCodes.TASK_LOAD_EVENTS_LIST);
    }
    @Override
    JSONObject executeMainTask(String... params) throws IOException, JSONException, BLinkApiException{
        String username = params[0];

        RequestHandler requestHandler = RequestHandler.PostRequestHandler(Endpoints.LOAD_EVENT_LIST_FOR_USERNAME);
        requestHandler.addPostField("username", username);

        return requestHandler.sendPostRequest();
    }
}
