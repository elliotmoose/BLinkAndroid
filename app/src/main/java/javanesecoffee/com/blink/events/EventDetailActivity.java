package javanesecoffee.com.blink.events;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

import javanesecoffee.com.blink.BlinkActivity;
import javanesecoffee.com.blink.R;
import javanesecoffee.com.blink.api.BLinkApiException;
import javanesecoffee.com.blink.api.BLinkEventObserver;
import javanesecoffee.com.blink.api.ImageEntityObserver;
import javanesecoffee.com.blink.api.ImageLoadObserver;
import javanesecoffee.com.blink.constants.ApiCodes;
import javanesecoffee.com.blink.constants.IntentExtras;
import javanesecoffee.com.blink.entities.Event;
import javanesecoffee.com.blink.entities.User;
import javanesecoffee.com.blink.helpers.ResponseParser;
import javanesecoffee.com.blink.managers.ConnectionsManager;
import javanesecoffee.com.blink.managers.EventManager;
import javanesecoffee.com.blink.managers.UserManager;
import javanesecoffee.com.blink.registration.FaceScanActivity;
import javanesecoffee.com.blink.registration.RegisterActivity;
import javanesecoffee.com.blink.social.SocialNameCard_RecyclerViewAdapter;
import javanesecoffee.com.blink.social.SocialSummaryFragment;
import javanesecoffee.com.blink.social.SocialTabCard_RecyclerViewAdapter;

public class EventDetailActivity extends BlinkActivity implements BLinkEventObserver  {
    User currentUser;
    Event currentEvent;
    String eventType;
    String eventID;
    int eventPosition;

    TextView eventName;
    TextView eventDate;
    TextView eventTime;
    TextView eventLocation;
    TextView eventPrice;
    TextView eventDescription;


    Button eventRegisterButton;

    RecyclerView eventTags;
    RecyclerView eventAlsoAttending;

    ArrayList<User> alsoAttending = new ArrayList<>();
    ArrayList<String> eventTagList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_detail_page);

        EventManager.getInstance().registerObserver(this);
        //textview
        eventName = findViewById(R.id.event_detail_name);
        eventDate = findViewById(R.id.event_detail_date);
        eventTime = findViewById(R.id.event_detail_time);
        eventLocation = findViewById(R.id.event_detail_location);
        eventPrice = findViewById(R.id.event_detail_price);
        eventDescription = findViewById(R.id.event_detail_description);

        //button
        eventRegisterButton = findViewById(R.id.event_detail_register_button);

        //recyclerview
        eventTags = findViewById(R.id.event_detail_tags);
        eventAlsoAttending = findViewById(R.id.event_also_attending_profile_pic);

        //user
        currentUser = UserManager.getLoggedInUser();

        Intent intent = getIntent();
        eventID = intent.getStringExtra(IntentExtras.EVENT.EVENT_ID_KEY);
        eventType = intent.getStringExtra(IntentExtras.EVENT.EVENT_TYPE_KEY);
        eventPosition = intent.getIntExtra(IntentExtras.EVENT.EVENT_POSITION_KEY,0);

        if (eventID != null) {
            currentEvent = EventManager.getInstance().eventsForType(EventListTypes.valueOf(eventType)).get(eventPosition);
        }

        eventRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(EventDetailActivity.this).
                        setTitle("Confirm")
                        .setMessage("Are you sure you want to register for this event?")
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String username = currentUser.getUsername();
                                String event_id = eventID;
                                EventManager.register(username, event_id);
                                showProgressDialog("Registering...");
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });


        View decorview = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorview.setSystemUiVisibility(uiOptions);
        updateData();
        initRecyclerView();

    }

    @Override
    protected void onDestroy() {
        EventManager.getInstance().deregisterObserver(this);
        super.onDestroy();
    }

    public void updateData(){
        if(currentEvent!=null){
            eventName.setText(currentEvent.getName());
            eventDate.setText(currentEvent.getDate());
            eventTime.setText(currentEvent.getTime());
            eventLocation.setText(currentEvent.getAddress());
            eventPrice.setText(currentEvent.getPrice());
            eventDescription.setText(currentEvent.getDescription());

            eventTagList.clear();
            for(String tag: currentEvent.getTags()){
                eventTagList.add(tag);
            }

            alsoAttending.clear();
            for(User participant : currentEvent.getParticipantList()) {
                alsoAttending.add(participant);
            }

            if(EventListTypes.valueOf(eventType) == EventListTypes.UPCOMING || EventListTypes.valueOf(eventType) == EventListTypes.PAST_EVENTS) {
                eventRegisterButton.setVisibility(View.GONE);
            }
            else {
                eventRegisterButton.setVisibility(View.VISIBLE);
            }
        }
    }

    private void initRecyclerView() {
        EventDetailImageAdapter DetailImage_adapter = new EventDetailImageAdapter(alsoAttending, this);
        EventTagRecyclerViewAdapter Tag_adapter = new EventTagRecyclerViewAdapter(eventTagList,this);
        eventAlsoAttending.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        eventTags.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false));

        EventDetailActivity.HorizontalSpaceItemDecoration spaceDecoration = new EventDetailActivity.HorizontalSpaceItemDecoration(40);
        eventAlsoAttending.addItemDecoration(spaceDecoration);

        eventAlsoAttending.setAdapter(DetailImage_adapter);
        eventTags.setAdapter(Tag_adapter);
    }

    @Override
    public void onBLinkEventTriggered(JSONObject response, String taskId) throws BLinkApiException {
        if(taskId == ApiCodes.TASK_REGISTER_FOR_EVENT)
        {
            hideProgressDialog();
            boolean success = ResponseParser.responseIsSuccess(response);

            if(success)
            {
                //refresh
                EventManager.getInstance().loadEventsList();

                new AlertDialog.Builder(EventDetailActivity.this).
                        setTitle("Success")
                        .setMessage("You have successfully registered for the event!")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        })
                        .show();
            }
            else
            {
                throw ResponseParser.exceptionFromResponse(response);
            }
        }
    }


    @Override
    public void onBLinkEventException(BLinkApiException exception, String taskId) {
        if(taskId == ApiCodes.TASK_REGISTER_FOR_EVENT) {
            hideProgressDialog();
            new AlertDialog.Builder(EventDetailActivity.this).setTitle(exception.statusText).setMessage(exception.message).setPositiveButton("Ok", null).show();
        }
    }

    public class HorizontalSpaceItemDecoration extends RecyclerView.ItemDecoration {
        private final int space;

        public HorizontalSpaceItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.left = space;
        }
    }
}
