package javanesecoffee.com.blink.social;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import org.json.JSONObject;

import java.util.ArrayList;

import javanesecoffee.com.blink.R;
import javanesecoffee.com.blink.api.BLinkApiException;
import javanesecoffee.com.blink.api.BLinkEventObserver;
import javanesecoffee.com.blink.constants.ApiCodes;
import javanesecoffee.com.blink.constants.IntentExtras;
import javanesecoffee.com.blink.entities.Connection;
import javanesecoffee.com.blink.managers.ConnectionsManager;

public class SocialConnectConfirmationActivity extends AppCompatActivity implements BLinkEventObserver {

    private SocialUndoConnectionsRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_connect_confirmation);

        ImageView imageView = findViewById(R.id.connectImageView);
        RecyclerView newConnectionsRecyclerView = findViewById(R.id.newConnectionsRecyclerView);

        String path = getIntent().getStringExtra(IntentExtras.CONNECT.IMAGE_PATH_KEY);
        if(path != null) {
            Bitmap image = BitmapFactory.decodeFile(path);
            if(image != null) {
                imageView.setImageBitmap(image);
            }
        }

        ArrayList<Connection> connections = ConnectionsManager.getInstance().getJustConnected();
         adapter = new SocialUndoConnectionsRecyclerViewAdapter(connections, this);
        newConnectionsRecyclerView.setAdapter(adapter);

        ConnectionsManager.getInstance().registerObserver(this);
    }

    @Override
    protected void onDestroy() {
        ConnectionsManager.getInstance().deregisterObserver(this);
        super.onDestroy();
    }

    private void UpdateData() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onBLinkEventTriggered(JSONObject response, String taskId) throws BLinkApiException {
        if(taskId == ApiCodes.TASK_UNDO_CONNECT || taskId == ApiCodes.TASK_CONNECT_USERS) {
            UpdateData();
        }
    }

    @Override
    public void onBLinkEventException(BLinkApiException exception, String taskId) {
        new AlertDialog.Builder(this).setTitle(exception.statusText).setMessage(exception.message).setPositiveButton("Ok", null).show();
    }
}
