package javanesecoffee.com.blink.api;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import javanesecoffee.com.blink.constants.ApiCodes;
import javanesecoffee.com.blink.constants.Endpoints;
import javanesecoffee.com.blink.helpers.RequestHandler;

public class UndoConnectTask extends BLinkAsyncTask{

    public UndoConnectTask(AsyncResponseHandler responseHandler){
        super(responseHandler, ApiCodes.TASK_UNDO_CONNECT);
    }
    @Override
    JSONObject executeMainTask(String... params) throws IOException, JSONException, BLinkApiException {
        String connection_id = params[0];

        RequestHandler request_handler = RequestHandler.PostRequestHandler(Endpoints.UNDO_CONNECT);
        request_handler.addFormField("connection_id", connection_id);
        return request_handler.sendPostRequest();
    }
}
