package javanesecoffee.com.blink.registration;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
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
        View decorview = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorview.setSystemUiVisibility(uiOptions);
    }

    public void goHome(){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }
}
