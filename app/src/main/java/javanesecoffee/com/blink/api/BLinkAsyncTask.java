package javanesecoffee.com.blink.api;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import javanesecoffee.com.blink.R;
import javanesecoffee.com.blink.helpers.RequestHandler;

/**
 * This Abstract class is created for the sole purpose of standardizing the generics of AsyncTask for which
 * we will use for our API requests. It also demands that a requestHandler be passed in to handle responses
 *
 * This class helps to handle exceptions too
 */


public abstract class BLinkAsyncTask extends AsyncTask<String, Void, JSONObject> {

    public AsyncResponseHandler responseHandler;
    private BLinkApiException exception;
    private String taskId;

    BLinkAsyncTask(AsyncResponseHandler responseHandler, String taskId)
    {
        super();
        this.responseHandler = responseHandler;
        this.taskId = taskId;
    }

    protected void setException(BLinkApiException exception)
    {
        this.exception = exception;
    }

    @Override
    protected JSONObject doInBackground(String... params){
        try {
            return executeMainTask(params);
        } catch (IOException e) {
            e.printStackTrace();
            setException(BLinkApiException.REQUEST_FAILED_EXCEPTION());
            return null;
        }
        catch (JSONException e) {
            e.printStackTrace();
            setException(BLinkApiException.MALFORMED_DATA_EXCEPTION());
            return null;
        }
        catch (BLinkApiException e) {
            e.printStackTrace();
            setException(e);
            return null;
        }
    }

    abstract JSONObject executeMainTask(String... params) throws IOException, JSONException, BLinkApiException;

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        onTaskComplete(jsonObject);

        //complete callback
        if(responseHandler != null)
        {
            if(this.exception == null)
            {
                responseHandler.onAsyncTaskComplete(jsonObject, this.taskId);
            }
            else
            {
                responseHandler.onAsyncTaskFailedWithException(this.exception, this.taskId);
            }
        }
    }

    protected void onTaskComplete(JSONObject jsonObject)
    {

    }
}
