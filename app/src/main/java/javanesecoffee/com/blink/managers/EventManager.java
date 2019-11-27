package javanesecoffee.com.blink.managers;

import android.renderscript.AllocationAdapter;
import android.util.JsonReader;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

import javanesecoffee.com.blink.api.BLinkApiException;
import javanesecoffee.com.blink.api.LoadEventListTask;
import javanesecoffee.com.blink.api.LoadParticipantListTask;
import javanesecoffee.com.blink.constants.ApiCodes;
import javanesecoffee.com.blink.entities.Event;
import javanesecoffee.com.blink.entities.User;
import javanesecoffee.com.blink.events.EventListTypes;
import javanesecoffee.com.blink.helpers.ResponseParser;

public class EventManager extends Manager {
    public static EventManager singleton = new EventManager();
    public static EventManager getInstance() {
        return singleton;
    }

    public static ArrayList<User> participant_list = new ArrayList<>();
    public static EventListTypes current_request_event_list_type;

    ArrayList<Event> pastEvents = new ArrayList<>();
    ArrayList<Event> upcomingEvents = new ArrayList<>();
    ArrayList<Event> exploreEvents = new ArrayList<>();

    EventManager() {
        super();
        upcomingEvents.add(new Event("Industry Night 2019", "SUTD", "We are having industry night whoop!", "8 Somapah Road", "08/10/19", "9:00pm","FREE", "SOME_EVENT_ID"));
        upcomingEvents.add(new Event("Hackathon 2019", "SUTD", "We are having industry night whoop!", "8 Somapah Road", "08/10/19", "9:00pm","FREE", "SOME_EVENT_ID"));
        exploreEvents.add(new Event("Recruitment Talk", "MasterCard", "A talk!", "8 Somapah Road", "12/12/19", "6:00pm", "FREE", "event2" ));
        exploreEvents.add(new Event("Information Session", "Google", "A talk!", "8 Somapah Road", "18/12/19", "3:00pm", "FREE", "event2" ));
        exploreEvents.add(new Event("Interview Workshop", "Facebook", "A talk!", "8 Somapah Road", "25/12/19", "1:00pm", "FREE", "event2" ));
    }

    /**
     * Method to be called from activity
     * @param event_id
     */
    public void getParticipantList(String event_id){
        LoadParticipantListTask load_participant_list = new LoadParticipantListTask(getInstance());
        load_participant_list.execute(event_id);
    }

    /**
     * Method to be called from activity
     */
    public void LoadEventsList(){
        if(UserManager.getLoggedInUser() != null) {
            String username = UserManager.getLoggedInUser().getUsername();
            LoadEventListTask loadEventTask = new LoadEventListTask(getInstance());
            loadEventTask.execute(username);
        }
    }

    @Override //UserManager on async task complete, call super to notify observers
    public void onAsyncTaskComplete(JSONObject response, String taskId) {

        switch (taskId)
        {
            case ApiCodes.TASK_LOAD_PARTICIPANT_LIST:

                try {
                    boolean success = ResponseParser.ResponseIsSuccess(response);
                    if(success)
                    {
                        JSONObject data = ResponseParser.DataFromResponse(response);
                        EventManager.participant_list = UserListFromData(data);
                    }
                } catch (BLinkApiException e) {
                    e.printStackTrace();
                }
                break;

            case ApiCodes.TASK_LOAD_EVENTS_LIST:
                try {
                    boolean success = ResponseParser.ResponseIsSuccess(response);
                    if(success)
                    {
                        JSONObject data = ResponseParser.DataFromResponse(response);
                        UpdateEventListsWithData(data);
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

    public ArrayList<User> UserListFromData(JSONObject data){
        ArrayList<User> user_list = new ArrayList<>();
        try {
            //TODO: change user_list into the correct json field name
            JSONArray json_user_list = data.getJSONArray("user_list");
            for(int i = 0; i < json_user_list.length(); i++){
                user_list.add(new User(json_user_list.getJSONObject(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (BLinkApiException e){
            e.printStackTrace();
        }
        return user_list;
    }


    public ArrayList<Event> EventListFromData(JSONObject data, String key) throws BLinkApiException{

        ArrayList<Event> output = new ArrayList<>();
        try {
            JSONArray explore_event_list = data.getJSONArray(key);

            for(int i=0; i < explore_event_list.length(); i++){
                output.add(new Event(explore_event_list.getJSONObject(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return output;
    }

    public void UpdateEventListsWithData(JSONObject data) {
        try {
            exploreEvents = EventListFromData(data, "explore");
            upcomingEvents = EventListFromData(data, "upcoming");
            pastEvents = EventListFromData(data, "past");
        } catch (BLinkApiException e) {
            e.printStackTrace();
        }
    }


    public ArrayList<Event> eventsForType(EventListTypes type) {
        switch(type) {
            case EXPLORE:
                return exploreEvents;
            case UPCOMING:
                return upcomingEvents;
            case PAST_EVENTS:
                return pastEvents;
            default:
                return new ArrayList<>();
        }
    }
}
