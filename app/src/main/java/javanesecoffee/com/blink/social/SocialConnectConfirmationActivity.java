package javanesecoffee.com.blink.social;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import java.util.ArrayList;

import javanesecoffee.com.blink.R;
import javanesecoffee.com.blink.constants.IntentExtras;
import javanesecoffee.com.blink.entities.Connection;
import javanesecoffee.com.blink.managers.ConnectionsManager;

public class SocialConnectConfirmationActivity extends AppCompatActivity {

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
        SocialUndoConnectionsRecyclerViewAdapter adapter = new SocialUndoConnectionsRecyclerViewAdapter(connections, this);
        newConnectionsRecyclerView.setAdapter(adapter);
    }
}
