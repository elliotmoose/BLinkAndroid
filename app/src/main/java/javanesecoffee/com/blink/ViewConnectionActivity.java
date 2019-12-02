package javanesecoffee.com.blink;

import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import javanesecoffee.com.blink.api.ImageEntityObserver;
import javanesecoffee.com.blink.constants.IntentExtras;
import javanesecoffee.com.blink.entities.Connection;
import javanesecoffee.com.blink.entities.User;
import javanesecoffee.com.blink.managers.ConnectionsManager;
import javanesecoffee.com.blink.managers.ImageManager;

public class ViewConnectionActivity extends AppCompatActivity implements ImageEntityObserver {

    TextView timestampTextView;
    ImageView imageView;
    Connection connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_connection);

        String username = getIntent().getStringExtra(IntentExtras.USER.USER_NAME_KEY);
        User user = ConnectionsManager.getInstance().getUserFromConnections(username);
        connection = user.getConnection();

        timestampTextView = findViewById(R.id.connection_time_stamp_textview);
        imageView = findViewById(R.id.connectionImageView);

//        Log.d("CONNECTION", connection.getImage_id());
//        new AlertDialog.Builder(this).setTitle("Connection").setMessage(connection.getConnection_id()).setPositiveButton("Ok", null).show();
        updateData();
    }

    private void updateData() {
        if (connection != null) {
            Bitmap image = ImageManager.getImageOrLoadIfNeeded(connection.getImage_id(), this, ImageManager.ImageType.CONNECTION_IMAGE);

            if(image != null) {
                imageView.setImageBitmap(image);
            }
            else {
                //resets when view is being reused
                imageView.setImageBitmap(ImageManager.eventPlaceholder);
            }
        }
    }

    @Override
    public void onImageUpdated(Bitmap bitmap) {
        updateData();
    }
}
