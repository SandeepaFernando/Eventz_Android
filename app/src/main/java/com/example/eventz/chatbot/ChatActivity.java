package com.example.eventz.chatbot;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.eventz.R;
import com.example.eventz.home.HomeActivity;
import com.example.eventz.preferences.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ChatActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    EditText messageET;
    ImageView sendBtn, backBtn;
    private RequestQueue requestQueue;
    List<MessageChatModel> messageChatModelList = new ArrayList<>();
    MessageChatAdapter adapter;
    String token;
    User user = new User();

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Objects.requireNonNull(getSupportActionBar()).hide();

        SharedPreferences sp = user.retrieveUserData(Objects.requireNonNull(getApplication()));
        token = sp.getString("KEY_TOKEN", "");

        recyclerView = findViewById(R.id.recycler_view_chat);
        LinearLayoutManager manager = new LinearLayoutManager(ChatActivity.this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        requestQueue = Volley.newRequestQueue(Objects.requireNonNull(getApplicationContext()));

        sendBtn = findViewById(R.id.sendBtn);
        messageET = findViewById(R.id.message);
        backBtn = findViewById(R.id.back_img);

        recyclerView.smoothScrollToPosition(messageChatModelList.size());
        adapter = new MessageChatAdapter(messageChatModelList, ChatActivity.this);
        recyclerView.setAdapter(adapter);


        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageTxt = messageET.getText().toString();
                if (!messageTxt.isEmpty()) {
                    @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("hh.mm aa");
                    final String dateString = dateFormat.format(new Date()).toString();
                    System.out.println("Current time in AM/PM: " + dateString);

                    chat(messageTxt);
                    MessageChatModel model = new MessageChatModel(messageTxt, dateString, 0);

                    messageChatModelList.add(model);
                    recyclerView.smoothScrollToPosition(messageChatModelList.size());
                    adapter.notifyDataSetChanged();
                    messageET.setText("");
                }

            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void chat(String text) {
        String url = getString(R.string.ip);
        String URL = url + "chat?text=" + text;
        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL, null,
                new Response.Listener<JSONObject>() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("hh.mm aa");
                            final String dateString = dateFormat.format(new Date()).toString();
                            System.out.println("Current time in AM/PM: " + dateString);

                            Log.i("RESPONSE", response.toString());

                            String chatrespones = response.getString("response");

                            MessageChatModel model = new MessageChatModel(chatrespones, dateString, 1);

                            messageChatModelList.add(model);
                            recyclerView.smoothScrollToPosition(messageChatModelList.size());
                            adapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            Toast.makeText(ChatActivity.this, "error", Toast.LENGTH_LONG).show();

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

        requestQueue.add(request);
    }

}
