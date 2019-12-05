package javanesecoffee.com.blink;

import android.graphics.Bitmap;
import android.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

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

        updateData();
    }

    private void updateData() {
        if (connection != null) {

            long unixSeconds = Long.parseLong(connection.getTime());
            Date date = new Date(unixSeconds); // *1000 is to convert seconds to milliseconds
            TimeZone timezone = TimeZone.getTimeZone("SGT");
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy h:mm a (EEEE)"); // the format of your date
            sdf.setTimeZone(timezone);
            String formattedDate = sdf.format(date);
            timestampTextView.setText("Connection Made On: "+ formattedDate);

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
