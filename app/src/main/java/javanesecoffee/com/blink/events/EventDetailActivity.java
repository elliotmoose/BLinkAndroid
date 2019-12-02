package javanesecoffee.com.blink.events;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import javanesecoffee.com.blink.R;
import javanesecoffee.com.blink.constants.IntentExtras;
import javanesecoffee.com.blink.entities.Event;
import javanesecoffee.com.blink.entities.User;
import javanesecoffee.com.blink.managers.EventManager;
import javanesecoffee.com.blink.managers.UserManager;

public class EventDetailActivity extends AppCompatActivity  {
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


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_detail_page);

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
                Log.d("EVENT DETAILS ACTIVITY", "onClick: Opening Dialogue");
                EventRegisterDialog dialog = new EventRegisterDialog();
                dialog.show(getSupportFragmentManager(),"EventRegisterDialogue");
            }
        });


        View decorview = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorview.setSystemUiVisibility(uiOptions);
        updateData();
        initRecyclerView();

    }

    public void updateData(){
        if(currentEvent!=null){
            Log.d("EVENT DETAILS ACTIVITY", "updateData: EVENT DETAILS ACTIITY");
            eventName.setText(currentEvent.getName());
            eventDate.setText(currentEvent.getDate());
            eventTime.setText(currentEvent.getTime());
            eventLocation.setText(currentEvent.getAddress());
            eventPrice.setText(currentEvent.getPrice());
            eventDescription.setText(currentEvent.getDescription());

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

        eventAlsoAttending.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        EventDetailActivity.HorizontalSpaceItemDecoration spaceDecoration = new EventDetailActivity.HorizontalSpaceItemDecoration(40);
        eventAlsoAttending.addItemDecoration(spaceDecoration);

        eventAlsoAttending.setAdapter(DetailImage_adapter);
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
