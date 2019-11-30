package javanesecoffee.com.blink.events;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONObject;

import java.util.ArrayList;

import javanesecoffee.com.blink.R;
import javanesecoffee.com.blink.api.BLinkApiException;
import javanesecoffee.com.blink.api.BLinkEventObserver;
import javanesecoffee.com.blink.constants.ApiCodes;
import javanesecoffee.com.blink.constants.IntentExtras;
import javanesecoffee.com.blink.entities.Event;
import javanesecoffee.com.blink.managers.ConnectionsManager;
import javanesecoffee.com.blink.managers.EventManager;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventListFragment extends Fragment implements BLinkEventObserver {

    private EventListTypes type = EventListTypes.EXPLORE;

    private ListView eventListView;
    private EventsListAdapter eventListAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ArrayList<Event> events = new ArrayList<>();

    public EventListFragment(){
        super();
    }

    public void setType(EventListTypes type) {
        this.type = type;
    }

    Event EVENT_KEY;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventManager.getInstance().registerObserver(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventManager.getInstance().deregisterObserver(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_event_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable final Bundle savedInstanceState) {
        loadLayout(view, savedInstanceState);
    }

    public void setEvents(ArrayList<Event> newEvents) {
//        this.events = newEvents;
        //cannot change the reference otherwise adapter loses ref
        this.events.clear();
        for(Event event : newEvents) {
            events.add(event);
        }

        if(eventListAdapter != null) {
            eventListAdapter.notifyDataSetChanged();
        }
    }
    public void updateEventList()
    {
        setEvents(EventManager.getInstance().eventsForType(this.type));
    }

    @Override
    public void onBLinkEventTriggered(JSONObject response, String taskId) throws BLinkApiException {
        if(taskId == ApiCodes.TASK_LOAD_EVENTS_LIST) {
            UpdateEvents();
            if(swipeRefreshLayout!=null){
                swipeRefreshLayout.setRefreshing(false);
            }
        }
    }
    private void UpdateEvents(){
        UpdateEventList();
        if(eventListAdapter!=null){
            eventListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onBLinkEventException(BLinkApiException exception, String taskId) {

    }
    public void loadLayout(@NonNull final View view, @Nullable final Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        eventListView = getView().findViewById(R.id.eventListView);
        AdapterView.OnItemClickListener temp = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //System.out.println(events.get(position));
                //Toast.makeText(getContext(), String.valueOf(events.get(position)), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent (getActivity(), EventDetailActivity.class);
                intent.putExtra(IntentExtras.EVENT.EVENT_ID_KEY,String.valueOf(events.get(position).getEvent_id()));
                intent.putExtra(IntentExtras.EVENT.EVENT_TYPE_KEY,type.toString());
                intent.putExtra(IntentExtras.EVENT.EVENT_POSITION_KEY,position);
                startActivity(intent);
            }
        };
        swipeRefreshLayout = getView().findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh(){
                loadLayout(view, savedInstanceState);
                swipeRefreshLayout.setRefreshing(false);
                EventManager.getInstance().LoadEventsList();
            }
        });

        eventListView.setOnItemClickListener(temp);
        eventListAdapter = new EventsListAdapter(getContext(), R.layout.fragment_event, events);
        eventListView.setAdapter(eventListAdapter);
        updateEventList();
    }
}
