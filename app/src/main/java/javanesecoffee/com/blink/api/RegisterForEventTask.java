package javanesecoffee.com.blink.api;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import javanesecoffee.com.blink.constants.ApiCodes;
import javanesecoffee.com.blink.constants.Endpoints;
import javanesecoffee.com.blink.helpers.RequestHandler;

public class RegisterForEventTask extends BLinkAsyncTask {
    public RegisterForEventTask(AsyncResponseHandler requestHandler)
    {
        super(requestHandler, ApiCodes.TASK_REGISTER_FOR_EVENT);
    }

    @Override
    JSONObject executeMainTask(String... params) throws IOException, JSONException, BLinkApiException {
        String username = params[0];
        String event_id = params[1];

        RequestHandler requestHandler = RequestHandler.PostRequestHandler(Endpoints.REGISTER_EVENT);
        requestHandler.addPostField("username", username);
        requestHandler.addPostField("event_id", event_id);
        return requestHandler.sendPostRequest();
    }
}
