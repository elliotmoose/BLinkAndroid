package javanesecoffee.com.blink.social;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
//import android.os.UserManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import de.hdodenhof.circleimageview.CircleImageView;
import javanesecoffee.com.blink.R;
import javanesecoffee.com.blink.api.BLinkApiException;
import javanesecoffee.com.blink.api.ImageLoadObserver;
import javanesecoffee.com.blink.constants.IntentExtras;
import javanesecoffee.com.blink.entities.User;
import javanesecoffee.com.blink.managers.ConnectionsManager;
import javanesecoffee.com.blink.managers.UserManager;

public class UserDetailsActivity extends AppCompatActivity implements ImageLoadObserver {
    User currentUser;
<<<<<<< HEAD
    UserManager currentUserManager;
    String username = "username";
    String designation = "designation";
    String company = "company";
    public final String TAG = "Logcat";
=======
>>>>>>> master
    TextView editUsername;
    TextView editBio;
    TextView editDesignation;
    TextView editCompany;
    CircleImageView editProfilePic;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_details_page);
        currentUserManager = currentUserManager.getInstance();
        editProfilePic = findViewById(R.id.profile_pic);
        editUsername = findViewById(R.id.fieldUsername);
        editBio = findViewById(R.id.bio);
        editDesignation = findViewById(R.id.designation);
        editCompany = findViewById(R.id.company);

<<<<<<< HEAD
        currentUser = currentUserManager.getLoggedInUser();
        editUsername.setText(currentUser.getUsername());
        editBio.setText(currentUser.getBio());
        //editDesignation.setText(currentUser.);
        editCompany.setText(currentUser.getCompany());
        editProfilePic.setImageBitmap(currentUser.getProfilepicture());
=======
        currentUser = UserManager.getLoggedInUser();
>>>>>>> master

        Intent intent = getIntent();
        String type = intent.getStringExtra(IntentExtras.USER.USER_TYPE_KEY);
        String username = intent.getStringExtra(IntentExtras.USER.USER_NAME_KEY);

        switch (type) {
            case IntentExtras.USER.USER_TYPE_CONNECTION:
                currentUser = ConnectionsManager.getInstance().getUserFromConnections(username);
                break;
            case IntentExtras.USER.USER_TYPE_EXPLORE:
                currentUser = ConnectionsManager.getInstance().getUserFromExploreConnections(username);
                break;
            case IntentExtras.USER.USER_TYPE_SELF:
                currentUser = UserManager.getLoggedInUser();
                break;
        }

        UpdateData();
    }

    public void UpdateData() {
        if(currentUser != null) {
            Log.d("USER_DETAILS_ACTIVITY", currentUser.getUsername());
            editUsername.setText(currentUser.getUsername());
            editBio.setText(currentUser.getBio());
            editDesignation.setText(currentUser.getPosition());
            editCompany.setText(currentUser.getCompany());
            Bitmap image = currentUser.getProfilepictureAndLoadIfNeeded(this);
            if(image != null) {
                editProfilePic.setImageBitmap(image);
            }
        }
        else {
            Toast.makeText(UserDetailsActivity.this, "Could not load this user.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onImageLoad(Bitmap bitmap) {
        UpdateData();
    }

    @Override
    public void onImageLoadFailed(BLinkApiException exception) {
        UpdateData();
    }
}