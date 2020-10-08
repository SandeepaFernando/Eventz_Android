package com.example.eventz.updateEvent;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

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
import com.example.eventz.R;
import com.example.eventz.home.ui.update_profile.UpdateProfileFragment;
import com.example.eventz.preferences.User;
import com.example.eventz.register.SkillAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UpdateEvents extends AppCompatActivity implements SkillAdapterUpdateEvent.onItemClickListener {
    User user = new User();
    String token;
    String userType;
    String userId;
    String eventId;
    private RequestQueue mRequestQueueupdateEvent;
    private SkillAdapterUpdateEvent mskillAdapter;
    private RecyclerView mRecyclerViewEventUpdate;
    private ArrayList<SkillItemUpdateEvent> mSkillList;
    EditText editTitle;
    EditText editDescription;
    EditText editVenue;
    EditText editNoFoGuest;
    EditText editBudget;
    EditText editDate;
    EditText editTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_events);

        SharedPreferences sp = user.retrieveUserData(getApplicationContext());
        userType = sp.getString("USERTYPE", "");
        token = sp.getString("KEY_TOKEN", "");
        userId = sp.getString("USER_ID", "");

        mRequestQueueupdateEvent = Volley.newRequestQueue(this);
        mRecyclerViewEventUpdate = findViewById(R.id.recyclerViewSkill_update_event);
        mRecyclerViewEventUpdate.setLayoutManager(new LinearLayoutManager(this));
        mSkillList = new ArrayList<>();

        editTitle = findViewById(R.id.text_input_title_update_event);
        editDescription = findViewById(R.id.text_input_description_add_event);
        editVenue = findViewById(R.id.text_input_venue_update_event);
        editNoFoGuest = findViewById(R.id.text_input_noOfGuests_update_event);
        editBudget = findViewById(R.id.text_input_eventBudget_update_event);
        editDate = findViewById(R.id.text_input_pick_date_update_event);
        editTime = findViewById(R.id.text_input_pick_time_update_event);


        final Intent intent = getIntent();
        eventId = intent.getStringExtra("EVENTID");

        getEventById();
    }

    private void getEventById() {
        String url = getString(R.string.ip);
        String URL = url + "events";

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
                                //id = String.valueOf(jsonObject.getInt("id"));
                                String title = jsonObject.getString("title");
                                String description = jsonObject.getString("description");
                                String eventDate = jsonObject.getString("eventDate");
                                String venue = jsonObject.getString("venue");
                                String noOfGuests = jsonObject.getString("noOfGuests");
                                String eventBudget = jsonObject.getString("eventBudget");

                                JSONArray tags = jsonObject.getJSONArray("eventTags");
                                for (int i = 0; i < tags.length(); i++) {
                                    JSONObject tagObject = tags.getJSONObject(i);
                                    String tagName = tagObject.getString("tagName");

                                    mSkillList.add(new SkillItemUpdateEvent(tagName));
                                }

                                editTitle.setText(title);
                                editDescription.setText(description);
                                editVenue.setText(venue);
                                editNoFoGuest.setText(noOfGuests);
                                editBudget.setText(eventBudget);

                                String date = eventDate.substring(0, 10);
                                String time = eventDate.substring(11, 16);
                                editDate.setText(date);
                                editTime.setText(time);

                                mskillAdapter = new SkillAdapterUpdateEvent(UpdateEvents.this, mSkillList);
                                mRecyclerViewEventUpdate.setAdapter(mskillAdapter);
                                SkillAdapterUpdateEvent.setOnItemClickListener(UpdateEvents.this);
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

        mRequestQueueupdateEvent.add(request);
    }

    @Override
    public void onItemClick(int position) {
        Log.i("POSITION", String.valueOf(position));
        mSkillList.remove(position);
        mskillAdapter.notifyItemRemoved(position);
//        skillIdArr.remove(position);
//        skillNameArr.remove(position);
//
//        Log.i("AFTERREMOVE", skillIdArr.toString());
//        Log.i("AFTERREMOVE", skillNameArr.toString());
    }
}