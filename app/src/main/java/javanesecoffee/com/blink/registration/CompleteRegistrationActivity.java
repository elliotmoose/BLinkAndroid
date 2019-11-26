package javanesecoffee.com.blink.registration;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import javanesecoffee.com.blink.R;
import javanesecoffee.com.blink.events.MainActivity;

public class CompleteRegistrationActivity extends AppCompatActivity {
    Button letsgo;


    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_confirmation);
        letsgo = findViewById(R.id.letsgo);
        letsgo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goHome();
            }
        });
    }

    public void goHome(){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }
}
