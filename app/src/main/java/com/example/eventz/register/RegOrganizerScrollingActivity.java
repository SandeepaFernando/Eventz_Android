package com.example.eventz.register;

import android.content.Intent;
import android.os.Bundle;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.eventz.MainActivity;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.eventz.R;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;

public class RegOrganizerScrollingActivity extends AppCompatActivity {
    private TextInputLayout textInputEmail;
    private TextInputLayout textInputUsername;
    private TextInputLayout textInputPassword;
    private TextInputLayout textInputFName;
    private TextInputLayout textInputLName;
    private TextInputLayout textInputLocation;
    private Button btnRegisterOraganizer;
    private String emailInput;
    private String usernameInput;
    private String passwordInput;
    private String fNameInput;
    private String lNameInput;
    private String locationInput;
    private RequestQueue mRequestQueueReg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_organizer_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        toolBarLayout.setTitle(getTitle());

        textInputEmail = findViewById(R.id.text_input_email_organizer);
        textInputUsername = findViewById(R.id.text_input_username_organizer);
        textInputPassword = findViewById(R.id.text_input_password_organizer);
        textInputFName = findViewById(R.id.text_input_FirstName_organizer);
        textInputLName = findViewById(R.id.text_input_LastName_organizer);
        textInputLocation = findViewById(R.id.text_input_location_organizer);
        btnRegisterOraganizer = findViewById(R.id.button_register_organizer);
        mRequestQueueReg = Volley.newRequestQueue(this);

        btnRegisterOraganizer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateEmail() | !validateUsername() | !validatePassword() | !validateFname() | !validateLname() | !validateLocation()) {
                    return;
                }
                registerorganizer(emailInput, usernameInput, passwordInput, fNameInput, lNameInput, locationInput);

            }
        });

    }

    private void registerorganizer(String emailInput, String usernameInput, String passwordInput, String fNameInput, String lNameInput, String locationInput) {
        JSONObject jsonUserOBJ = new JSONObject();
        final String outputjson;
        try {
            jsonUserOBJ.put("username", usernameInput);
            jsonUserOBJ.put("password", passwordInput);
            jsonUserOBJ.put("first_name", fNameInput);
            jsonUserOBJ.put("last_name", lNameInput);
            jsonUserOBJ.put("userType", "2");
            jsonUserOBJ.put("email", emailInput);
            jsonUserOBJ.put("is_staff", "1");
            jsonUserOBJ.put("location", locationInput);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        outputjson = jsonUserOBJ.toString();
        Log.i("OUTJSON", outputjson);

        //String res;
        String end_num = getString(R.string.url_end);
        String URL_REG = "http://192.168.1." + end_num + ":8000/register";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL_REG, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("RESPONS", response.toString());
                        try {
                            String res = response.getString("response");

                            if (res.equals("Successfully registered")) {
                                Intent intent = new Intent(RegOrganizerScrollingActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();

                                Toast.makeText(RegOrganizerScrollingActivity.this, "Registration Successful, Please Login now.", Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(RegOrganizerScrollingActivity.this, "Registration Fail!, Try Again.", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error.getMessage());
                Toast.makeText(RegOrganizerScrollingActivity.this, "Check your connectivity", Toast.LENGTH_LONG).show();
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

        };

        int socketTimeout = 500000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);

        mRequestQueueReg.add(request);
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

    private boolean validateLocation() {
        locationInput = textInputLocation.getEditText().getText().toString().trim();
        if (locationInput.isEmpty()) {
            textInputLocation.setError("Field can't be empty");
            return false;
        } else {
            textInputLocation.setError(null);
            return true;
        }
    }
}