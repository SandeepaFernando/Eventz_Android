package com.example.eventz.updateEvent;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.eventz.MainActivity;
import com.example.eventz.R;
import com.example.eventz.preferences.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class UpdateEvents extends AppCompatActivity implements SkillAdapterUpdateEvent.onItemClickListener {
    User user = new User();
    String token;
    String userType;
    String userId;
    String eventId;
    private RequestQueue mRequestQueueupdateEvent;
    private RequestQueue mRequestQueueUpdate;
    private RequestQueue mRequestQueueFillSkills;
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
    Button btnUpdate;
    String title, description, venue, noFoGuest, budget, date, time;
    private ArrayList<String> skillIdArr;
    Calendar calendar;
    DatePickerDialog pickerDialog;
    TimePickerDialog timePickerDialog;
    String dateIn_IN;
    ImageView imgGetAllSkills;
    private int mStatusCode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_events);

        SharedPreferences sp = user.retrieveUserData(getApplicationContext());
        userType = sp.getString("USERTYPE", "");
        token = sp.getString("KEY_TOKEN", "");
        userId = sp.getString("USER_ID", "");

        mRequestQueueupdateEvent = Volley.newRequestQueue(this);
        mRequestQueueUpdate = Volley.newRequestQueue(this);
        mRequestQueueFillSkills = Volley.newRequestQueue(this);
        mRecyclerViewEventUpdate = findViewById(R.id.recyclerViewSkill_update_event);
        mRecyclerViewEventUpdate.setLayoutManager(new LinearLayoutManager(this));
        mSkillList = new ArrayList<>();
        skillIdArr = new ArrayList<>();

        editTitle = findViewById(R.id.text_input_title_update_event);
        editDescription = findViewById(R.id.text_input_description_add_event);
        editVenue = findViewById(R.id.text_input_venue_update_event);
        editNoFoGuest = findViewById(R.id.text_input_noOfGuests_update_event);
        editBudget = findViewById(R.id.text_input_eventBudget_update_event);
        editDate = findViewById(R.id.text_input_pick_date_update_event);
        editDate.setFocusableInTouchMode(false);
        editTime = findViewById(R.id.text_input_pick_time_update_event);
        editTime.setFocusableInTouchMode(false);
        btnUpdate = findViewById(R.id.button_update_event_now);
        imgGetAllSkills = findViewById(R.id.img_add_skills_update_event);


        final Intent intent = getIntent();
        eventId = intent.getStringExtra("EVENTID");

        getEventById();


        imgGetAllSkills.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAllSkills();
            }
        });

        editDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("CLICK ", " Calender");
                calendar = Calendar.getInstance();
                final int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                pickerDialog = new DatePickerDialog(UpdateEvents.this, new DatePickerDialog.OnDateSetListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month = month + 1;
                        editDate.setText(year + "-" + month + "-" + dayOfMonth);
                        dateIn_IN = dayOfMonth + "/" + month + "/" + year;
                    }
                }, day, month, year);
                pickerDialog.updateDate(year, month, day);
                pickerDialog.show();
            }
        });

        editTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                timePickerDialog = new TimePickerDialog(UpdateEvents.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        editTime.setText(hourOfDay + ":" + minute);
                    }
                }, hour, minute, false);
                timePickerDialog.updateTime(hour, minute);
                timePickerDialog.show();
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateEvent();
            }
        });
    }

    private void getAllSkills() {
        skillIdArr.clear();
        mSkillList.clear();

        String url = getString(R.string.ip);
        String URL = url + "getTags";

        final JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, URL, null,
                new Response.Listener<JSONArray>() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onResponse(JSONArray response) {
                        try {

                            Log.i("RESPONSE", response.toString());

                            for (int j = 0; j < response.length(); j++) {
                                JSONObject jsonObject = response.getJSONObject(j);
                                Log.i("INSIDEOBJECR", jsonObject.toString());
                                String skillID = String.valueOf(jsonObject.getInt("id"));
                                String tagName = jsonObject.getString("tagName");

                                skillIdArr.add(skillID);
                                mSkillList.add(new SkillItemUpdateEvent(tagName));

                            }
                            Log.i("ARRRRR", skillIdArr.toString());

                            mskillAdapter = new SkillAdapterUpdateEvent(UpdateEvents.this, mSkillList);
                            mRecyclerViewEventUpdate.setAdapter(mskillAdapter);
                            SkillAdapterUpdateEvent.setOnItemClickListener(UpdateEvents.this);


                        } catch (JSONException e) {
                            Toast.makeText(UpdateEvents.this, "error", Toast.LENGTH_LONG).show();

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        Toast.makeText(UpdateEvents.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.v("VOLLEY", error.toString());
                    }
                }

            }
        }) {

        };

        int socketTimeout = 500000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);

        mRequestQueueFillSkills.add(request);
    }

    private void updateEvent() {
        title = editTitle.getText().toString();
        description = editDescription.getText().toString();
        venue = editVenue.getText().toString();
        noFoGuest = editNoFoGuest.getText().toString();
        budget = editBudget.getText().toString();
        date = editDate.getText().toString();
        time = editTime.getText().toString();

        String dateTime = date + " " + time;
        JSONArray jsonSkillArray = new JSONArray();
        JSONObject json2 = new JSONObject();
        final String outputjson;

        try {
            for (int i = 0; i < skillIdArr.size(); i++) {
                JSONObject json1 = new JSONObject();
                json1.put("tagId", skillIdArr.get(i));
                jsonSkillArray.put(json1);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            json2.put("id", eventId);
            json2.put("title", title);
            json2.put("description", description);
            json2.put("eventDate", dateTime);
            json2.put("venue", venue);
            json2.put("noOfGuests", noFoGuest);
            json2.put("organizer", userId);
            json2.put("eventBudget", budget);
            json2.put("eventTags", jsonSkillArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        outputjson = json2.toString();
        Log.i("OUTJSON", outputjson);

        String url = getString(R.string.ip);
        String URL = url + "events";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("RESPONS", response.toString());
                        if (mStatusCode == 201) {
                            Intent intent = new Intent(UpdateEvents.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);

                            Toast.makeText(UpdateEvents.this, "Event Updated..", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error.getMessage());
                Toast.makeText(UpdateEvents.this, "Check your connectivity", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Token " + token);

                return params;
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() {
                return outputjson == null ? null : outputjson.getBytes(Charset.forName("UTF-8"));
            }

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                if (response != null) {
                    mStatusCode = response.statusCode;
                }
                return super.parseNetworkResponse(response);
            }

        };

        int socketTimeout = 500000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);

        mRequestQueueUpdate.add(request);
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
                                    String tagId = tagObject.getString("tagId");

                                    skillIdArr.add(tagId);
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
        skillIdArr.remove(position);
    }
}