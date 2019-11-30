package javanesecoffee.com.blink;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import javanesecoffee.com.blink.api.BLinkApiException;
import javanesecoffee.com.blink.api.BLinkEventObserver;
import javanesecoffee.com.blink.constants.ApiCodes;
import javanesecoffee.com.blink.constants.BuildModes;
import javanesecoffee.com.blink.constants.Config;
import javanesecoffee.com.blink.constants.IntentExtras;
import javanesecoffee.com.blink.entities.User;
import javanesecoffee.com.blink.helpers.ImageHelper;
import javanesecoffee.com.blink.helpers.ResponseParser;
import javanesecoffee.com.blink.managers.ConnectionsManager;
import javanesecoffee.com.blink.managers.UserManager;
import javanesecoffee.com.blink.social.SocialConnectConfirmationActivity;

public class CameraFragment extends Fragment implements BLinkEventObserver {
    private File imageFile;
    static final int REQUEST_SELFIE_CONNECT_CAPTURE = 2;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        initProgressDialog();

        UserManager.getInstance().registerObserver(this);
        ConnectionsManager.getInstance().registerObserver(this);


        return inflater.inflate(R.layout.fragment_camera, container, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        UserManager.getInstance().deregisterObserver(this);
        ConnectionsManager.getInstance().deregisterObserver(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        Button selfieButton = getView().findViewById(R.id.selfieButton);
        selfieButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                if(Config.buildMode == BuildModes.TEST_CONNECT) {
                    //register face
                    showProgressDialog("Testing Connections...");
                    Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.sidelliot);
                    FileOutputStream fos = null;

                    String pictureFileName = "TestConnect" + Calendar.getInstance().getTimeInMillis();
                    File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                    imageFile = new File(storageDir,pictureFileName);

                    try {
                        fos = new FileOutputStream(imageFile);
                        bm.compress(Bitmap.CompressFormat.JPEG,100,fos);
                        fos.close();
                    }
                    catch (IOException e) {
                        Log.e("app",e.getMessage());
                        if (fos != null) {
                            try {
                                fos.close();
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                    ConnectionsManager.getInstance().connectUsers(imageFile, "mooselliot");
                    return;
                }

                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                try {
                    //create file to store image in
                    String pictureFileName = "Selfie" + Calendar.getInstance().getTimeInMillis();
                    File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                    imageFile = File.createTempFile(pictureFileName, ".jpg", storageDir);

                    //if there is an activity to push camera from
                    if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                        //retrieve uri
                        String provider = getActivity().getPackageName();
                        Uri photoUri = FileProvider.getUriForFile(getActivity(), provider, imageFile);
                        //put uri as target file for picture
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                        //push camera
                        startActivityForResult(cameraIntent, REQUEST_SELFIE_CONNECT_CAPTURE);
                    }
                } catch (IOException e) {
                    imageFile.delete();

                    Log.d("ImageError", e.toString());
                    Toast.makeText(getActivity().getApplicationContext(), "Please try again as the image could not be saved", Toast.LENGTH_LONG).show();
                    return;
                }

            }

        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_SELFIE_CONNECT_CAPTURE) {
            //check has image
            if (imageFile == null) {
                Toast.makeText(getActivity(), "The file has not been stored, hence the face could not be registered", Toast.LENGTH_LONG).show();
                return;
            }

            //check has user
            User user = UserManager.getLoggedInUser();
            if(user == null) {
                Toast.makeText(getActivity(), "There is no user logged in at this moment", Toast.LENGTH_LONG).show();
                return;
            }

            //check has username
            String username = user.getUsername();
            if(username == null) {
                Toast.makeText(getActivity(), "There is no username attached to this user", Toast.LENGTH_LONG).show();
                return;
            }

            //register face
            showProgressDialog("Making Connections...");
            ConnectionsManager.getInstance().connectUsers(ImageHelper.rotateFileIfNeeded(imageFile), username);
        }
    }

    private ProgressDialog dialog;

    public void initProgressDialog()
    {
        if(dialog == null) {
            dialog = new ProgressDialog(getActivity());
            dialog.setMessage("Registering...");
        }
    }

    public void showProgressDialog()
    {
        initProgressDialog();
        dialog.show();
    }

    public void showProgressDialog(String message)
    {
        initProgressDialog();
        dialog.setMessage(message);
        dialog.show();
    }

    public void hideProgressDialog() {
        initProgressDialog();
        if(dialog.isShowing()) {
            dialog.hide();
        }
    }

    private void showConfirmationScreen(String image_path) {
        Intent intent = new Intent(getContext(), SocialConnectConfirmationActivity.class);
        intent.putExtra(IntentExtras.CONNECT.IMAGE_PATH_KEY, image_path);
        startActivity(intent);
    }

    @Override
    public void onBLinkEventTriggered(JSONObject response, String taskId) throws BLinkApiException {
        if(taskId == ApiCodes.TASK_CONNECT_USERS) {
            hideProgressDialog();

            boolean success = ResponseParser.responseIsSuccess(response);
            if(success)
            {
                if(imageFile != null) {
                    showConfirmationScreen(imageFile.getPath());
                }
                else {
                    //if we can't show the image, we'll default to saying it has connected
                    throw new BLinkApiException("NO_IMAGE","Success!", "You have been connected!");
                }
            }
        }
    }

    @Override
    public void onBLinkEventException(BLinkApiException exception, String taskId) {
        if(taskId == ApiCodes.TASK_CONNECT_USERS) {
            hideProgressDialog();
            new AlertDialog.Builder(getActivity()).setTitle(exception.statusText).setMessage(exception.message).setPositiveButton("Ok", null).show();
        }
    }

    public String getURLForResource (int resourceId) {
        //use BuildConfig.APPLICATION_ID instead of R.class.getPackage().getName() if both are not same
        return Uri.parse("drawable://"+R.class.getPackage().getName()+"/" +resourceId).toString();
    }
}
