package javanesecoffee.com.blink.social;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.json.JSONObject;

import java.io.File;
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
        Button button = findViewById(R.id.connectConfirmationButton);




        final String path = getIntent().getStringExtra(IntentExtras.CONNECT.IMAGE_PATH_KEY);
        if(path != null) {
            Bitmap image = BitmapFactory.decodeFile(path);
            if(image != null) {
                imageView.setImageBitmap(image);
            }
        }

        final Activity activity = this;
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                activity.finish();
                File file = new File(path);
                file.delete();
            }
        });

        adapter = new SocialUndoConnectionsRecyclerViewAdapter(ConnectionsManager.getInstance().getJustConnected(), this);
        newConnectionsRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
//        SocialSummaryFragment.HorizontalSpaceItemDecoration spaceDecoration = new SocialSummaryFragment.HorizontalSpaceItemDecoration(40);
        newConnectionsRecyclerView.setAdapter(adapter);
        ConnectionsManager.getInstance().registerObserver(this);
    }

    @Override
    protected void onDestroy() {
        ConnectionsManager.getInstance().deregisterObserver(this);
        super.onDestroy();
    }

    private void UpdateData() {
        new AlertDialog.Builder(this).setTitle("Connections Left").setMessage(ConnectionsManager.getInstance().getJustConnected().size() +"").setPositiveButton("Ok", null).show();
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
