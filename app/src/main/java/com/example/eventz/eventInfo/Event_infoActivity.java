package com.example.eventz.eventInfo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

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
import com.example.eventz.home.HomeActivity;
import com.example.eventz.home.VendorAdapter;
import com.example.eventz.preferences.User;
import com.example.eventz.register.RegOrganizerScrollingActivity;
import com.example.eventz.register.RegVenderScrollingActivity;
import com.example.eventz.register.SkillAdapter;
import com.example.eventz.register.SkillItem;
import com.example.eventz.updateEvent.UpdateEvents;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eventz.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.iid.MessengerIpcClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Event_infoActivity extends AppCompatActivity implements CommentAdapter.onItemClickListener {
    User user = new User();
    String eventId;
    String userId;
    private ArrayList<EventSkillInfo> mSkillList;
    private ArrayList<CommentInfo> mCommentList;
    private ArrayList<String> commentIdList;
    private RecyclerView mRecyclerView;
    private RecyclerView mRecyclerViewComment;
    private EventSkillAdapter mskillAdapter;
    private CommentAdapter mcommentAdapter;
    private RequestQueue mRequestQueueInfo;
    private RequestQueue mRequestQueueComment;
    TextView titleTV, descriptionTV, venueTV, dateTV, num_peopleTV, f_nameTV, emailTV, comment_number;
    String title, description, eventDate, venue, noOfGuests, fname, email;
    String token;
    String userType;
    //Button edit_eventBTN;
    Button post_commentBTN;
    private EditText input_commentTv;
    String outputjsonComment;
    private int mStatusCode = 0;
    String commentTxt;
    String commentNumber;
    String userNameSP;
    String userNameComment;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        CollapsingToolbarLayout toolBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        toolBarLayout.setTitle(getTitle());

        ImageButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //-----------------------------------------------------------------------------
        titleTV = findViewById(R.id.txtview_title);
        descriptionTV = findViewById(R.id.txtview_description);
        venueTV = findViewById(R.id.txtview_venue);
        dateTV = findViewById(R.id.txtview_date);
        num_peopleTV = findViewById(R.id.txtview_numofpeople);
        f_nameTV = findViewById(R.id.txtview_fname);
        emailTV = findViewById(R.id.txtview_email);
        //edit_eventBTN = findViewById(R.id.button_edit_event);
        input_commentTv = findViewById(R.id.text_input_comment);
        post_commentBTN = findViewById(R.id.post_comment_btn);
        comment_number = findViewById(R.id.comment_number);

        SharedPreferences sp = user.retrieveUserData(Objects.requireNonNull(getApplicationContext()));
        userType = sp.getString("USERTYPE", "");
        token = sp.getString("KEY_TOKEN", "");
        userNameSP = sp.getString("USERNAME", "");
        commentIdList = new ArrayList<>();

        mSkillList = new ArrayList<>();
        mRecyclerView = findViewById(R.id.event_skill_set);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mCommentList = new ArrayList<>();
        mRecyclerViewComment = findViewById(R.id.recyclerView_user_comments);
        mRecyclerViewComment.setLayoutManager(new LinearLayoutManager(this));

        getEventbyid();

    }

    void getEventbyid() {
        if (!mSkillList.isEmpty() | !mCommentList.isEmpty()) {
            mSkillList.clear();
            mCommentList.clear();
        }
        mRequestQueueInfo = Volley.newRequestQueue(this);
        final Intent intent = getIntent();
        eventId = intent.getStringExtra("eventId");
        userId = intent.getStringExtra("userId");
        Log.i("EXTRASITE_ID", eventId);

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
                                title = jsonObject.getString("title");
                                description = jsonObject.getString("description");
                                eventDate = jsonObject.getString("eventDate");
                                venue = jsonObject.getString("venue");
                                noOfGuests = jsonObject.getString("noOfGuests");

                                JSONArray tags = jsonObject.getJSONArray("eventTags");
                                Log.i("TAGARR", tags.toString());

                                for (int i = 0; i < tags.length(); i++) {
                                    JSONObject tagObject = tags.getJSONObject(i);
                                    String tagName = tagObject.getString("tagName");

                                    mSkillList.add(new EventSkillInfo(tagName));
                                }

                                JSONObject organizerobj = jsonObject.getJSONObject("organizer");
                                fname = organizerobj.getString("first_name");
                                email = organizerobj.getString("email");

                                //-----------------comment-----------------

                                JSONArray eventReviews = jsonObject.getJSONArray("eventReviews");
                                commentNumber = String.valueOf(eventReviews.length());
                                for (int k = 0; k < eventReviews.length(); k++) {
                                    JSONObject commentObject = eventReviews.getJSONObject(k);
                                    String commentStr = commentObject.getString("comment");
                                    String commentDate = commentObject.getString("commentedOn");

                                    JSONObject commentedByOBJ = commentObject.getJSONObject("commentedBy");
                                    String commentName = commentedByOBJ.getString("first_name");
                                    userNameComment = commentedByOBJ.getString("username");

                                    mCommentList.add(new CommentInfo(commentName, commentDate, commentStr, userNameSP, userNameComment));
                                    Log.i("XXXXXXXXXXXXXXXXX", userNameSP + "===" + userNameComment);

                                }

                            }

                            mskillAdapter = new EventSkillAdapter(Event_infoActivity.this, mSkillList);
                            mRecyclerView.setAdapter(mskillAdapter);

                            mcommentAdapter = new CommentAdapter(Event_infoActivity.this, mCommentList);
                            mRecyclerViewComment.setAdapter(mcommentAdapter);
                            CommentAdapter.setOnItemClickListener(Event_infoActivity.this);


                            titleTV.setText(title);
                            descriptionTV.setText(description);
                            venueTV.setText(venue);
                            dateTV.setText(eventDate);
                            num_peopleTV.setText(noOfGuests);
                            f_nameTV.setText(fname);
                            emailTV.setText(email);
                            comment_number.setText("(" + commentNumber + ")");
                            Log.i("UTYPE", userType);

                            //======================== If Organizer can Edit(2)======================
