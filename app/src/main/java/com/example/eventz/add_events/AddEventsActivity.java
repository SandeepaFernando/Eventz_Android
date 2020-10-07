package com.example.eventz.add_events;

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
import android.widget.LinearLayout;
import android.widget.TextView;
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
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddEventsActivity extends AppCompatActivity implements SkillAdapterAddEvent.onItemClickListener {
    User user = new User();
    String token;
    String userType;
    String userId;
    Boolean your_date_is_outdated;
    private TextInputLayout textInputTitle;
    private TextInputLayout textInputDescription;
    private TextInputLayout textInputVenue;
    private TextInputLayout textInputNumOfGust;
    private TextInputLayout textInputAddBudget;
    private TextInputLayout textInputDateLayout;
    private TextInputLayout textInputTimeLayout;
    private EditText textInputDate;
    private EditText textInputTime;
    Button btn_skillPredict;
    Button btn_numOfVendors;
    Button btn_post;
    private RequestQueue mRequestQueuePredict;
    private RequestQueue mRequestQueueaddSkills;
    private RequestQueue mRequestQueueNumOfVendors;
    private RequestQueue mRequestQueuePost;
    private ArrayList<SkillItemAddEvent> mSkillList;
    private ArrayList<SkillItemAddEvent> mSkillListAdd;
    private RecyclerView mRecyclerViewPredict;
    private RecyclerView mRecyclerViewAddSkills;
    private SkillAdapterAddEvent mskillAdapter;
    private ArrayList<String> skillIdArr;
    private ArrayList<String> skillNameArr;
    private ArrayList<String> categoryArr;
    LinearLayout linearLayout_predict;
    LinearLayout linearLayout_numOfVendors;
    ImageView addSkillsImg;
    ImageView clearnNmViewImg;
    Calendar calendar;
    DatePickerDialog pickerDialog;
    TimePickerDialog timePickerDialog;
    TextView numPlattinum;
    TextView numGold;
    TextView numSilver;
    private int mStatusCode = 0;
    String dateIn_IN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_events);

        SharedPreferences sp = user.retrieveUserData(getApplicationContext());
        userType = sp.getString("USERTYPE", "");
        token = sp.getString("KEY_TOKEN", "");
        userId = sp.getString("USER_ID", "");

        Log.i("HOME-SP", userType + " " + token + " UID=" + userId);

        textInputTitle = findViewById(R.id.text_input_title_add_event);
        textInputDescription = findViewById(R.id.input_description_add_event);
        textInputVenue = findViewById(R.id.text_input_venue_add_event);
        textInputNumOfGust = findViewById(R.id.text_input_noOfGuests_add_event);
        textInputAddBudget = findViewById(R.id.text_input_eventBudget_add_event);
        textInputDateLayout = findViewById(R.id.text_input_date_add_event);
        textInputTimeLayout = findViewById(R.id.text_input_time_add_event);
        linearLayout_predict = findViewById(R.id.predicted_skills_layout);
        linearLayout_numOfVendors = findViewById(R.id.num_of_vendor_skills_layout);
        addSkillsImg = findViewById(R.id.img_add_skills_addevent);
        textInputDate = findViewById(R.id.text_input_pick_date_add_event);
        textInputDate.setFocusableInTouchMode(false);
        textInputTime = findViewById(R.id.text_input_pick_time_add_event);
        textInputTime.setFocusableInTouchMode(false);
        numPlattinum = findViewById(R.id.txt_num_of_plattinum);
        numGold = findViewById(R.id.txt_num_of_gold);
        numSilver = findViewById(R.id.txt_num_of_silver);
        clearnNmViewImg = findViewById(R.id.img_clear_all_num_view);

        btn_skillPredict = findViewById(R.id.btn_predict_skills);
        btn_numOfVendors = findViewById(R.id.btn_num_of_vendors);
        btn_post = findViewById(R.id.button_post_event_now);

        mRequestQueuePredict = Volley.newRequestQueue(this);
        mRecyclerViewPredict = findViewById(R.id.recyclerView__skill_predict);
        mRecyclerViewPredict.setLayoutManager(new LinearLayoutManager(this));

        mRequestQueueaddSkills = Volley.newRequestQueue(this);
        mRecyclerViewAddSkills = findViewById(R.id.recyclerViewSkill_add_event);
        mRecyclerViewAddSkills.setLayoutManager(new LinearLayoutManager(this));

        mRequestQueueNumOfVendors = Volley.newRequestQueue(this);
        mRequestQueuePost = Volley.newRequestQueue(this);

        mSkillList = new ArrayList<>();
        mSkillListAdd = new ArrayList<>();
        skillIdArr = new ArrayList<>();
        skillNameArr = new ArrayList<>();
        categoryArr = new ArrayList<>();


        textInputDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("CLICK ", " Calender");
                calendar = Calendar.getInstance();
                final int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                pickerDialog = new DatePickerDialog(AddEventsActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month = month + 1;
                        textInputDate.setText(year + "-" + month + "-" + dayOfMonth);
                        dateIn_IN = dayOfMonth + "/" + month + "/" + year;
                    }
                }, day, month, year);
                pickerDialog.updateDate(year, month, day);
                pickerDialog.show();
            }
        });

        textInputTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                timePickerDialog = new TimePickerDialog(AddEventsActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        textInputTime.setText(hourOfDay + ":" + minute);
                    }
                }, hour, minute, false);
                timePickerDialog.updateTime(hour, minute);
                timePickerDialog.show();
            }
        });


        btn_skillPredict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateDescription()) {
                    getPredictedSkills();
                    if (!mSkillListAdd.isEmpty()) {
                        Log.i("Adapter- ", "Clear - Skills");
                        mSkillListAdd.clear();
                        mskillAdapter.notifyDataSetChanged();
                        skillIdArr.clear();
                        skillNameArr.clear();
                    }
                } else
                    Toast.makeText(AddEventsActivity.this, "You Need To Provide A Description!", Toast.LENGTH_LONG).show();
            }
        });

        addSkillsImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fillSkills();
                if (!mSkillList.isEmpty()) {
                    Log.i("Adapter- ", "Clear - Predict");
                    mSkillList.clear();
                    mskillAdapter.notifyDataSetChanged();
                    skillIdArr.clear();
                    skillNameArr.clear();
                }
            }
        });

        btn_numOfVendors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateminBudget() && !skillIdArr.isEmpty()) {
                    getNumOfVendors();

                } else
                    Toast.makeText(AddEventsActivity.this, "Please Provide Budget and Skills!", Toast.LENGTH_LONG).show();


            }
        });

        clearnNmViewImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //clear num of vendor view
                linearLayout_numOfVendors.setVisibility(View.GONE);
            }
        });

        btn_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateminBudget() && !skillIdArr.isEmpty() && validateDescription() && validateDate() && validateNumOfGuest() && validateTitle() && validateVenue() && validateTime()) {
                    postAdd();
                    Log.i("POS--", " POSTED");
                }
                if (!validateminBudget() | !validateDescription() | !validateDate() | !validateNumOfGuest() | !validateTitle() | !validateVenue() | validateTime()) {
                    return;
                }
            }
        });

    }

    private void postAdd() {
        Log.i("TAG-POS - ", "IN POST");
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
            json2.put("title", textInputTitle.getEditText().getText().toString());
            json2.put("description", textInputDescription.getEditText().getText().toString());
            json2.put("eventDate", textInputDate.getText().toString() + " " + textInputTime.getText().toString());
            json2.put("venue", textInputVenue.getEditText().getText().toString());
            json2.put("noOfGuests", textInputNumOfGust.getEditText().getText().toString());
            json2.put("organizer", userId);
            json2.put("eventBudget", textInputAddBudget.getEditText().getText().toString());
            json2.put("eventTags", jsonSkillArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        outputjson = json2.toString();
        Log.i("OUTJSON", outputjson);

        String url = getString(R.string.ip);
        String URL_REG = url + "events";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL_REG, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("RESPONS", response.toString());
                        if (mStatusCode == 201) {
                            Intent intent = new Intent(AddEventsActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();

                            Toast.makeText(AddEventsActivity.this, "Event Successfully Posted..", Toast.LENGTH_LONG).show();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error.getMessage());
                Toast.makeText(AddEventsActivity.this, "Check your connectivity", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() {
                return outputjson == null ? null : outputjson.getBytes(Charset.forName("UTF-8"));
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Token " + token);

                return params;
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

        mRequestQueuePost.add(request);
    }


    private boolean validateDate() {
        String date = textInputDate.getText().toString().trim();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date strDate = sdf.parse(dateIn_IN);
            Log.i("STARTDATE - ", strDate.toString());
            if (new Date().after(strDate)) {
                your_date_is_outdated = true;
                Log.i("new Date() - ", new Date().toString());
            } else {
                your_date_is_outdated = false;
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date.isEmpty()) {
            textInputDateLayout.setError("Date can't be empty");
            return false;

        } else if (your_date_is_outdated) {
            textInputDateLayout.setError("Your Date Is Outdated");
            return false;

        } else {
            textInputDateLayout.setError(null);
            return true;
        }
    }

    private boolean validateTime() {
        String minBudgetInput = textInputTime.getText().toString().trim();
        if (minBudgetInput.isEmpty()) {
            textInputTimeLayout.setError("Field can't be empty");
            return false;
        } else {
            textInputTimeLayout.setError(null);
            return true;
        }
    }


    private boolean validateminBudget() {
        String minBudgetInput = textInputAddBudget.getEditText().getText().toString().trim();
        if (minBudgetInput.isEmpty()) {
            textInputAddBudget.setError("Field can't be empty");
            return false;
        } else {
            textInputAddBudget.setError(null);
            return true;
        }
    }

    private boolean validateDescription() {
        String minBudgetInput = textInputDescription.getEditText().getText().toString().trim();
        if (minBudgetInput.isEmpty()) {
            textInputDescription.setError("Field can't be empty");
            return false;
        } else {
            textInputDescription.setError(null);
            return true;
        }
    }

    private boolean validateVenue() {
        String venue = textInputVenue.getEditText().getText().toString().trim();
        if (venue.isEmpty()) {
            textInputVenue.setError("Field can't be empty");
            return false;
        } else {
            textInputVenue.setError(null);
            return true;
        }
    }

    private boolean validateNumOfGuest() {
        String numOfGuest = textInputNumOfGust.getEditText().getText().toString().trim();
        if (numOfGuest.isEmpty()) {
            textInputNumOfGust.setError("Field can't be empty");
            return false;
        } else {
            textInputNumOfGust.setError(null);
            return true;
        }
    }

    private boolean validateTitle() {
        String title = textInputTitle.getEditText().getText().toString().trim();
        if (title.isEmpty()) {
            textInputTitle.setError("Field can't be empty");
            return false;
        } else {
            textInputTitle.setError(null);
            return true;
        }
    }

    private void getPredictedSkills() {
        if (!mSkillList.isEmpty()) {
            Log.i("Adapter- ", "Clear - Predict");
            mSkillList.clear();
            mskillAdapter.notifyDataSetChanged();
            skillIdArr.clear();
            skillNameArr.clear();
        }

        String description = textInputDescription.getEditText().getText().toString().trim();
        String url = getString(R.string.ip);
        String URL = url + "predictTags?text=" + description;

        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL, null,
                new Response.Listener<JSONObject>() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            Log.i("RESPONSE", response.toString());

                            JSONArray tags = response.getJSONArray("tags");

                            for (int j = 0; j < tags.length(); j++) {
                                JSONObject jsonObject = tags.getJSONObject(j);
                                Log.i("INSIDEOBJECR", jsonObject.toString());
                                String skillID = String.valueOf(jsonObject.getInt("tagId"));
                                String tagName = jsonObject.getString("tagName");

                                skillIdArr.add(skillID);
                                skillNameArr.add(tagName);

                                mSkillList.add(new SkillItemAddEvent(tagName));

                            }

                            Log.i("ARRRRR", skillIdArr.toString());
                            Log.i("ARRRRR", skillNameArr.toString());

                            mskillAdapter = new SkillAdapterAddEvent(AddEventsActivity.this, mSkillList);
                            mRecyclerViewPredict.setAdapter(mskillAdapter);
                            SkillAdapterAddEvent.setOnItemClickListener(AddEventsActivity.this);

                            // set visible
                            linearLayout_predict.setVisibility(View.VISIBLE);

                            if (tags.length() == 0) {
                                // if empty set visible gone
                                linearLayout_predict.setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(), "Your Description Not Match With Our Data", Toast.LENGTH_LONG).show();
                            }

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

        mRequestQueuePredict.add(request);
    }

    private void fillSkills() {
        if (!mSkillListAdd.isEmpty()) {
            Log.i("Adapter- ", "Clear - Skills");
            mSkillListAdd.clear();
            mskillAdapter.notifyDataSetChanged();
            skillIdArr.clear();
            skillNameArr.clear();
        }
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
                                skillNameArr.add(tagName);

                                mSkillListAdd.add(new SkillItemAddEvent(tagName));

                            }

                            Log.i("ARRRRR", skillIdArr.toString());
                            Log.i("ARRRRR", skillNameArr.toString());

                            mskillAdapter = new SkillAdapterAddEvent(AddEventsActivity.this, mSkillListAdd);
                            mRecyclerViewAddSkills.setAdapter(mskillAdapter);
                            SkillAdapterAddEvent.setOnItemClickListener(AddEventsActivity.this);

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

        mRequestQueueaddSkills.add(request);
    }

    private void getNumOfVendors() {
        if (!categoryArr.isEmpty()) {
            categoryArr.clear();
        }

        String minBudgetInput = textInputAddBudget.getEditText().getText().toString().trim();
        String tagString = skillIdArr.toString();
        String tags = tagString.replaceAll("[\\[\\]() ]", "");
        String url = getString(R.string.ip);
        String URL = url + "getVendorsByBudgetAndTags?budget=" + minBudgetInput + "&tags=" + tags.trim();

        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL, null,
                new Response.Listener<JSONObject>() {
                    @SuppressLint("SetTextI18n")
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            Log.i("RESPONSE", response.toString());

                            JSONArray result = response.getJSONArray("result");

                            for (int j = 0; j < result.length(); j++) {
                                JSONObject jsonObject = result.getJSONObject(j);
                                Log.i("INSIDEOBJECR", jsonObject.toString());
                                String num = String.valueOf(jsonObject.getInt("numberOfVendors"));
                                String category = jsonObject.getString("rateCategory");

                                categoryArr.add(category);

                                switch (category) {
                                    case "Platinum":
                                        numPlattinum.setText(num + " Vendors");
                                        break;

                                    case "Gold":
                                        numGold.setText(num + " Vendors");
                                        break;

                                    case "Silver":
                                        numSilver.setText(num + " Vendors");
                                        break;
                                }

                            }

                            if (!categoryArr.contains("Platinum")) {
                                numPlattinum.setText("0" + " Vendors");

                            } else if (!categoryArr.contains("Gold")) {
                                numGold.setText("0" + " Vendors");

                            } else if (!categoryArr.contains("Silver")) {
                                numSilver.setText("0" + " Vendors");

                            }

                            // set visible
                            linearLayout_numOfVendors.setVisibility(View.VISIBLE);

                            if (result.length() == 0) {
                                // if empty set visible gone
                                linearLayout_numOfVendors.setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(), "No Results Found", Toast.LENGTH_LONG).show();
                            }

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

        mRequestQueueNumOfVendors.add(request);
    }


    @Override
    public void onItemClick(int position) {
        Log.i("POSITION", String.valueOf(position));
        if (!mSkillList.isEmpty()) {
            mSkillList.remove(position);
            mskillAdapter.notifyItemRemoved(position);
        }

        if (!mSkillListAdd.isEmpty()) {
            mSkillListAdd.remove(position);
            mskillAdapter.notifyItemRemoved(position);
        }
        skillIdArr.remove(position);
        skillNameArr.remove(position);

        Log.i("AFTERREMOVE", skillIdArr.toString());
        Log.i("AFTERREMOVE", skillNameArr.toString());
    }
}