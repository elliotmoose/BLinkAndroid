package javanesecoffee.com.blink.registration;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import javanesecoffee.com.blink.BlinkActivity;
import javanesecoffee.com.blink.R;
import javanesecoffee.com.blink.api.BLinkApiException;
import javanesecoffee.com.blink.api.BLinkEventObserver;
import javanesecoffee.com.blink.constants.ApiCodes;
import javanesecoffee.com.blink.constants.BuildModes;
import javanesecoffee.com.blink.constants.Config;
import javanesecoffee.com.blink.entities.User;
import javanesecoffee.com.blink.helpers.ImageHelper;
import javanesecoffee.com.blink.helpers.ResponseParser;
import javanesecoffee.com.blink.managers.UserManager;

public class FaceScanActivity extends BlinkActivity implements BLinkEventObserver {
    private static final int pic_id = 123;
    static final int REQUEST_FACE_SCAN_CAPTURE = 1;

    Button cameraButton;
    ImageView click_image_id;
    private File imageFile;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.face_scan_activity);

        UserManager.getInstance().registerObserver(this);

        //reset image
        if (imageFile != null) {
            imageFile.delete();
        }

        cameraButton = (Button)findViewById(R.id.camera_button);

        cameraButton.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                try {
                    //create file to store image in
                    String pictureFileName = "RegisterFaceScan" + Calendar.getInstance().getTimeInMillis();
                    File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                    imageFile = File.createTempFile(pictureFileName, ".jpg", storageDir);

                    //if there is an activity to push camera from
                    if (camera_intent.resolveActivity(getPackageManager()) != null) {
                        //retrieve uri
                        String provider = getPackageName();
                        Uri photoUri = FileProvider.getUriForFile(FaceScanActivity.this, provider, imageFile);
                        //put uri as target file for picture
                        camera_intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                        //push camera
                        startActivityForResult(camera_intent, REQUEST_FACE_SCAN_CAPTURE);
                    }
                } catch (IOException e) {
                    imageFile.delete();

                    Log.d("ImageError", e.toString());
                    Toast.makeText(getApplicationContext(), "Please try again as the image could not be saved", Toast.LENGTH_LONG).show();
                    return;
                }

            }
        });
        View decorview = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorview.setSystemUiVisibility(uiOptions);
    }

    @Override
    protected void onDestroy() {
        UserManager.getInstance().deregisterObserver(this);
        super.onDestroy();
    }

    public void NextActivity(){
        Intent intent = new Intent(getApplicationContext(), MoreInfoActivity.class);
        startActivity(intent);
        finish();
    }

    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data) {

        if (requestCode == REQUEST_FACE_SCAN_CAPTURE) {
            //check has image
            if (imageFile == null) {
                Toast.makeText(getApplicationContext(), "The file has not been stored, hence the face could not be registered", Toast.LENGTH_LONG).show();
                return;
            }

            //check has user
            User user = UserManager.getLoggedInUser();
            if(user == null) {
                Toast.makeText(getApplicationContext(), "There is no user logged in at this moment", Toast.LENGTH_LONG).show();
                return;
            }

            //check has username
            String username = user.getUsername();
            if(username == null) {
                Toast.makeText(getApplicationContext(), "There is no username attached to this user", Toast.LENGTH_LONG).show();
                return;
            }

            //register face
            showProgressDialog("Preparing your profile...");
            UserManager.registerFace(ImageHelper.rotateFileIfNeeded(imageFile), username);
        }
    }

    @Override
    public void onBLinkEventTriggered(JSONObject response, String taskId) throws BLinkApiException{
        if(taskId == ApiCodes.TASK_REGISTER_FACE)
        {
            hideProgressDialog();
            boolean success = ResponseParser.responseIsSuccess(response);
            if(success)
            {
                NextActivity();
            }
        }
    }

    @Override
    public void onBLinkEventException(BLinkApiException exception, String taskId) {
        if(taskId == ApiCodes.TASK_REGISTER_FACE) {
            hideProgressDialog();
            new AlertDialog.Builder(FaceScanActivity.this).setTitle(exception.statusText).setMessage(exception.message).setPositiveButton("Ok", null).show();

            if(Config.buildMode == BuildModes.OFFLINE) {
                NextActivity();
            }
        }

    }
}
