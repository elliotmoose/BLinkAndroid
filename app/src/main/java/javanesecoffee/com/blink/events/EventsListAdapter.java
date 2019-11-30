package javanesecoffee.com.blink.events;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

import javanesecoffee.com.blink.entities.Event;

//TODO: this should be of type Event
public class EventsListAdapter extends ArrayAdapter<Event> {

    private int resourceLayout;
    private Context mContext;

    public EventsListAdapter(Context context, int resource, List<Event> items) {
        super(context, resource, items);
        this.resourceLayout = resource;
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(mContext);
            v = vi.inflate(resourceLayout, null);
        }

        Event event = getItem(position);

        EventFrameLayout eventFrameLayout = (EventFrameLayout) v;
        eventFrameLayout.setEvent(event);
        eventFrameLayout.updateData();


        return v;
    }

}