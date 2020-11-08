package com.example.eventz.eventInfo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.eventz.preferences.User;
import com.example.eventz.updateEvent.UpdateEvents;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eventz.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Event_infoActivity extends AppCompatActivity implements CommentAdapter.onItemClickListener, BidAdapter.onItemClickListener {
    User user = new User();
    String eventId;
    String userId;
    private ArrayList<EventSkillInfo> mSkillList;
    private ArrayList<CommentInfo> mCommentList;
    private ArrayList<BidInfo> mBidtList;
    private ArrayList<String> commentIdList;
    private ArrayList<String> bidIdList;
    private RecyclerView mRecyclerView;
    private RecyclerView mRecyclerViewComment;
    private RecyclerView mRecyclerViewBid;
    private EventSkillAdapter mskillAdapter;
    private BidAdapter mBidAdapter;
    private CommentAdapter mcommentAdapter;
    private RequestQueue mRequestQueueInfo;
    private RequestQueue mRequestQueueAccBid;
    private RequestQueue mRequestQueueComment;
    private RequestQueue mRequestQueueBid;
    private RequestQueue mRequestQueueCommentDelete;
    TextView titleTV, descriptionTV, venueTV, dateTV, num_peopleTV, f_nameTV, emailTV, comment_number, budgetTV, accVendorNameTv, accVendorEmailTv;
    String title, description, eventDate, venue, noOfGuests, fname, email, budget, accVendorfName, accVendorEmail;
    CardView accVendordCardView;
    String token;
    String userType;
    //Button edit_eventBTN;
    Button post_commentBTN;
    private EditText input_commentTv;
    String outputjsonComment;
    private int mStatusCode = 0;
    String commentTxt;
    String outputjsonBid;
    String commentNumber;
    String userNameSP;
    String userNameComment;
    ImageButton fabBid;

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

        //-----------------------------------------------------------------------------
        fabBid = findViewById(R.id.fab_bid);
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
        budgetTV = findViewById(R.id.txtview_money);
        accVendorNameTv = findViewById(R.id.txtview_name_accepted_vendor);
        accVendorEmailTv = findViewById(R.id.txtview_email_accepted_vendor);
        accVendordCardView = findViewById(R.id.card_acc_vendor);

        SharedPreferences sp = user.retrieveUserData(Objects.requireNonNull(getApplicationContext()));
        userType = sp.getString("USERTYPE", "");
        token = sp.getString("KEY_TOKEN", "");
        userNameSP = sp.getString("USERNAME", "");

        if (userType.equals("2")) {
            fabBid.setVisibility(View.GONE);
        }

        commentIdList = new ArrayList<>();
        bidIdList = new ArrayList<>();

        mSkillList = new ArrayList<>();
        mRecyclerView = findViewById(R.id.event_skill_set);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mCommentList = new ArrayList<>();
        mRecyclerViewComment = findViewById(R.id.recyclerView_user_comments);
        mRecyclerViewComment.setLayoutManager(new LinearLayoutManager(this));

        mBidtList = new ArrayList<>();
        mRecyclerViewBid = findViewById(R.id.recyclerView_event_bids);
        mRecyclerViewBid.setLayoutManager(new LinearLayoutManager(this));

        getEventbyid();

    }

    private void bidDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(Event_infoActivity.this);
        builder.setCancelable(false);
        builder.setTitle("Confirm");
        builder.setMessage("Are you sure You want to Bid for this Event !");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Context context = getApplicationContext();
                CharSequence text = "Bid Successfully.";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                postBid(eventId, userId);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.create().show();
    }

    private void postBid(String eventId, String userId) {
        String bidAmount = "1000";
        outputjsonBid = "{" +
                "\"eventId\"" + ":" + "\"" + eventId + "\"," +
                "\"bidder\"" + ":" + "\"" + userId + "\"," +
                "\"bidAmount\"" + ":" + "\"" + bidAmount + "\"" +
                "}";
        Log.i("Commnet Json - ", outputjsonBid);

        mRequestQueueBid = Volley.newRequestQueue(getApplicationContext());
        String url = getString(R.string.ip);
        String URL = url + "bidForEvent";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("RESPONS", response.toString());
                        try {
                            String id = response.getString("id");
                            Log.i("RES- Comment- ", id);
                            if (mStatusCode == 201) {
                                Toast.makeText(Event_infoActivity.this, "Bid Added !.", Toast.LENGTH_LONG).show();
                                getEventbyid();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Post bid Fail!, Try Again.", Toast.LENGTH_LONG).show();
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
                return outputjsonBid == null ? null : outputjsonBid.getBytes(Charset.forName("UTF-8"));
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

        mRequestQueueBid.add(request);

    }

    void getEventbyid() {
        if (!mSkillList.isEmpty() | !mCommentList.isEmpty()) {
            mSkillList.clear();
            mCommentList.clear();
            commentIdList.clear();
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
                                budget = jsonObject.getString("eventBudget");

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

                                //=====================================================

                                if (!jsonObject.isNull("acceptedVendor")) {

                                    JSONObject accVendorobj = jsonObject.getJSONObject("acceptedVendor");

                                    accVendordCardView.setVisibility(View.VISIBLE);
                                    accVendorfName = accVendorobj.getString("first_name");
                                    accVendorEmail = accVendorobj.getString("email");

                                }

                                //-----------------comment-----------------

                                JSONArray eventReviews = jsonObject.getJSONArray("eventReviews");
                                commentNumber = String.valueOf(eventReviews.length());
                                for (int k = 0; k < eventReviews.length(); k++) {
                                    JSONObject commentObject = eventReviews.getJSONObject(k);
                                    String id = commentObject.getString("id");
                                    String commentStr = commentObject.getString("comment");
                                    String commentDate = commentObject.getString("commentedOn");

                                    JSONObject commentedByOBJ = commentObject.getJSONObject("commentedBy");
                                    String commentName = commentedByOBJ.getString("first_name");
                                    userNameComment = commentedByOBJ.getString("username");

                                    commentIdList.add(id);
                                    mCommentList.add(new CommentInfo(commentName, commentDate, commentStr, userNameSP, userNameComment));
                                    Log.i("XXXXXXXXXXXXXXXXX", userNameSP + "===" + userNameComment);

                                }

                                //-------------------Bids-------------------------

                                JSONArray eventBids = jsonObject.getJSONArray("eventBids");
                                for (int k = 0; k < eventBids.length(); k++) {
                                    JSONObject bidObject = eventBids.getJSONObject(k);
                                    String bidId = bidObject.getString("id");
                                    bidIdList.add(bidId);

                                    JSONObject userOBJ = bidObject.getJSONObject("bidder");
                                    String uname = userOBJ.getString("first_name");
                                    String uemail = userOBJ.getString("email");

                                    mBidtList.add(new BidInfo(uname, uemail));

                                }

                            }

                            mskillAdapter = new EventSkillAdapter(Event_infoActivity.this, mSkillList);
                            mRecyclerView.setAdapter(mskillAdapter);

                            mcommentAdapter = new CommentAdapter(Event_infoActivity.this, mCommentList);
                            mRecyclerViewComment.setAdapter(mcommentAdapter);
                            CommentAdapter.setOnItemClickListener(Event_infoActivity.this);

                            mBidAdapter = new BidAdapter(Event_infoActivity.this, mBidtList);
                            mRecyclerViewBid.setAdapter(mBidAdapter);
                            BidAdapter.setOnItemClickListener(Event_infoActivity.this);


                            titleTV.setText(title);
                            descriptionTV.setText(description);
                            venueTV.setText(venue);
                            dateTV.setText(eventDate);
                            num_peopleTV.setText(noOfGuests);
                            f_nameTV.setText(fname);
                            emailTV.setText(email);
                            comment_number.setText("(" + commentNumber + ")");
                            budgetTV.setText(budget);
                            accVendorNameTv.setText(accVendorfName);
                            accVendorEmailTv.setText(accVendorEmail);
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

                            fabBid.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    bidDialog();

                                }
                            });


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
        String commetId = commentIdList.get(position);
        Log.i("CLICK- ID Comment ", "Click Delete" + commetId);

        mRequestQueueCommentDelete = Volley.newRequestQueue(getApplicationContext());
        String url = getString(R.string.ip);
        String URL = url + "eventComment/" + commetId + "/";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE, URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("RESPONS", response.toString());
                        try {
                            String responseSt = response.getString("response");
                            Log.i("RES- CommentDELETE- ", responseSt);

                            if (mStatusCode == 201) {
                                Toast.makeText(Event_infoActivity.this, "Deleted.", Toast.LENGTH_LONG).show();
                                getEventbyid();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Delete Comment Fail!, Try Again.", Toast.LENGTH_LONG).show();
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

        mRequestQueueCommentDelete.add(request);

    }

    @Override
    public void onItemClickEdit(int position) {
        //This is Edit
        Log.i("CLICK ", "Click Edit" + position);
    }

    @Override
    public void onItemVendorClick(int position) {
        Log.i("CLICK ", "Click onItemVendorClick" + position);
        String bidId = bidIdList.get(position);
        acceptBidDialog(bidId, eventId);
    }

    private void acceptBidDialog(final String bidId, final String eventId) {
        Log.i("CLICK ", eventId + bidId + "++++++");
        final AlertDialog.Builder builder = new AlertDialog.Builder(Event_infoActivity.this);
        builder.setCancelable(false);
        builder.setTitle("Confirm");
        builder.setMessage("Are you sure You want to Accept this Vendor !");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Context context = getApplicationContext();
                acceptBidder(eventId, bidId);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.create().show();
    }

    private void acceptBidder(String eventId, final String bidId) {
        Log.i("CLICK ", eventId + bidId + "+++++=====");
        mRequestQueueAccBid = Volley.newRequestQueue(getApplicationContext());
        String url = getString(R.string.ip);
        String URL = url + "acceptBidder";
        StringRequest request = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Log.i("RES", response);
                            String result = jsonObject.getString("success");

                            if (result.equals("true")) {
                                Toast.makeText(Event_infoActivity.this, "Accepted.", Toast.LENGTH_LONG).show();
                            } else
                                Toast.makeText(Event_infoActivity.this, "NOT Accepted!!", Toast.LENGTH_LONG).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
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
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("bidId", bidId);
                Log.i("PARAMS- ", params.toString());
                return params;
            }

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
        mRequestQueueAccBid.add(request);

    }
}