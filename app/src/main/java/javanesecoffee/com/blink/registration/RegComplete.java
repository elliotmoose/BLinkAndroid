package javanesecoffee.com.blink.registration;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import javanesecoffee.com.blink.EventDescriptionActivity;
import javanesecoffee.com.blink.R;

public class RegComplete extends AppCompatActivity {
    Button letsgo;


    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.complete_reg);
        letsgo = findViewById(R.id.letsgo);
        letsgo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goHome();
            }
        });
    }

    public void goHome(){
        Intent intent = new Intent(getApplicationContext(), EventDescriptionActivity.class);
        startActivity(intent);
    }
}
