package javanesecoffee.com.blink.api;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import javanesecoffee.com.blink.constants.ApiCodes;
import javanesecoffee.com.blink.constants.Endpoints;
import javanesecoffee.com.blink.helpers.RequestHandler;


//TODO: update the postParams into the request
public class UpdateFCMTokenTask extends BLinkAsyncTask {
    public UpdateFCMTokenTask(AsyncResponseHandler requestHandler)
    {
        super(requestHandler, ApiCodes.TASK_UPDATE_FCM_TOKEN);
    }

    @Override
    JSONObject executeMainTask(String... params) throws IOException, JSONException, BLinkApiException{
        String username = params[0];
        String token = params[1];

        RequestHandler requestHandler = RequestHandler.PostRequestHandler(Endpoints.UPDATE_FCM_TOKEN);
        requestHandler.addPostField("username", username);
        requestHandler.addPostField("token", token);

        return requestHandler.sendPostRequest();
    }
}
