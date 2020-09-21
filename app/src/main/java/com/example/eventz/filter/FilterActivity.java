package com.example.eventz.filter;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
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
import com.example.eventz.eventInfo.EventSkillAdapter;
import com.example.eventz.eventInfo.EventSkillInfo;
import com.example.eventz.eventInfo.Event_infoActivity;
import com.example.eventz.preferences.User;
import com.example.eventz.register.RegVenderScrollingActivity;
import com.example.eventz.register.SkillAdapter;
import com.example.eventz.register.SkillItem;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FilterActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    User user = new User();
    Calendar calendar;
    DatePickerDialog pickerDialog;
    private TextInputLayout textInputBudget;
    private TextInputLayout text_input_date_layout;
    private EditText textInputEventDate;
    private Button btnFilter;
    Boolean your_date_is_outdated;
    private RequestQueue mRequestQueue, mRequestQueueGetVendor;
    private ArrayList<String> skillIdArr;
    Spinner spinner;
    String tagId;
    String token;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        textInputBudget = findViewById(R.id.text_input_min);
        textInputEventDate = findViewById(R.id.text_input_date);
        text_input_date_layout = findViewById(R.id.text_input_date_layout);
        btnFilter = findViewById(R.id.button_filter);
        textInputEventDate.setFocusableInTouchMode(false);
        mRequestQueue = Volley.newRequestQueue(this);
        mRequestQueueGetVendor = Volley.newRequestQueue(this);
        skillIdArr = new ArrayList<>();

        SharedPreferences sp = user.retrieveUserData(Objects.requireNonNull(getApplicationContext()));
        token = sp.getString("KEY_TOKEN", "");

        spinner = (Spinner) findViewById(R.id.spinner_skill);
        spinner.setOnItemSelectedListener(this);

        fillSkillData();

        textInputEventDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("CLICK ", " Calender");
                calendar = Calendar.getInstance();
                final int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                pickerDialog = new DatePickerDialog(FilterActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month = month + 1;
                        textInputEventDate.setText(dayOfMonth + "/" + month + "/" + year);
                    }
                }, day, month, year);
                pickerDialog.updateDate(year, month, day);
                pickerDialog.show();
            }
        });

        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateDate() | !validateminBudget()){
                    return;
                }
                Log.i("CLICK", "validateted");
                String minBudgetInput = textInputBudget.getEditText().getText().toString().trim();
                String date = textInputEventDate.getText().toString().trim();

                getVendors(minBudgetInput, date, tagId);

            }
        });
    }

    private void getVendors(String minBudgetInput, String date, String tagId) {
        String end_num = getString(R.string.url_end);
        String URL = "http://192.168.1." + end_num + ":8000/filterVendors";

        String finalURL = URL + "?budget=" + minBudgetInput + "&tags=" + tagId + "&eventDate=" + date;

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
//                                String id = String.valueOf(jsonObject.getInt("id"));
//                                title = jsonObject.getString("title");
//                                description = jsonObject.getString("description");
//                                eventDate = jsonObject.getString("eventDate");
//                                venue = jsonObject.getString("venue");
//                                noOfGuests = jsonObject.getString("noOfGuests");
//
//                                JSONArray tags = jsonObject.getJSONArray("eventTags");
//                                Log.i("TAGARR", tags.toString());
//
//                                for (int i = 0; i < tags.length(); i++) {
//                                    JSONObject tagObject = tags.getJSONObject(i);
//                                    String tagName = tagObject.getString("tagName");
//
//                                    mSkillList.add(new EventSkillInfo(tagName));
//                                }
//
//                                JSONObject organizerobj = jsonObject.getJSONObject("organizer");
//                                fname = organizerobj.getString("first_name");
//                                email = organizerobj.getString("email");

                            }

//                            mskillAdapter = new EventSkillAdapter(Event_infoActivity.this, mSkillList);
//                            mRecyclerView.setAdapter(mskillAdapter);
//
//                            titleTV.setText(title);
//                            descriptionTV.setText(description);
//                            venueTV.setText(venue);
//                            dateTV.setText(eventDate);
//                            num_peopleTV.setText(noOfGuests);
//                            f_nameTV.setText(fname);
//                            emailTV.setText(email);
//                            Log.i("UTYPE", userType);
//
//                            //======================== If Organizer can Edit(2)======================
//                            if (userType.equals("2")) {
//                                Log.i("UTYPE", userType);
//                                edit_eventBTN.setVisibility(View.VISIBLE);
//                                edit_eventBTN.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        Log.i("CILCK ", " Click on edit Event");
//                                        edit_eventActivity(eventId, token);
//                                    }
//                                });
//                            }

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

        mRequestQueueGetVendor.add(request);
    }

    void fillSkillData() {
        String end_num = getString(R.string.url_end);
        String URL = "http://192.168.1." + end_num + ":8000/getTags";

        final JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, URL, null,
                new Response.Listener<JSONArray>() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onResponse(JSONArray response) {
                        try {

                            Log.i("RESPONSE", response.toString());

                            List<String> categories = new ArrayList<String>();
                            for (int j = 0; j < response.length(); j++) {
                                JSONObject jsonObject = response.getJSONObject(j);

                                String skillID = String.valueOf(jsonObject.getInt("id"));
                                String tagName = jsonObject.getString("tagName");

                                categories.add(tagName);
                                skillIdArr.add(skillID);

                            }

                            Log.i("ARRRRR", skillIdArr.toString());

                            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(FilterActivity.this, android.R.layout.simple_spinner_item, categories);

                            // Drop down layout style - list view with radio button
                            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                            // attaching data adapter to spinner
                            spinner.setAdapter(dataAdapter);

                        } catch (JSONException e) {
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

        };

        int socketTimeout = 500000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);

        mRequestQueue.add(request);
    }


    private boolean validateDate() {
        String date = textInputEventDate.getText().toString().trim();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date strDate = sdf.parse(date);
            if (new Date().after(strDate)) {
                your_date_is_outdated = true;
            } else {
                your_date_is_outdated = false;
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date.isEmpty()) {
            text_input_date_layout.setError("Date can't be empty");
            return false;

        } else if (your_date_is_outdated) {
            text_input_date_layout.setError("Your Date Is Outdated");
            return false;

        } else {
            text_input_date_layout.setError(null);
            return true;
        }
    }

    private boolean validateminBudget() {
        String minBudgetInput = textInputBudget.getEditText().getText().toString().trim();
        if (minBudgetInput.isEmpty()) {
            textInputBudget.setError("Field can't be empty");
            return false;
        } else {
            textInputBudget.setError(null);
            return true;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();
        Log.i("ITEM - ", item);
        tagId = skillIdArr.get(position);
        Log.i("tagId - ", tagId);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}