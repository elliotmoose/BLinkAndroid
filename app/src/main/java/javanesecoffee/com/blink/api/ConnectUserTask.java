package javanesecoffee.com.blink.api;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import javanesecoffee.com.blink.constants.ApiCodes;
import javanesecoffee.com.blink.constants.Endpoints;
import javanesecoffee.com.blink.helpers.RequestHandler;

public class ConnectUserTask extends BLinkAsyncTask{

    private File image_file;

    public ConnectUserTask(AsyncResponseHandler responseHandler){
        super(responseHandler, ApiCodes.TASK_CONNECT_USERS);
    }
    @Override
    JSONObject executeMainTask(String... params) throws IOException, JSONException, BLinkApiException {
        String username = params[0];
        String image_path = params[1];

        image_file = new File(image_path);

        RequestHandler request_handler = RequestHandler.FormRequestHandler(Endpoints.CONNECT_USERS);
        request_handler.addFormField("username", username);
        request_handler.addFilePart("image_file", image_file);

        return request_handler.sendFormDataRequest();
    }

    @Override
    protected void onTaskComplete(JSONObject jsonObject){
        if(image_file != null){
//            image_file.delete();
        }
    }
}
