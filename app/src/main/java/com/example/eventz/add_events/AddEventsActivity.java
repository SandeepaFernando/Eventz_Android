package com.example.eventz.add_events;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.example.eventz.R;
import com.example.eventz.preferences.User;

import java.util.Objects;

public class AddEventsActivity extends AppCompatActivity {
    User user = new User();
    String token;
    String userType;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_events);

        SharedPreferences sp = user.retrieveUserData(getApplicationContext());
        userType = sp.getString("USERTYPE", "");
        token = sp.getString("KEY_TOKEN", "");
        userId = sp.getString("USER_ID", "");

        Log.i("HOME-SP", userType + " " + token + " UID=" + userId);

    }
}