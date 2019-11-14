package javanesecoffee.com.blink;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import javanesecoffee.com.blink.registration.RegComplete;

import static javanesecoffee.com.blink.R.layout.more_info;

public class MoreInfo extends AppCompatActivity {
    Button next;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(more_info);
        next = findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveForward();
            }
        });
    }
    public void moveForward(){
        Intent intent = new Intent(getApplicationContext(), RegComplete.class);
        startActivity(intent);

    }

}