//                            if (userType.equals("2")) {
//                                Log.i("UTYPE", userType);
//                                edit_eventBTN.setVisibility(View.VISIBLE);
//                                edit_eventBTN.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        Log.i("CILCK ", " Click on edit Event");
//                                        edit_event(eventId);
//                                    }
//                                });
//                            }

                            postComment(eventId, userId);

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

        mRequestQueueInfo.add(request);
    }

    private void postComment(final String eventId, final String userId) {
        post_commentBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                commentTxt = input_commentTv.getText().toString();
                Log.i("Commnet TXT - ", commentTxt);

                if (!commentTxt.isEmpty()) {
                    Toast.makeText(Event_infoActivity.this, "Posting Comment...", Toast.LENGTH_SHORT).show();

                    outputjsonComment = "{" +
                            "\"eventId\"" + ":" + "\"" + eventId + "\"," +
                            "\"userId\"" + ":" + "\"" + userId + "\"," +
                            "\"comment\"" + ":" + "\"" + commentTxt + "\"" +
                            "}";
                    Log.i("Commnet Json - ", outputjsonComment);

                    mRequestQueueComment = Volley.newRequestQueue(getApplicationContext());
                    String url = getString(R.string.ip);
                    String URL = url + "eventComment";
                    JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL, null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Log.i("RESPONS", response.toString());
                                    try {
                                        String id = response.getString("id");
                                        Log.i("RES- Comment- ", id);
                                        input_commentTv.getText().clear();

                                        if (mStatusCode == 201) {
                                            Toast.makeText(Event_infoActivity.this, "Posted.", Toast.LENGTH_LONG).show();
                                            getEventbyid();
                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        Toast.makeText(getApplicationContext(), "Post Comment Fail!, Try Again.", Toast.LENGTH_LONG).show();
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            System.out.println(error.getMessage());
                            Toast.makeText(getApplicationContext(), "Check your connectivity", Toast.LENGTH_LONG).show();
                        }
                    }) {
                        @Override
                        public String getBodyContentType() {
                            return "application/json; charset=utf-8";
                        }

                        @Override
                        public byte[] getBody() {
                            return outputjsonComment == null ? null : outputjsonComment.getBytes(Charset.forName("UTF-8"));
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

                    mRequestQueueComment.add(request);

                } else
                    Toast.makeText(getApplicationContext(), "Nothing To Post!", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void edit_event(String eventId) {
        Intent intent = new Intent(Event_infoActivity.this, UpdateEvents.class);
        intent.putExtra("EVENTID", eventId);
        startActivity(intent);
    }

    @Override
    public void onItemClick(int position) {
        //This is Delete
        Log.i("CLICK ", "Click Delete" + position);
    }

    @Override
    public void onItemClickEdit(int position) {
        //This is Edit
        Log.i("CLICK ", "Click Edit" + position);
    }
}