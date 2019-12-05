package javanesecoffee.com.blink.social;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;
import javanesecoffee.com.blink.R;
import javanesecoffee.com.blink.api.BLinkApiException;
import javanesecoffee.com.blink.api.BLinkEventObserver;
import javanesecoffee.com.blink.api.ImageEntityObserver;
import javanesecoffee.com.blink.constants.ApiCodes;
import javanesecoffee.com.blink.constants.IntentExtras;
import javanesecoffee.com.blink.entities.Event;
import javanesecoffee.com.blink.entities.User;
import javanesecoffee.com.blink.helpers.ResponseParser;
import javanesecoffee.com.blink.managers.EventManager;
import javanesecoffee.com.blink.managers.ImageManager;
import javanesecoffee.com.blink.managers.UserManager;

public class UnconnectedUserDetailsActivity extends AppCompatActivity implements ImageEntityObserver, BLinkEventObserver {
    User displayedUser;

    TextView editUsername;
    TextView editDesignation;
    TextView editCompany;
    CircleImageView editProfilePic;

    RecyclerView recyclerView_eventCard;

    ArrayList<Event> events = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.unconnected_user_detail_page);

        editProfilePic = findViewById(R.id.unconnected_profile_pic);
        editUsername = findViewById(R.id.unconnected_loginUsername);
        editDesignation = findViewById(R.id.unconnected_designation);
        editCompany = findViewById(R.id.unconnected_company);

        recyclerView_eventCard = findViewById(R.id.unconnectedUserEventRecyclerView);

        Intent intent = getIntent();
        String username = intent.getStringExtra(IntentExtras.USER.USER_NAME_KEY);

        displayedUser = UserManager.getUserFromCache(username);

        updateData();

        View decorview = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorview.setSystemUiVisibility(uiOptions);

        EventManager.getInstance().registerObserver(this);
        //Load events ids to show
        EventManager.getInstance().loadEventsListForUsername(username);
    }

    @Override
    protected void onDestroy() {
        EventManager.getInstance().deregisterObserver(this);
        super.onDestroy();
    }

    public void updateData() {
        if(displayedUser != null) {
            Log.d("USER_DETAILS_ACTIVITY", displayedUser.getUsername());
            editUsername.setText(displayedUser.getDisplayname());
            editDesignation.setText(displayedUser.getPosition());
            editCompany.setText(displayedUser.getCompany());


            Bitmap image = ImageManager.getImageOrLoadIfNeeded(displayedUser.getUsername(), this, ImageManager.ImageType.PROFILE_IMAGE);
            if(image != null) {
                editProfilePic.setImageBitmap(image);
            }
            else {
                //resets when view is being reused
                editProfilePic.setImageBitmap(ImageManager.dpPlaceholder);
            }
        }
        else {
            Toast.makeText(UnconnectedUserDetailsActivity.this, "Could not load this user.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBLinkEventTriggered(JSONObject response, String taskId) throws BLinkApiException {
        if (taskId == ApiCodes.TASK_LOAD_EVENTS_LIST_FOR_USERNAME) {
            if (ResponseParser.responseIsSuccess(response)) {
                JSONObject data = ResponseParser.dataFromResponse(response);

                Iterator<String> jsonkeys = data.keys();
                ArrayList<String> keys = new ArrayList<>();
                while(jsonkeys.hasNext()) {
                    String key = jsonkeys.next();
                    keys.add(key);
                }

                if (keys.size() != 0) {
                    if(displayedUser != null && displayedUser.getUsername() == keys.get(0)){
                        try {
                            ArrayList<String> event_ids = new ArrayList<>();
                            JSONArray eventIdsArray = data.getJSONArray(keys.get(0));
                            for(int i=0; i<eventIdsArray.length(); i++) {
                                event_ids.add(eventIdsArray.getString(i));
                            }

                            events.clear();

                            //TODO: event_ids to events
                            return;
                        } catch (JSONException e) {
                            throw BLinkApiException.MALFORMED_DATA_EXCEPTION();
                        }
                    }
                }

                Log.d("UNCONNECTED_USER_DETAILS_ACTIVITY", "Cannot find user events");
            }
        }
    }

    @Override
    public void onBLinkEventException(BLinkApiException exception, String taskId) {

    }

    @Override
    public void onImageUpdated(Bitmap bitmap) {
        updateData();
    }
}