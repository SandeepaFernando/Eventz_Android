package com.example.eventz.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eventz.R;
import com.example.eventz.add_events.AddEventsActivity;
import com.example.eventz.chatbot.ChatActivity;
import com.example.eventz.eventInfo.Event_infoActivity;
import com.example.eventz.filter.FilterActivity;
import com.example.eventz.preferences.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class HomeActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    User user = new User();
    String userType, userName, userEmail;
    TextView nav_userName, nav_email;
    View nView;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, ChatActivity.class);
                startActivity(intent);
            }
        });


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        //============================= set User Name And Email==================================
        nView = navigationView.getHeaderView(0);
        nav_email = nView.findViewById(R.id.email_nav);
        nav_userName = nView.findViewById(R.id.userName_nav);


        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        //----------------------------------------------------------------------------------------------------------------------
        //NavigationView navigationView1 = (NavigationView) findViewById(R.id.nav_view);


        SharedPreferences sp = user.retrieveUserData(getApplicationContext());
        userType = sp.getString("USERTYPE", "");
        userName = sp.getString("USERNAME", "User NAme");
        userEmail = sp.getString("USER_EMAIL", "User Email");

        nav_userName.setText(userName);
        nav_email.setText(userEmail);
        Log.i("HOME-SP", userType + userName);

        if (userType.equals("2")) {
            FloatingActionButton fab_event = findViewById(R.id.fab_add_event);
            fab_event.setVisibility(View.VISIBLE);
            fab_event.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(HomeActivity.this, AddEventsActivity.class);
                    startActivity(intent);
                }
            });
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchItem.setVisible(false);
        if (userType.equals("2")) {
            searchItem.setVisible(true);
            searchItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (item.getItemId() == R.id.action_search) {
                        Log.i("CLICK ", " FILTER");
                        Intent intent = new Intent(HomeActivity.this, FilterActivity.class);
                        startActivity(intent);
                        return true;
                    }
                    return false;
                }
            });
        }
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
