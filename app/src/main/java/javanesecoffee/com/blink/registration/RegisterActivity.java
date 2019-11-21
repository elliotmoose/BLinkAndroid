package javanesecoffee.com.blink.registration;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.regex.Pattern;

import javanesecoffee.com.blink.R;
import javanesecoffee.com.blink.api.BLinkApiException;
import javanesecoffee.com.blink.api.BLinkEventObserver;
import javanesecoffee.com.blink.constants.ApiCodes;
import javanesecoffee.com.blink.constants.BuildModes;
import javanesecoffee.com.blink.constants.Config;
import javanesecoffee.com.blink.events.EventDescriptionActivity;
import javanesecoffee.com.blink.helpers.ResponseParser;
import javanesecoffee.com.blink.managers.UserManager;

public class RegisterActivity extends AppCompatActivity implements BLinkEventObserver {

    Button register_button;
    String displayname;
    String username;
    String password;
    String email;

    Pattern valid_username_pattern = Pattern.compile("[A-Za-z0-9_]+");
    // We also use this ^ for password
    Pattern valid_display_name_pattern = Pattern.compile("[A-Za-z ]+");
    Pattern valid_email_pattern = Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\."+
                                                    "[a-zA-Z0-9_+&*-]+)*@" +
                                                    "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                                                    "A-Z]{2,7}$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        register_button = findViewById(R.id.REGISTER_BUTTON);

        EditText usernameField = findViewById(R.id.fieldUsername);
        EditText passwordField = findViewById(R.id.fieldPassword);
        EditText displaynameField = findViewById(R.id.fieldDisplayname);
        EditText emailField = findViewById(R.id.positionField);

        if(Config.buildMode == BuildModes.TEST_REGISTRATION)
        {
            usernameField.setText("mooselliot");
            passwordField.setText("12345");
            displaynameField.setText("elliot koh");
            emailField.setText("kyzelliot@gmail.com");
        }

        UserManager.getInstance().registerObserver(this);

        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Config.buildMode == BuildModes.BYPASS_ONBOARDING) {
                    Intent intent = new Intent(getApplicationContext(), EventDescriptionActivity.class);
                    startActivity(intent);
                }
                else {

                    EditText usernameField = findViewById(R.id.fieldUsername);
                    EditText passwordField = findViewById(R.id.fieldPassword);
                    EditText displaynameField = findViewById(R.id.fieldDisplayname);
                    EditText emailField = findViewById(R.id.positionField);


                    username = usernameField.getText().toString();
                    password = passwordField.getText().toString();
                    displayname = displaynameField.getText().toString();
                    email = emailField.getText().toString();

                    if(validifyInputs(username, password, displayname, email)){
                        try {
                            UserManager.Register(username, password, displayname, email);
                        } catch (BLinkApiException e) {
                            Log.d("RegisterError", e.toString());
                            Toast.makeText(getApplicationContext(), "There was an error communicating to the server", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        });
    }

    public boolean validifyInputs(String username, String password, String display_name, String email){
        if ((username == null) || !valid_username_pattern.matcher(username).matches()){
            Toast.makeText(RegisterActivity.this, "Please enter a valid username", Toast.LENGTH_LONG).show();
            return false;
        }
        if ((password == null) || !valid_username_pattern.matcher(password).matches()){
            Toast.makeText(RegisterActivity.this, "Please enter a valid password", Toast.LENGTH_LONG).show();
            return false;
        }
        if ((display_name == null) || !valid_display_name_pattern.matcher(display_name).matches()){
            Toast.makeText(RegisterActivity.this, "Please enter a valid display name", Toast.LENGTH_LONG).show();
            return false;
        }
        if ((email == null) || !valid_email_pattern.matcher(email).matches()){
            Toast.makeText(RegisterActivity.this, "Please enter a valid email", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public void NextActivity()
    {
        Intent intent = new Intent(getApplicationContext(), FaceScanActivity.class);
        startActivity(intent);        
    }

    @Override //the exceptions thrown will be caught in the caller, AsyncResponseHandler. It will call onBlinkEventException
    public void onBLinkEventTriggered(JSONObject response, String taskId) throws BLinkApiException{
        if(taskId == ApiCodes.TASK_REGISTER)
        {
            boolean success = ResponseParser.ResponseIsSuccess(response);

            if(success)
            {
                NextActivity();
            }
            else
            {
                throw ResponseParser.ExceptionFromResponse(response);
            }
        }
    }

    @Override
    public void onBLinkEventException(BLinkApiException exception, String taskId) {
        new AlertDialog.Builder(RegisterActivity.this).setTitle(exception.statusText).setMessage(exception.message).setPositiveButton("Ok", null).show();
        if(Config.buildMode == BuildModes.OFFLINE) {
            NextActivity();
        }
    }

    @Override
    protected void onDestroy() {
        UserManager.getInstance().deregisterObserver(this);
        super.onDestroy();
    }
}
