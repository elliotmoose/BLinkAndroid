package javanesecoffee.com.blink;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
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
import java.io.IOException;
import java.util.Calendar;

import javanesecoffee.com.blink.api.BLinkApiException;
import javanesecoffee.com.blink.api.BLinkEventObserver;
import javanesecoffee.com.blink.constants.ApiCodes;
import javanesecoffee.com.blink.entities.User;
import javanesecoffee.com.blink.helpers.ImageHelper;
import javanesecoffee.com.blink.helpers.ResponseParser;
import javanesecoffee.com.blink.managers.UserManager;
import javanesecoffee.com.blink.registration.FaceScanActivity;

public class CameraFragment extends Fragment implements BLinkEventObserver {
    private File imageFile;
    static final int REQUEST_SELFIE_CONNECT_CAPTURE = 2;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        InitProgressDialog();

        UserManager.getInstance().registerObserver(this);

        return inflater.inflate(R.layout.fragment_camera, container, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        UserManager.getInstance().deregisterObserver(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        Button selfieButton = getView().findViewById(R.id.selfieButton);
        selfieButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
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
            ShowProgressDialog("Making Connections...");
            UserManager.ConnectUsers(ImageHelper.RotateFileIfNeeded(imageFile), username);
        }
    }

    private ProgressDialog dialog;

    public void InitProgressDialog()
    {
        if(dialog == null) {
            dialog = new ProgressDialog(getActivity());
            dialog.setMessage("Registering...");
        }
    }

    public void ShowProgressDialog()
    {
        InitProgressDialog();
        dialog.show();
    }

    public void ShowProgressDialog(String message)
    {
        InitProgressDialog();
        dialog.setMessage(message);
        dialog.show();
    }

    public void HideProgressDialog() {
        InitProgressDialog();
        if(dialog.isShowing()) {
            dialog.hide();
        }
    }

    @Override
    public void onBLinkEventTriggered(JSONObject response, String taskId) throws BLinkApiException {
        if(taskId == ApiCodes.TASK_CONNECT_USERS) {
            HideProgressDialog();

            boolean success = ResponseParser.ResponseIsSuccess(response);
            if(success)
            {
//                NextActivity();
            }
        }
    }

    @Override
    public void onBLinkEventException(BLinkApiException exception, String taskId) {
        if(taskId == ApiCodes.TASK_CONNECT_USERS) {
            HideProgressDialog();
            new AlertDialog.Builder(getActivity()).setTitle(exception.statusText).setMessage(exception.message).setPositiveButton("Ok", null).show();
        }
    }
}
