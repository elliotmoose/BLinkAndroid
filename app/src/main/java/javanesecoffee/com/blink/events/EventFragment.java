package javanesecoffee.com.blink.events;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;
import javanesecoffee.com.blink.R;
import javanesecoffee.com.blink.entities.Event;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventFragment extends Fragment {
    public EventFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_event, container, false);
    }


}
