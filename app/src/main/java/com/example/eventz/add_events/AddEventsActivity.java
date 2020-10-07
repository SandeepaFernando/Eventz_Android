package com.example.eventz.add_events;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.eventz.R;
import com.example.eventz.filter.FilterActivity;
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
import java.util.Map;
import java.util.Objects;

public class AddEventsActivity extends AppCompatActivity implements SkillAdapterAddEvent.onItemClickListener {
    User user = new User();
    String token;
    String userType;
    String userId;
    boolean your_date_is_outdated;
    private TextInputLayout textInputTitle;
    private TextInputLayout textInputDescription;
    private TextInputLayout textInputVenue;
    private TextInputLayout textInputNumOfGust;
    private TextInputLayout textInputAddBudget;
    private TextInputLayout textInputDateLayout;
    private EditText textInputDate;
    Button btn_skillPredict;
    private RequestQueue mRequestQueuePredict;
    private RequestQueue mRequestQueueaddSkills;
    private ArrayList<SkillItemAddEvent> mSkillList;
    private ArrayList<SkillItemAddEvent> mSkillListAdd;
    private RecyclerView mRecyclerViewPredict;
    private RecyclerView mRecyclerViewAddSkills;
    private SkillAdapterAddEvent mskillAdapter;
    private ArrayList<String> skillIdArr;
    private ArrayList<String> skillNameArr;
    LinearLayout linearLayout_predict;
    ImageView addSkillsImg;
    Calendar calendar;
    DatePickerDialog pickerDialog;

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
        linearLayout_predict = findViewById(R.id.predicted_skills_layout);
        addSkillsImg = findViewById(R.id.img_add_skills_addevent);
        textInputDate = findViewById(R.id.text_input_pick_date_add_event);
        textInputDate.setFocusableInTouchMode(false);

        btn_skillPredict = findViewById(R.id.btn_predict_skills);

        mRequestQueuePredict = Volley.newRequestQueue(this);
        mRecyclerViewPredict = findViewById(R.id.recyclerView__skill_predict);
        mRecyclerViewPredict.setLayoutManager(new LinearLayoutManager(this));

        mRequestQueueaddSkills = Volley.newRequestQueue(this);
        mRecyclerViewAddSkills = findViewById(R.id.recyclerViewSkill_add_event);
        mRecyclerViewAddSkills.setLayoutManager(new LinearLayoutManager(this));

        mSkillList = new ArrayList<>();
        mSkillListAdd = new ArrayList<>();
        skillIdArr = new ArrayList<>();
        skillNameArr = new ArrayList<>();

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
                        textInputDate.setText(dayOfMonth + "/" + month + "/" + year);
                    }
                }, day, month, year);
                pickerDialog.updateDate(year, month, day);
                pickerDialog.show();
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


    }


    private boolean validateDate() {
        String date = textInputDate.toString().trim();
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

        String minBudgetInput = textInputDescription.getEditText().getText().toString().trim();
        String url = getString(R.string.ip);
        String URL = url + "predictTags?text=" + minBudgetInput;

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


    @Override
    public void onItemClick(int position) {
        Log.i("POSITION", String.valueOf(position));
        mSkillList.remove(position);
        mskillAdapter.notifyItemRemoved(position);
        skillIdArr.remove(position);
        skillNameArr.remove(position);

        Log.i("AFTERREMOVE", skillIdArr.toString());
        Log.i("AFTERREMOVE", skillNameArr.toString());
    }
}