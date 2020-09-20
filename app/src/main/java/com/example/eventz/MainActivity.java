package com.example.eventz;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.eventz.home.HomeActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

import com.example.eventz.preferences.UserSession;
import com.example.eventz.preferences.User;
import com.example.eventz.register.RegisterActivity;

public class MainActivity extends AppCompatActivity {
    private EditText userName, password;
    private Button btn_submit, btn_Reg;
    String uNameSP;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    UserSession session;
    User usersp = new User();
    private String token;
    private RequestQueue requestQueue;
    //String URL = "http://192.168.1.100:8000/login";

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        session = new UserSession(getApplicationContext());
        password = findViewById(R.id.editTextPassword);
        userName = findViewById(R.id.editTextUsername);

        uNameSP = userName.getText().toString();

        btn_submit = findViewById(R.id.button);
        btn_Reg = findViewById(R.id.buttonReg);

        // creating an shared Preference file for the information to be stored
        // first argument is the name of file and second is the mode, 0 is private mode
        sharedPreferences = getApplicationContext().getSharedPreferences("Reg", 0);
        // get editor to edit in file
        editor = sharedPreferences.edit();

        Toast.makeText(getApplicationContext(),
                "User Login Status: " + session.isUserLoggedIn(),
                Toast.LENGTH_SHORT).show();

        if (session.isUserLoggedIn()) {
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();

        } else {

            btn_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String data = "{" +
                            "\"username\"" + ":" + "\"" + userName.getText().toString() + "\"," +
                            "\"password\"" + ":" + "\"" + password.getText().toString() + "\"" +
                            "}";

                    submit(data);

                    Log.i("CLICK", "Click submit");

                }
            });
        }

        btn_Reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

    }

    private void loginVendor() {
        usersp.store_data(usersp.getToken(), usersp.getUserName(), usersp.getType(), usersp.getUserId(), usersp.getUserEmail(), getApplicationContext());
        usersp.store_userObj(usersp.getUserobj(), getApplicationContext());

        String user = userName.getText().toString();
        String pass = password.getText().toString();

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("UName", user);
        editor.putString("Pass", pass);
        editor.apply();

        // Validate if username, password is filled
        if (user.trim().length() > 0 && pass.trim().length() > 0) {
            String uName = null;
            String uPassword = null;

            if (sharedPreferences.contains("UName")) {
                uName = sharedPreferences.getString("UName", "");

            }

            if (sharedPreferences.contains("Pass")) {
                uPassword = sharedPreferences.getString("Pass", "");

            }

            if (user.equals(uName) && pass.equals(uPassword)) {

                session.createUserLoginSession(uName, uPassword);

                // Starting MainActivity
                Intent i = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(i);
                finish();


            } else {

                // username / password doesn't match&
                Toast.makeText(getApplicationContext(),
                        "Username/Password is incorrect",
                        Toast.LENGTH_SHORT).show();

            }
        } else {

            // user didn't entered username or password
            Toast.makeText(getApplicationContext(),
                    "Please enter username and password",
                    Toast.LENGTH_LONG).show();

        }

    }

    private void submit(String data) {
        final String savedata = data;

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        String end_num = getString(R.string.url_end);
        String URL = "http://192.168.1." + end_num + ":8000/login";

        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL, null,
                new Response.Listener<JSONObject>() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.i("RESPONSE", response.toString());

                            token = response.getString("token");

                            if (token != null) {
                                Log.i("TOKEN", token);
                                usersp.setToken(token);


                                JSONObject user = response.getJSONObject("user");
                                usersp.setUserobj(user.toString());

                                String userType = user.getString("userType");
                                Log.i("USERTYPE", userType);
                                usersp.setType(userType);

                                uNameSP = user.getString("username");
                                usersp.setUserName(uNameSP);

                                uNameSP = user.getString("email");
                                usersp.setUserEmail(uNameSP);

                                String userID = user.getString("id");
                                usersp.setUserId(userID);

                                if (userType.equals("3")) {
                                    loginVendor();

                                } else if (userType.equals("2")) {
                                    loginOrganizer();
                                }
                            } else {

                                Toast.makeText(getApplicationContext(), "Username/Password is incorrect", Toast.LENGTH_LONG).show();
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
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public byte[] getBody() {
                return savedata == null ? null : savedata.getBytes(StandardCharsets.UTF_8);
            }

        };

        int socketTimeout = 500000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);

        requestQueue.add(request);

    }

    private void loginOrganizer() {
        usersp.store_data(usersp.getToken(), usersp.getUserName(), usersp.getType(), usersp.getUserId(), usersp.getUserEmail(), getApplicationContext());
        usersp.store_userObj(usersp.getUserobj(), getApplicationContext());

        String user = userName.getText().toString();
        String pass = password.getText().toString();

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("UName", user);
        editor.putString("Pass", pass);
        editor.apply();

        // Validate if username, password is filled
        if (user.trim().length() > 0 && pass.trim().length() > 0) {
            String uName = null;
            String uPassword = null;

            if (sharedPreferences.contains("UName")) {
                uName = sharedPreferences.getString("UName", "");

            }

            if (sharedPreferences.contains("Pass")) {
                uPassword = sharedPreferences.getString("Pass", "");

            }

            if (user.equals(uName) && pass.equals(uPassword)) {

                session.createUserLoginSession(uName, uPassword);

                // Starting MainActivity
                Intent i = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(i);
                finish();


            } else {

                // username / password doesn't match&
                Toast.makeText(getApplicationContext(),
                        "Username/Password is incorrect",
                        Toast.LENGTH_SHORT).show();

            }
        } else {

            // user didn't entered username or password
            Toast.makeText(getApplicationContext(),
                    "Please enter username and password",
                    Toast.LENGTH_LONG).show();

        }
    }
}
