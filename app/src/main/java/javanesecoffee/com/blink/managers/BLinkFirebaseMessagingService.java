package javanesecoffee.com.blink.managers;

import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessagingService;

import org.json.JSONObject;

import androidx.annotation.NonNull;
import javanesecoffee.com.blink.api.AsyncResponseHandler;
import javanesecoffee.com.blink.api.BLinkApiException;
import javanesecoffee.com.blink.api.UpdateFCMTokenTask;
import javanesecoffee.com.blink.constants.ApiCodes;
import javanesecoffee.com.blink.entities.User;
import javanesecoffee.com.blink.helpers.ResponseParser;

public class BLinkFirebaseMessagingService extends FirebaseMessagingService implements AsyncResponseHandler {

    public static BLinkFirebaseMessagingService singleton = new BLinkFirebaseMessagingService();

    public static BLinkFirebaseMessagingService getInstance() {
        return singleton;
    }

    private static String TAG = "FIREBASE_MESSAGING_SERVICE";
    private static String token;

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);

        Log.d(TAG, token);
        updateUserTokenForPushNotifications(token);
    }

    public void updateUserTokenForPushNotifications() {

        if(token != null) {
            updateUserTokenForPushNotifications(token);
            return;
        }

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        updateUserTokenForPushNotifications(token);
                    }
                });

    }
    public void updateUserTokenForPushNotifications(String token) {
        BLinkFirebaseMessagingService.token = token;

        User user = UserManager.getLoggedInUser();
        if(user == null) {
            Log.w(TAG, "No logged in user");
            return;
        }

        String username = user.getUsername();

        UpdateFCMTokenTask task = new UpdateFCMTokenTask(this);
        task.execute(username, token);
    }

    @Override
    public void onAsyncTaskComplete(JSONObject response, String taskId) {
        if(taskId == ApiCodes.TASK_UPDATE_FCM_TOKEN) {
            try {
                if(ResponseParser.responseIsSuccess(response)) {
                    Log.d(TAG, "Token updated");
                }
                else {
                    Log.w(TAG, "Token update failed");
                }
            } catch (BLinkApiException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onAsyncTaskFailedWithException(BLinkApiException exception, String taskId) {
        exception.printStackTrace();
    }
}
