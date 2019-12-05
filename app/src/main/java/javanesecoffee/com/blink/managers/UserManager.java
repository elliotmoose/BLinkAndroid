package javanesecoffee.com.blink.managers;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

import javanesecoffee.com.blink.api.BLinkApiException;
import javanesecoffee.com.blink.api.LoginTask;
import javanesecoffee.com.blink.api.MoreInfoTask;
import javanesecoffee.com.blink.api.RegisterFaceTask;
import javanesecoffee.com.blink.api.RegisterTask;
import javanesecoffee.com.blink.constants.ApiCodes;
import javanesecoffee.com.blink.entities.User;
import javanesecoffee.com.blink.helpers.ResponseParser;


public class UserManager extends Manager{

    public static UserManager singleton = new UserManager();
    public static UserManager getInstance() {
        return singleton;
    }

    private static User loggedInUser;

    public static User getLoggedInUser() {
        return loggedInUser;
    }

    public static HashMap<String, User> userCache = new HashMap<>();

    /**
     * Send a login request to the URL
     *
     * @param username username for login
     * @param password password for login
     */
    public static void login(String username, String password)
    {
        LoginTask task = new LoginTask(getInstance()); //pass singleton in as handler
        task.execute(username, password); //pass in params

    }

    public static void setLoggedInUser(User loggedInUser) {
        UserManager.loggedInUser = loggedInUser;
        Log.d("UserManager", "Logged in as user: " + loggedInUser.getUsername());

        //update fcm
        BLinkFirebaseMessagingService.getInstance().updateUserTokenForPushNotifications();
    }

    /**
     * Send a register request to the URL
     *
     * @param username username for login
     * @param password password for login
     * @param displayname email of user
     * @param email email of user
     *
     */

    public static void register(String username, String password, String displayname, String email){
        RegisterTask task = new RegisterTask(getInstance()); //pass singleton in as handler
        task.execute(username, password, displayname ,email); //pass in params
    }

    public static void registerFace(File image_file, String username){
        RegisterFaceTask task = new RegisterFaceTask(getInstance()); //pass singleton in as handler
        task.execute(username, image_file.getPath()); //pass in params
    }

    public static void registerMoreInfo(String bio, String position, String company, String linkedin, String facebook, String instagram) throws BLinkApiException{
        User user = getLoggedInUser();

        if(user != null && user.getUsername() != "") {
            MoreInfoTask task = new MoreInfoTask(getInstance()); //pass singleton in as handler
            task.execute(user.getUsername(), bio, position, company, linkedin, facebook, instagram); //pass in params
        }
        else
        {
            throw new BLinkApiException("MORE_INFO_ERROR", "More Info Error", "Invalid user. Please try again.");
        }
    }

    public static void addUserToCache(User user){
        if(!userCache .containsKey(user.getUsername())){
            userCache.put(user.getUsername(), user);
            Log.d("UserManager", "User cache added: " + user.getUsername());
        }
    }

    public static User getUserFromCache( String username){
        if(getLoggedInUser() != null) {
            if(getLoggedInUser().getUsername() == username) {
                return getLoggedInUser();
            }
        }
        Log.d("UserManager", "Getting : " + username);
        return userCache.get(username);
    }



    @Override //UserManager on async task complete, call super to notify observers
    public void onAsyncTaskComplete(JSONObject response, String taskId) {


        String status = null;

        switch (taskId)
        {

            case ApiCodes.TASK_REGISTER_FACE: //handle by user interface
                break;

            case ApiCodes.TASK_LOGIN:
            case ApiCodes.TASK_REGISTER:

                try {
                    boolean success = ResponseParser.responseIsSuccess(response);
                    if(success)
                    {
                        JSONObject data = ResponseParser.dataFromResponse(response);
                        User user = new User(data);
                        setLoggedInUser(user);
                    }
                } catch (BLinkApiException e) {
                    e.printStackTrace();
                }
                break;

            case ApiCodes.TASK_MORE_INFO:
                try {
                    boolean success = ResponseParser.responseIsSuccess(response);
                    if(success)
                    {
                        //TODO send More info POST to server
                        Log.d("UserManager", "MORE_INFO_TASK performed succesfully");
                    }
                } catch (BLinkApiException e) {
                    e.printStackTrace();
                }
                break;

            default:
                Log.d("UserManager", "Unhandled Async Task Completion");
        }

        super.onAsyncTaskComplete(response, taskId); //notify observers
    }
}
