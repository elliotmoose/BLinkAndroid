package javanesecoffee.com.blink.events;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import javanesecoffee.com.blink.CameraFragment;
import javanesecoffee.com.blink.HomeFragment;
import javanesecoffee.com.blink.R;
import javanesecoffee.com.blink.entities.User;
import javanesecoffee.com.blink.managers.ConnectionsManager;
import javanesecoffee.com.blink.managers.EventManager;
import javanesecoffee.com.blink.managers.ImageManager;
import javanesecoffee.com.blink.managers.UserManager;
import javanesecoffee.com.blink.social.SocialFragment;

public class MainActivity extends AppCompatActivity {
    //get the details of the event from the server and display them

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        navListener.onNavigationItemSelected(bottomNav.getMenu().findItem(R.id.navhome));


        ImageManager.initPlaceholders(this, getResources());
        LoadAllData();
    }

    private void LoadAllData() {
        User user = UserManager.getLoggedInUser();
        if(user != null) {
            EventManager.getInstance().loadEventsList();
            ConnectionsManager.getInstance().loadAllConnections();
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFrag;

            switch (item.getItemId()){
                default:
                case R.id.navhome:
                    selectedFrag = new HomeFragment();
                    break;
                case R.id.navcam:
                    selectedFrag = new CameraFragment();
                    break;
                case R.id.navevent:
                    selectedFrag = new SocialFragment();
                    break;

            }

            getSupportFragmentManager().beginTransaction().replace(R.id.fragContainer,selectedFrag).commit();
            return true;
        }
    };



}
