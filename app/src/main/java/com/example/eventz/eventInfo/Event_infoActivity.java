package com.example.eventz.eventInfo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.eventz.preferences.User;
import com.example.eventz.register.RegVenderScrollingActivity;
import com.example.eventz.register.SkillAdapter;
import com.example.eventz.register.SkillItem;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eventz.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Event_infoActivity extends AppCompatActivity {
    User user = new User();
    String eventId;
    private ArrayList<EventSkillInfo> mSkillList;
    private RecyclerView mRecyclerView;
    private EventSkillAdapter mskillAdapter;
    private RequestQueue mRequestQueueReg;
    TextView titleTV, descriptionTV, venueTV, dateTV, num_peopleTV, f_nameTV, emailTV;
    String title, description, eventDate, venue, noOfGuests, fname, email;
    String token;
    String userType;
    Button edit_eventBTN;
    //String URL = "http://192.168.1.103:8000/events";

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        CollapsingToolbarLayout toolBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        toolBarLayout.setTitle(getTitle());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //-----------------------------------------------------------------------------
        titleTV = findViewById(R.id.txtview_title);
        descriptionTV = findViewById(R.id.txtview_description);
        venueTV = findViewById(R.id.txtview_venue);
        dateTV = findViewById(R.id.txtview_date);
        num_peopleTV = findViewById(R.id.txtview_numofpeople);
        f_nameTV = findViewById(R.id.txtview_fname);
        emailTV = findViewById(R.id.txtview_email);
        edit_eventBTN = findViewById(R.id.button_edit_event);


        SharedPreferences sp = user.retrieveUserData(Objects.requireNonNull(getApplicationContext()));
        userType = sp.getString("USERTYPE", "");
        token = sp.getString("KEY_TOKEN", "");


        mSkillList = new ArrayList<>();
        mRecyclerView = findViewById(R.id.event_skill_set);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        getEventbyid();

    }


    void getEventbyid() {
        mRequestQueueReg = Volley.newRequestQueue(this);
        final Intent intent = getIntent();
        eventId = intent.getStringExtra("eventId");
        Log.i("EXTRASITE_ID", eventId);

        String end_num = getString(R.string.url_end);
        String URL = "http://192.168.1." + end_num + ":8000/events";

        String finalURL = URL + "?eventId=" + eventId;

        final JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, finalURL, null,
                new Response.Listener<JSONArray>() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onResponse(JSONArray response) {
                        try {

                            Log.i("RESPONSEINFO", response.toString());

                            for (int j = 0; j < response.length(); j++) {
                                JSONObject jsonObject = response.getJSONObject(j);
                                Log.i("INSIDEOBJECR", jsonObject.toString());
                                String id = String.valueOf(jsonObject.getInt("id"));
                                title = jsonObject.getString("title");
                                description = jsonObject.getString("description");
                                eventDate = jsonObject.getString("eventDate");
                                venue = jsonObject.getString("venue");
                                noOfGuests = jsonObject.getString("noOfGuests");

                                JSONArray tags = jsonObject.getJSONArray("eventTags");
                                Log.i("TAGARR", tags.toString());

                                for (int i = 0; i < tags.length(); i++) {
                                    JSONObject tagObject = tags.getJSONObject(i);
                                    String tagName = tagObject.getString("tagName");

                                    mSkillList.add(new EventSkillInfo(tagName));
                                }

                                JSONObject organizerobj = jsonObject.getJSONObject("organizer");
                                fname = organizerobj.getString("first_name");
                                email = organizerobj.getString("email");

                            }

                            mskillAdapter = new EventSkillAdapter(Event_infoActivity.this, mSkillList);
                            mRecyclerView.setAdapter(mskillAdapter);

                            titleTV.setText(title);
                            descriptionTV.setText(description);
                            venueTV.setText(venue);
                            dateTV.setText(eventDate);
                            num_peopleTV.setText(noOfGuests);
                            f_nameTV.setText(fname);
                            emailTV.setText(email);
                            Log.i("UTYPE", userType);

                            //======================== If Organizer can Edit(2)======================
                            if (userType.equals("2")) {
                                Log.i("UTYPE", userType);
                                edit_eventBTN.setVisibility(View.VISIBLE);
                                edit_eventBTN.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Log.i("CILCK ", " Click on edit Event");
                                        edit_eventActivity(eventId, token);
                                    }
                                });
                            }

                        } catch (JSONException e) {
                            Log.i("JSONException", e.getMessage());
                            Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_LONG).show();

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.v("VOLLEY", error.toString());
                    }
                }

            }
        }) {
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

        mRequestQueueReg.add(request);
    }

    private void edit_eventActivity(String eventId, String token) {
    }

}