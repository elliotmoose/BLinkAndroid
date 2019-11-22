package javanesecoffee.com.blink.registration;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import javanesecoffee.com.blink.BlinkActivity;
import javanesecoffee.com.blink.R;
import javanesecoffee.com.blink.api.BLinkApiException;
import javanesecoffee.com.blink.api.BLinkEventObserver;
import javanesecoffee.com.blink.constants.ApiCodes;
import javanesecoffee.com.blink.constants.BuildModes;
import javanesecoffee.com.blink.constants.Config;
import javanesecoffee.com.blink.helpers.ResponseParser;
import javanesecoffee.com.blink.managers.UserManager;


import static javanesecoffee.com.blink.R.layout.more_info;

public class MoreInfoActivity extends BlinkActivity implements BLinkEventObserver {
    Button next;
    Button skip;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(more_info);
        next = findViewById(R.id.next);

        if(Config.buildMode == BuildModes.TEST_REGISTRATION) {
            EditText bioField = findViewById(R.id.bioField);
            EditText positionField = findViewById(R.id.positionField);
            EditText companyField = findViewById(R.id.companyField);
            EditText linkedinField = findViewById(R.id.linkedinField);
            EditText facebookField = findViewById(R.id.facebookField);
            EditText instagramField = findViewById(R.id.instagramField);

            bioField.setText("I'm a moose");
            positionField.setText("Full Stack Developer");
            companyField.setText("SUTD");
            linkedinField.setText("linkedin.com/in/mooselliot");
            instagramField.setText("mooselliot");
        }


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText bioField = findViewById(R.id.bioField);
                EditText positionField = findViewById(R.id.positionField);
                EditText companyField = findViewById(R.id.companyField);
                EditText linkedinField = findViewById(R.id.linkedinField);
                EditText facebookField = findViewById(R.id.facebookField);
                EditText instagramField = findViewById(R.id.instagramField);

                String bio = bioField.getText().toString();
                String position = positionField.getText().toString();
                String company = companyField.getText().toString();
                String linkedin = linkedinField.getText().toString();
                String facebook = facebookField.getText().toString();
                String instagram = instagramField.getText().toString();


                try {
                    ShowProgressDialog("Updating info...");
                    UserManager.RegisterMoreInfo(bio, position, company, linkedin, facebook, instagram);
                } catch (BLinkApiException e) {
                    e.printStackTrace();
                    Toast.makeText(MoreInfoActivity.this, e.message, Toast.LENGTH_LONG).show();
                }
            }
        });

        skip = findViewById(R.id.skip);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NextActivity();
            }
        });

        UserManager.getInstance().registerObserver(this);
    }


    public void NextActivity(){
        Intent intent = new Intent(getApplicationContext(), CompleteRegistrationActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBLinkEventTriggered(JSONObject response, String taskId) throws BLinkApiException {
        if(taskId == ApiCodes.TASK_MORE_INFO){
            HideProgressDialog();
            boolean success = ResponseParser.ResponseIsSuccess(response);

            if(success){
                NextActivity();
            }
            else{
                throw ResponseParser.ExceptionFromResponse(response);
            }
        }
    }

    @Override
    public void onBLinkEventException(BLinkApiException exception, String taskId){
        if(taskId == ApiCodes.TASK_MORE_INFO){
            HideProgressDialog();
            new AlertDialog.Builder(MoreInfoActivity.this).setTitle(exception.statusText).setMessage(exception.message).setPositiveButton("Ok", null).show();
            if(Config.buildMode == BuildModes.OFFLINE) {
                NextActivity();
            }
        }
    }


    @Override
    protected void onDestroy() {
        UserManager.getInstance().deregisterObserver(this);
        super.onDestroy();
    }
}
