package javanesecoffee.com.blink.events;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import javanesecoffee.com.blink.R;
import javanesecoffee.com.blink.api.ImageEntityObserver;
import javanesecoffee.com.blink.constants.IntentExtras;
import javanesecoffee.com.blink.entities.Event;
import javanesecoffee.com.blink.entities.User;
import javanesecoffee.com.blink.managers.ImageManager;
import javanesecoffee.com.blink.social.UserDetailsActivity;

public class EventCardRecyclerViewAdapter extends RecyclerView.Adapter<EventCardRecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "eventCard_Recycler";
    private Context mContext;
    ArrayList<Event> events = new ArrayList<>();

    public EventCardRecyclerViewAdapter(ArrayList<Event> items,Context context) {
        super();
        this.events = items;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_event, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {

        holder.event = events.get(i);
        holder.updateData();
        final ViewHolder holderfinal = holder;

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String event_id = holderfinal.event.getEvent_id();
                //TODO: check if this user is a connection
                //if he is not a connection, go to locked UserDetailsActivity
                Intent intent = new Intent(mContext, EventDetailActivity.class);
                intent.putExtra(IntentExtras.EVENT.EVENT_ID_KEY,event_id);
                intent.putExtra(IntentExtras.EVENT.EVENT_TYPE_KEY,IntentExtras.EVENT.EVENT_TYPE_EXPLORE);
//                intent.putExtra(IntentExtras.EVENT.EVENT_POSITION_KEY,);
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return events.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements ImageEntityObserver {
        Event event;

        ImageView eventImage;

        TextView eventNameText;
        TextView eventOrganiser ;
        TextView eventDate;
        TextView eventTime;

        ConstraintLayout parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            parentLayout = itemView.findViewById(R.id.fragment_event);
            eventImage = itemView.findViewById(R.id.EventImage);
            eventNameText = itemView.findViewById(R.id.eventNameTextView);
            eventOrganiser = itemView.findViewById(R.id.eventOrganiserTextView);
            eventDate = itemView.findViewById(R.id.eventDateTextView);
            eventTime = itemView.findViewById(R.id.eventTimeTextView);

        }

        public void updateData() {
            if(event == null) {
                return;
            }

            Bitmap image = ImageManager.getImageOrLoadIfNeeded(event.getEvent_id(), this, ImageManager.ImageType.EVENT_IMAGE);

            if(image != null) {
                eventImage.setImageBitmap(image);
            }
            else {
                //resets when view is being reused
                eventImage.setImageBitmap(ImageManager.eventPlaceholder);
            }

            eventNameText.setText(event.getName());
            eventOrganiser.setText(event.getOrganiser());
            eventDate.setText(event.getDate());
            eventTime.setText(event.getTime());
        }

        @Override
        public void onImageUpdated(Bitmap bitmap) {
            updateData();
        }
    }
}