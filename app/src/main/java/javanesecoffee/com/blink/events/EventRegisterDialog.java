package javanesecoffee.com.blink.events;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import javanesecoffee.com.blink.R;
import javanesecoffee.com.blink.api.BLinkApiException;
import javanesecoffee.com.blink.api.BLinkEventObserver;
import javanesecoffee.com.blink.constants.ApiCodes;
import javanesecoffee.com.blink.helpers.ResponseParser;

public class EventRegisterDialog extends DialogFragment {
    private static final String TAG = "MyCustomDialog";


    public interface OnInputListener{
        void sendInput(String input);
    }


    //widgets
    private TextView ActionConfirm, ActionCancel;


    //vars
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.event_register_dialog, container, false);
        ActionCancel = view.findViewById(R.id.event_register_action_cancel);
        ActionConfirm = view.findViewById(R.id.event_register_action_confirm);


        ActionCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: closing dialog");
                getDialog().dismiss();
            }
        });

        ActionConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: capturing input");
                //TO-DO need to implement register evnet task

                getDialog().dismiss();
            }

        });

        return view;
    }


}


