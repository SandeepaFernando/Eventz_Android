package com.example.eventz.register;

import android.os.Build;
import android.os.Bundle;

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
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.eventz.R;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class RegVenderScrollingActivity extends AppCompatActivity implements SkillAdapter.onItemClickListener {
    private ArrayList<SkillItem> mSkillList;
    private RecyclerView mRecyclerView;
    private SkillAdapter mskillAdapter;
    private RequestQueue mRequestQueue;
    private TextInputLayout textInputEmail;
    private TextInputLayout textInputUsername;
    private TextInputLayout textInputPassword;
    private TextInputLayout textInputFName;
    private TextInputLayout textInputLName;
    private String emailInput;
    private String usernameInput;
    private String passwordInput;
    private String fNameInput;
    private String lNameInput;
    private Button btnRegister;
    private ArrayList<String> skillIdArr;
    private ArrayList<String> skillNameArr;
    String URL = "http://192.168.1.103:8000/getTags";

    public RegVenderScrollingActivity() {
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_vender_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        toolBarLayout.setTitle(getTitle());

        textInputEmail = findViewById(R.id.text_input_email);
        textInputUsername = findViewById(R.id.text_input_username);
        textInputPassword = findViewById(R.id.text_input_password);
        textInputFName = findViewById(R.id.text_input_FirstName);
        textInputLName = findViewById(R.id.text_input_LastName);
        btnRegister = findViewById(R.id.button_register_vender);
        mSkillList = new ArrayList<>();
        skillIdArr = new ArrayList<>();
        skillNameArr = new ArrayList<>();
        mRequestQueue = Volley.newRequestQueue(this);
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        fillSkillData();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateEmail() | !validateUsername() | !validatePassword() | !validateFname() | !validateLname()) {
                    return;
                }
                registerVender(emailInput, usernameInput, passwordInput, fNameInput, lNameInput, skillIdArr, skillNameArr);

            }
        });

    }

    private void registerVender(String emailInput, String usernameInput, String passwordInput, String fNameInput, String lNameInput, ArrayList<String> skillIdArr, ArrayList<String> skillNameArr) {
        JSONArray jsonSkillArray = new JSONArray();
        JSONObject json2 = new JSONObject();
        final String outputjson;

        try {
            for (int i = 0; i < skillIdArr.size(); i++) {
                JSONObject json1 = new JSONObject();
                json1.put("tagId", skillIdArr.get(i));
                json1.put("tagName", skillNameArr.get(i));
                jsonSkillArray.put(json1);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            json2.put("username", usernameInput);
            json2.put("password", passwordInput);
            json2.put("first_name", fNameInput);
            json2.put("last_name", lNameInput);
            json2.put("userType", "3");
            json2.put("email", emailInput);
            json2.put("is_staff", "1");
            json2.put("skills", jsonSkillArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        outputjson = json2.toString();
        Log.i("OUTJSON", outputjson);

    }

    private boolean validateEmail() {
        emailInput = textInputEmail.getEditText().getText().toString().trim();
        if (emailInput.isEmpty()) {
            textInputEmail.setError("Field can't be empty");
            return false;
        } else {
            textInputEmail.setError(null);
            return true;
        }
    }

    private boolean validateUsername() {
        usernameInput = textInputUsername.getEditText().getText().toString().trim();
        if (usernameInput.isEmpty()) {
            textInputUsername.setError("Field can't be empty");
            return false;
        } else if (usernameInput.length() > 15) {
            textInputUsername.setError("Username too long");
            return false;
        } else {
            textInputUsername.setError(null);
            return true;
        }
    }

    private boolean validatePassword() {
        passwordInput = textInputPassword.getEditText().getText().toString().trim();
        if (passwordInput.isEmpty()) {
            textInputPassword.setError("Field can't be empty");
            return false;
        } else {
            textInputPassword.setError(null);
            return true;
        }
    }

    private boolean validateFname() {
        fNameInput = textInputFName.getEditText().getText().toString().trim();
        if (fNameInput.isEmpty()) {
            textInputFName.setError("Field can't be empty");
            return false;
        } else {
            textInputFName.setError(null);
            return true;
        }
    }

    private boolean validateLname() {
        lNameInput = textInputLName.getEditText().getText().toString().trim();
        if (lNameInput.isEmpty()) {
            textInputLName.setError("Field can't be empty");
            return false;
        } else {
            textInputLName.setError(null);
            return true;
        }
    }

    void fillSkillData() {
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

                                mSkillList.add(new SkillItem(tagName));

                            }

                            Log.i("ARRRRR", skillIdArr.toString());
                            Log.i("ARRRRR", skillNameArr.toString());

                            mskillAdapter = new SkillAdapter(RegVenderScrollingActivity.this, mSkillList);
                            mRecyclerView.setAdapter(mskillAdapter);
                            SkillAdapter.setOnItemClickListener(RegVenderScrollingActivity.this);

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