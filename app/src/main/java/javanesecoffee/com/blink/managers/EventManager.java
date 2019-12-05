package javanesecoffee.com.blink.managers;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import javanesecoffee.com.blink.api.BLinkApiException;
import javanesecoffee.com.blink.api.LoadEventListForUsernameTask;
import javanesecoffee.com.blink.api.LoadEventListTask;
import javanesecoffee.com.blink.api.RegisterForEventTask;
import javanesecoffee.com.blink.api.RegisterTask;
import javanesecoffee.com.blink.constants.ApiCodes;
import javanesecoffee.com.blink.constants.Config;
import javanesecoffee.com.blink.entities.Event;
import javanesecoffee.com.blink.entities.User;
import javanesecoffee.com.blink.events.EventListTypes;
import javanesecoffee.com.blink.helpers.ResponseParser;

public class EventManager extends Manager {
    public static EventManager singleton = new EventManager();
    public static EventManager getInstance() {
        return singleton;
    }

    ArrayList<Event> pastEvents = new ArrayList<>();
    ArrayList<Event> upcomingEvents = new ArrayList<>();
    ArrayList<Event> exploreEvents = new ArrayList<>();

    EventManager() {
        super();
        if(Config.TEMPLATE_EVENTS) {
            ArrayList<String> tag = new ArrayList<>();
            tag.add("Coding");
            tag.add("CV");
//            upcomingEvents.add(new Event("Industry Night 2019", "SUTD", "We are having industry night whoop!", "8 Somapah Road", "08/10/19", "9:00pm","FREE", "SOME_EVENT_ID"));
//            upcomingEvents.add(new Event("Hackathon 2019", "SUTD", "We are having industry night whoop!", "8 Somapah Road", "08/10/19", "9:00pm","FREE", "SOME_EVENT_ID"));
//            exploreEvents.add(new Event("Recruitment Talk", "MasterCard", "A talk!", "8 Somapah Road", "12/12/19", "6:00pm", "FREE", "event2" ));
//            exploreEvents.add(new Event("Information Session", "Google", "A talk!", "8 Somapah Road", "18/12/19", "3:00pm", "FREE", "event2" ));
            exploreEvents.add(new Event("Interview Workshop", "Facebook", "A talk!", "8 Somapah Road", "25/12/19", "1:00pm", "FREE", "event2" , tag));
        }
    }

    /**
     * Method to be called from activity
     */
    public void loadEventsList(){
        if(UserManager.getLoggedInUser() != null) {
            String username = UserManager.getLoggedInUser().getUsername();
            LoadEventListTask loadEventTask = new LoadEventListTask(getInstance());
            loadEventTask.execute(username);
        }
    }

    public void loadEventsListForUsername(String username) {
        LoadEventListForUsernameTask task = new LoadEventListForUsernameTask(getInstance());
        task.execute(username);
    }

    @Override //UserManager on async task complete, call super to notify observers
    public void onAsyncTaskComplete(JSONObject response, String taskId) {

        switch (taskId)
        {
            case ApiCodes.TASK_LOAD_EVENTS_LIST:
                try {
                    boolean success = ResponseParser.responseIsSuccess(response);
                    if(success)
                    {
                        JSONObject data = ResponseParser.dataFromResponse(response);
                        updateEventListsWithData(data);
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

    public ArrayList<Event> eventListFromData(JSONObject data, String key) throws BLinkApiException{
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

    public void updateEventListsWithData(JSONObject data) {
        try {
            exploreEvents = eventListFromData(data, "explore");
            upcomingEvents = eventListFromData(data, "upcoming");
            pastEvents = eventListFromData(data, "past");
        } catch (BLinkApiException e) {
            e.printStackTrace();
        }
    }

    public static void register(String username, String event_id){
        RegisterForEventTask task = new RegisterForEventTask(getInstance()); //pass singleton in as handler
        task.execute(username, event_id); //pass in params
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
