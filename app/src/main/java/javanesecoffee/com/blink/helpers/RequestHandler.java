package javanesecoffee.com.blink.helpers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;

import javanesecoffee.com.blink.api.BLinkApiException;
import javanesecoffee.com.blink.constants.Config;

// This is a helper class for register request only
public class RequestHandler {
    private static String DEFAULT_CHARSET = "UTF-8";
    private static final String LINE_FEED = "\r\n";

    private String requestUrl;
    private String requestType;

    //FOR FORM DATA REQUESTS
    private String boundary = "";
    private HttpURLConnection httpConn;
    private OutputStream outputStream;

    private PrintWriter writer;


    //FOR POST REQUESTS
    private HashMap<String, String> postDataParams = new HashMap<>();


    private static final String DOMAIN = Config.DOMAIN;

    public static RequestHandler FormRequestHandler(String endpoint)  throws BLinkApiException{
        RequestHandler handler = new RequestHandler(endpoint, "FORMDATA");

        handler.boundary = "" + System.currentTimeMillis() + "";

        try{
            URL url = new URL(handler.requestUrl);
            handler.httpConn = (HttpURLConnection) url.openConnection();
            handler.httpConn.setUseCaches(false);
            handler.httpConn.setDoOutput(true);    // indicates POST method
            handler.httpConn.setDoInput(true);
            handler.httpConn.setRequestProperty("Content-Type",
                    "multipart/form-data; boundary=" + handler.boundary);
            handler.outputStream = handler.httpConn.getOutputStream();

            handler.writer = new PrintWriter(new OutputStreamWriter(handler.outputStream, DEFAULT_CHARSET),
                    true);
        }catch (Exception e) {
            e.printStackTrace();
            throw new BLinkApiException("NO_CONNECTION", "Connection Failed", "Could not connect to server");
        }

        return handler;
    }

    public static RequestHandler PostRequestHandler(String endpoint)
    {
        return new RequestHandler(endpoint,"POST");
    }

    public RequestHandler(String endpoint, String requestType){
        this.requestUrl = DOMAIN + endpoint;
        this.requestType = requestType;
    }

    //this method will send a post request to the specified url
    //in this app we are using only post request
    //in the hashmap we have the data to be sent to the server in keyvalue pairs
    public JSONObject sendPostRequest() throws IOException, JSONException, BLinkApiException{

        if(this.requestType != "POST")
        {
            throw new BLinkApiException("NOT_POST_REQUEST", "Not Post Request", "This request is not POST");
        }

        URL url = new URL(requestUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(15000);
        conn.setConnectTimeout(15000);
        conn.setRequestMethod("POST");
        conn.setDoInput(true);
        conn.setDoOutput(true);

        OutputStream os = conn.getOutputStream();

        BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(os, DEFAULT_CHARSET));
        writer.write(getPostDataString(postDataParams));

        writer.flush();
        writer.close();
        os.close();
        int responseCode = conn.getResponseCode();

        if (responseCode == HttpsURLConnection.HTTP_OK) {
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String response;

            while ((response = br.readLine()) != null) {
                sb.append(response);
            }

            return new JSONObject(sb.toString());
        }
        else {
            throw new IOException("Server returned non-OK status: " + responseCode);
        }
    }

    /**
     * Adds a form field to the request
     *
     * @param params HashMap<param, value>
     */
    //this method is converting keyvalue pairs data into a query string as needed to send to the server
    private static String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }


    /**
     * Adds a form field to the request
     *
     * @param name  field name
     * @param value field value
     */
    public void addFormField(String name, String value) throws BLinkApiException {

        if(this.requestType != "FORMDATA")
        {
            throw new BLinkApiException("NOT_FORMDATA_REQUEST", "Not FormData Request", "This request is not FORMDATA");
        }

        writer.append("--" + boundary).append(LINE_FEED);
        writer.append("Content-Disposition: form-data; name=\"" + name + "\"")
                .append(LINE_FEED);
        writer.append("Content-Type: text/plain; charset=" + DEFAULT_CHARSET).append(
                LINE_FEED);
        writer.append(LINE_FEED);
        writer.append(value).append(LINE_FEED);
        writer.flush();
    }

    /**
     * Adds a form field to the request
     *
     * @param name  field name
     * @param value field value
     */

    public void addPostField(String name, String value) throws BLinkApiException {

        if(this.requestType != "POST")
        {
            throw new BLinkApiException("NOT_POST_REQUEST", "Not Post Request", "This request is not POST");
        }

        this.postDataParams.put(name, value);
    }

    /**
     * Adds a upload file section to the request
     *
     * @param fieldName  name attribute in <input type="file" name="..." />
     * @param uploadFile a File to be uploaded
     * @throws IOException
     */
    public void addFilePart(String fieldName, File uploadFile)
            throws IOException, BLinkApiException {

        if(this.requestType != "FORMDATA")
        {
            throw new BLinkApiException("NOT_FORMDATA_REQUEST", "Not FormData Request", "This request is not FORMDATA");
        }

        String fileName = uploadFile.getName();
        writer.append("--" + boundary).append(LINE_FEED);
        writer.append(
                "Content-Disposition: form-data; name=\"" + fieldName
                        + "\"; filename=\"" + fileName + "\"")
                .append(LINE_FEED);
        writer.append(
                "Content-Type: "
                        + HttpsURLConnection.guessContentTypeFromName(fileName))
                .append(LINE_FEED);
        writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
        writer.append(LINE_FEED);
        writer.flush();

        FileInputStream inputStream = new FileInputStream(uploadFile);
        byte[] buffer = new byte[4096];
        int bytesRead = -1;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        outputStream.flush();
        inputStream.close();
        writer.append(LINE_FEED);
        writer.flush();
    }

    /**
     * Adds a header field to the request.
     *
     * @param name  - name of the header field
     * @param value - value of the header field
     */
    public void addHeaderField(String name, String value) {
        writer.append(name + ": " + value).append(LINE_FEED);
        writer.flush();
    }

    /**
     * Completes the request and receives response from the server.
     *
     * @return a list of Strings as response in case the server returned
     * status OK, otherwise an exception is thrown.
     * @throws IOException
     */
    public JSONObject sendFormDataRequest() throws IOException, JSONException, BLinkApiException {

        if(this.requestType != "FORMDATA")
        {
            throw new BLinkApiException("NOT_FORMDATA_REQUEST", "Not FormData Request", "This request is not FORMDATA");
        }

        String responseString = "";
        writer.append(LINE_FEED).flush();
        writer.append("--" + boundary + "--").append(LINE_FEED);
        writer.close();
        outputStream.close();

        // checks server's status code first
        int status = httpConn.getResponseCode();
        if (status == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    httpConn.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                responseString += line;
            }
            reader.close();
            httpConn.disconnect();
        } else {
            System.out.println(httpConn.getErrorStream());
            throw new IOException("Server returned non-OK status: " + status);
        }

        //PARSE INTO JSON OBJECT
        return new JSONObject(responseString);
    }

    public static Bitmap getImage(String endpoint) throws BLinkApiException {
        try {
            String src = DOMAIN + endpoint;
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            e.printStackTrace();
            throw BLinkApiException.REQUEST_FAILED_EXCEPTION();
        }
    }
}
