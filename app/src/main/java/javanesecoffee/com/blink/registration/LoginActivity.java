package javanesecoffee.com.blink.registration;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
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
import javanesecoffee.com.blink.constants.IntentExtras;
import javanesecoffee.com.blink.events.MainActivity;
import javanesecoffee.com.blink.helpers.ResponseParser;
import javanesecoffee.com.blink.managers.ConnectionsManager;
import javanesecoffee.com.blink.managers.UserManager;
import javanesecoffee.com.blink.social.UserDetailsActivity;

public class LoginActivity extends BlinkActivity implements BLinkEventObserver {

    Button login_button;
    Button register_button;
    String username;
    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login_button = findViewById(R.id.confirmLoginButon);
        register_button = findViewById(R.id.goRegisterButton);

        EditText usernameField = findViewById(R.id.loginUsername);
        EditText passwordField = findViewById(R.id.loginPassword);

        if(Config.buildMode == BuildModes.TEST_REGISTRATION || Config.buildMode == BuildModes.TEST_CONNECT)
        {
            usernameField.setText("mooselliot");
            passwordField.setText("12345");
        }

        UserManager.getInstance().registerObserver(this);

        login_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (Config.buildMode == BuildModes.BYPASS_ONBOARDING) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }
                else if (Config.buildMode == BuildModes.TEST_SOCIAL_DETAIL) {
                    ConnectionsManager.getInstance();
                    Intent intent = new Intent(getApplicationContext(), UserDetailsActivity.class);
                    intent.putExtra(IntentExtras.USER.USER_TYPE_KEY, IntentExtras.USER.USER_TYPE_CONNECTION);
                    intent.putExtra(IntentExtras.USER.USER_NAME_KEY, "mooselliot");
                    startActivity(intent);
                }
                else {
                    EditText usernameField = findViewById(R.id.loginUsername);
                    EditText passwordField = findViewById(R.id.loginPassword);

                    username = usernameField.getText().toString();
                    password = passwordField.getText().toString();


                    if(validifyInputs(username, password)){
                        ShowProgressDialog("Logging in...");
                        UserManager.Login(username, password);
                    }
                }
            }
        });

        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MoveToRegisterActivity();
            }
        });
    }


    public boolean validifyInputs(String username, String password){

        if ((username == null) || username.equals("")){
            Toast.makeText(LoginActivity.this, "Please enter a username", Toast.LENGTH_LONG).show();
            return false;
        }
        if ((password == null) || password.equals("")){
            Toast.makeText(LoginActivity.this, "Please enter a password", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public void NextActivity()
    {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void MoveToRegisterActivity(){
        HideProgressDialog();
        Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
        startActivity(intent);
        finish();
    }

    @Override //the exceptions thrown will be caught in the caller, AsyncResponseHandler. It will call onBlinkEventException
    public void onBLinkEventTriggered(JSONObject response, String taskId) throws BLinkApiException{
        if(taskId == ApiCodes.TASK_LOGIN)
        {
            HideProgressDialog();
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
        Log.d("LOGIN_ACTIVITY", exception.toString());
        if(taskId == ApiCodes.TASK_LOGIN) {
            HideProgressDialog();
            new AlertDialog.Builder(LoginActivity.this).setTitle(exception.statusText).setMessage(exception.message).setPositiveButton("Ok", null).show();
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
