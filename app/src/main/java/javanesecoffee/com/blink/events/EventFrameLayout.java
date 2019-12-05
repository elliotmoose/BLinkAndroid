package javanesecoffee.com.blink.events;


import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import javanesecoffee.com.blink.R;
import javanesecoffee.com.blink.api.ImageEntityObserver;
import javanesecoffee.com.blink.entities.Event;
import javanesecoffee.com.blink.managers.ImageManager;

public class EventFrameLayout extends FrameLayout implements ImageEntityObserver {

    private Event event;
    private EventTagRecyclerViewAdapter EventTagRecyclerViewAdapter;


    public EventFrameLayout(@NonNull Context context) {
        super(context);
        this.setClipChildren(false);
        this.setClipToPadding(false);
    }

    public EventFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.setClipChildren(false);
        this.setClipToPadding(false);
    }

    public EventFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setClipChildren(false);
        this.setClipToPadding(false);
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public void updateData() {
        if(this.event != null) {
            TextView eventName = this.findViewById(R.id.eventNameTextView);
            TextView eventDateTextView = this.findViewById(R.id.eventDateTextView);
            TextView eventOrganiserTextView = this.findViewById(R.id.eventOrganiserTextView);
            TextView eventTimeTextView = this.findViewById(R.id.eventTimeTextView);

            eventName.setText(event.getName());
            eventDateTextView.setText(event.getDate());
            eventTimeTextView.setText(event.getTime());
            eventOrganiserTextView.setText(event.getOrganiser());
            ImageView imageView = this.findViewById(R.id.EventImage);

            Bitmap image = ImageManager.getImageOrLoadIfNeeded(this.event.getEvent_id(), this, ImageManager.ImageType.EVENT_IMAGE);

            if(image != null) {
                Log.d("IMAGE_MANAGER", "Image updated" + image.getByteCount());
                imageView.setImageBitmap(image);
            }
            else {
                //resets when view is being reused
                imageView.setImageBitmap(ImageManager.eventPlaceholder);
            }
//            initRecyclerView();
        }
    }
//    private void initRecyclerView(){
//        RecyclerView EventTagRecyclerView = findViewById(R.id.EventTagRecyclerView);
//        ArrayList<String> tags = event.getTags();
//        EventTagRecyclerViewAdapter = new EventTagRecyclerViewAdapter(tags, getContext());
//        EventTagRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL, false));
//        EventTagRecyclerView.setAdapter(EventTagRecyclerViewAdapter);
//    }

    @Override
    public void onImageUpdated(Bitmap bitmap) {
        this.updateData();
    }
}
