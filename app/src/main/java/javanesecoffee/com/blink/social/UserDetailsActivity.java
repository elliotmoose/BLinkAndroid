package javanesecoffee.com.blink.social;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;
import javanesecoffee.com.blink.R;
import javanesecoffee.com.blink.api.ImageEntityObserver;
import javanesecoffee.com.blink.constants.IntentExtras;
import javanesecoffee.com.blink.entities.User;
import javanesecoffee.com.blink.managers.ImageManager;
import javanesecoffee.com.blink.managers.UserManager;

public class UserDetailsActivity extends AppCompatActivity implements ImageEntityObserver {
    User currentUser;

    TextView editUsername;
    TextView editBio;
    TextView editDesignation;
    TextView editCompany;
    CircleImageView editProfilePic;

    TextView mailTextView;
    TextView linkedinTextView;
    TextView facebookTextView;
    TextView instagramTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_details_page);

        editProfilePic = findViewById(R.id.profile_pic);
        editUsername = findViewById(R.id.loginUsername);
        editBio = findViewById(R.id.bio);
        editDesignation = findViewById(R.id.designation);
        editCompany = findViewById(R.id.company);
        mailTextView = findViewById(R.id.mailTextView);
        linkedinTextView = findViewById(R.id.linkedInTextView);
        facebookTextView = findViewById(R.id.facebookTextView);
        instagramTextView = findViewById(R.id.instagramTextView);



        currentUser = UserManager.getLoggedInUser();

        Intent intent = getIntent();
        String type = intent.getStringExtra(IntentExtras.USER.USER_TYPE_KEY);
        String username = intent.getStringExtra(IntentExtras.USER.USER_NAME_KEY);

//        if(username != null) {
//            switch (type) {
//                case IntentExtras.USER.USER_TYPE_CONNECTION:
//                    currentUser = ConnectionsManager.getInstance().getUserFromConnections(username);
//                    break;
//                case IntentExtras.USER.USER_TYPE_EXPLORE:
//                    currentUser = ConnectionsManager.getInstance().getUserFromExploreConnections(username);
//                    break;
//                case IntentExtras.USER.USER_TYPE_SELF:
//                    currentUser = UserManager.getLoggedInUser();
//                    break;
//            }
//        }
        if(type == IntentExtras.USER.USER_TYPE_SELF ){
            currentUser = UserManager.getLoggedInUser();

        }else{
            currentUser = UserManager.getUserFromCache(username);
        }
        updateData();
        View decorview = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorview.setSystemUiVisibility(uiOptions);
    }



    public void updateData() {
        if(currentUser != null) {
            Log.d("USER_DETAILS_ACTIVITY", currentUser.getUsername());
            editUsername.setText(currentUser.getDisplayname());
            editBio.setText(currentUser.getBio());
            editDesignation.setText(currentUser.getPosition());
            editCompany.setText(currentUser.getCompany());

            mailTextView.setText(currentUser.getEmail());
            linkedinTextView.setText(currentUser.getLinkedin());
            facebookTextView.setText(currentUser.getFacebook());
            instagramTextView.setText(currentUser.getInstagram());

            Bitmap image = ImageManager.getImageOrLoadIfNeeded(currentUser.getUsername(), this, ImageManager.ImageType.PROFILE_IMAGE);
            if(image != null) {
                editProfilePic.setImageBitmap(image);
            }
            else {
                //resets when view is being reused
                editProfilePic.setImageBitmap(ImageManager.dpPlaceholder);
            }
        }
        else {
            Toast.makeText(UserDetailsActivity.this, "Could not load this user.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onImageUpdated(Bitmap bitmap) {
        updateData();
    }
}