package com.example.eventz.eventInfo;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.eventz.register.RegVenderScrollingActivity;
import com.example.eventz.register.SkillAdapter;
import com.example.eventz.register.SkillItem;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.eventz.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Event_infoActivity extends AppCompatActivity {
    String eventId;
    String URL = "http://192.168.1.103:8000/events";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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



    }


//    void getEventbyid() {
//        final Intent intent = getIntent();
//        eventId = intent.getStringExtra("eventId");
//        Log.i("EXTRASITE_ID", eventId);
//
//        final JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, URL, null,
//                new Response.Listener<JSONArray>() {
//                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
//                    @Override
//                    public void onResponse(JSONArray response) {
//                        try {
//
//                            Log.i("RESPONSE", response.toString());
//
//                            for (int j = 0; j < response.length(); j++) {
//                                JSONObject jsonObject = response.getJSONObject(j);
//                                Log.i("INSIDEOBJECR", jsonObject.toString());
//                                String skillID = String.valueOf(jsonObject.getInt("id"));
//                                String tagName = jsonObject.getString("tagName");
//
//                                skillIdArr.add(skillID);
//                                skillNameArr.add(tagName);
//
//                                mSkillList.add(new SkillItem(tagName));
//
//                            }
//
//                            Log.i("ARRRRR", skillIdArr.toString());
//                            Log.i("ARRRRR", skillNameArr.toString());
//
//                            mskillAdapter = new SkillAdapter(RegVenderScrollingActivity.this, mSkillList);
//                            mRecyclerView.setAdapter(mskillAdapter);
//                            SkillAdapter.setOnItemClickListener(RegVenderScrollingActivity.this);
//
//                        } catch (JSONException e) {
//                            Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_LONG).show();
//
//                        }
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                if (error.networkResponse == null) {
//                    if (error.getClass().equals(TimeoutError.class)) {
//                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
//                        Log.v("VOLLEY", error.toString());
//                    }
//                }
//
//            }
//        }) {
//
//        };
//
//        int socketTimeout = 500000;//30 seconds - change to what you want
//        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
//        request.setRetryPolicy(policy);
//
//        mRequestQueue.add(request);
//    }
}