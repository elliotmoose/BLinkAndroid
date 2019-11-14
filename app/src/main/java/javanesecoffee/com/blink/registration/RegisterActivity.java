package javanesecoffee.com.blink.registration;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import javanesecoffee.com.blink.R;
import javanesecoffee.com.blink.registration.FaceScanActivity;

public class RegisterActivity extends AppCompatActivity {

    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        button = findViewById(R.id.REGISTER_BUTTON);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NextActivity();
            }
        });
    }

    public void NextActivity()
    {

        Intent intent = new Intent(getApplicationContext(), FaceScanActivity.class);
        startActivity(intent);
    }
}
