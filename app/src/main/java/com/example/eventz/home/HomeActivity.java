package com.example.eventz.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.eventz.R;
import com.example.eventz.add_events.AddEventsActivity;
import com.example.eventz.chatbot.ChatActivity;
import com.example.eventz.eventInfo.Event_infoActivity;
import com.example.eventz.filter.FilterActivity;
import com.example.eventz.preferences.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.messaging.FirebaseMessaging;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    User user = new User();
    String userType, userName, userEmail, userId, token;
    TextView nav_userName, nav_email;
    View nView;
    String outputjsonBid;
    private RequestQueue mRequestQueue;
    private int mStatusCode = 0;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
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
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);


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

        initFCM();

        SharedPreferences sp = user.retrieveUserData(getApplicationContext());
        userType = sp.getString("USERTYPE", "");
        userName = sp.getString("USERNAME", "User NAme");
        userEmail = sp.getString("USER_EMAIL", "User Email");
        userId = sp.getString("USER_ID", "User Email");
        token = sp.getString("KEY_TOKEN", "");

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

    private void initFCM() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    Log.w("TAG", "Fetching FCM registration token failed", task.getException());
                    return;
                }
                // Get new FCM registration token
                String FCMtoken = task.getResult();

                postToken(FCMtoken);
                Log.d("TAGFCM - ", FCMtoken);


            }
        });
    }

    private void postToken(final String FCMtoken) {
        mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        String url = getString(R.string.ip);
        String URL = url + "updatePushToken";
        StringRequest request = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("RESPONS", response.toString());

                        //if (mStatusCode == 200) {
                        Log.i("FCM - ", "FCM Updated");
                        //}
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error.getMessage());
                Toast.makeText(getApplicationContext(), "Check your connectivity", Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("push_token", FCMtoken);
                params.put("device_id", userId);
                params.put("user_id", userId);
                Log.i("PARAMS- ", params.toString());
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Token " + token);

                return params;
            }
        };

        int socketTimeout = 500000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);

        mRequestQueue.add(request);
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
