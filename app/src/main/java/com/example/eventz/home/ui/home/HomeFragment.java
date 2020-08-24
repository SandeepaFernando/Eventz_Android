package com.example.eventz.home.ui.home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import com.example.eventz.eventInfo.Event_infoActivity;
import com.example.eventz.home.VendorAdapter;
import com.example.eventz.home.VendorInfo;
import com.example.eventz.preferences.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HomeFragment extends Fragment implements VendorAdapter.onItemClickListener {

    private HomeViewModel homeViewModel;
    User user = new User();
    TextView textView;
    View root;
    private RequestQueue requestQueue;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private VendorAdapter mvendorAdapter;
    private ArrayList<VendorInfo> mevnetList;
    private ArrayList<String> eventIdArr;
    String token;
    String userType;
    String URL = "http://192.168.1.103:8000/events";

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        //----------------------------------------------------------------------------------------------------------------------


        SharedPreferences sp = user.retrieveUserData(Objects.requireNonNull(getActivity()));
        userType = sp.getString("USERTYPE", "");
        token = sp.getString("KEY_TOKEN", "");

        Log.i("HOME-SP", userType + " " + token);

        if (userType.equals("3")) {
            root = inflater.inflate(R.layout.fragment_home, container, false);
            //textView = root.findViewById(R.id.text_home);
            eventIdArr = new ArrayList<>();
            mevnetList = new ArrayList<>();
            mRecyclerView = root.findViewById(R.id.recycler_view_vendor_home);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            requestQueue = Volley.newRequestQueue(Objects.requireNonNull(getActivity()));

            getAllEvents();

        }

//        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });

        swipeRefreshLayout = root.findViewById(R.id.swiperefresh);
        pullDownFunc();

        return root;
    }

    public void pullDownFunc() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                Log.i("onRefresh", "XXXXXXXXXXXXXXX-onRefresh");

                new Handler().postDelayed(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void run() {
                        //parseData(filterIdArr);
                        swipeRefreshLayout.setRefreshing(false);
                        Log.i("run", "XXXXXXXXXXXXXXX-run");
                        getAllEvents();

                    }

                }, 1);

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void getAllEvents() {
        if (!mevnetList.isEmpty()){
            mevnetList.clear();
        }
        if (!eventIdArr.isEmpty()){
            eventIdArr.clear();
        }

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
                                String id = String.valueOf(jsonObject.getInt("id"));
                                String title = jsonObject.getString("title");
                                String venue = jsonObject.getString("venue");
                                String date = jsonObject.getString("eventDate");
                                String noOfGuests = jsonObject.getString("noOfGuests");

                                eventIdArr.add(id);
                                mevnetList.add(new VendorInfo(title,venue,date,noOfGuests));
                            }

                            mvendorAdapter = new VendorAdapter(getActivity(), mevnetList);
                            mRecyclerView.setAdapter(mvendorAdapter);
                            VendorAdapter.setOnItemClickListener(HomeFragment.this);


                        } catch (JSONException e) {
                            Toast.makeText(getContext(), "error", Toast.LENGTH_LONG).show();

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.v("VOLLEY", error.toString());
                    }
                }

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Authorization", "Token " +token);

                return params;
            }
        };

        int socketTimeout = 500000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);

        requestQueue.add(request);
    }

    @Override
    public void onItemClick(int position) {
        Log.i("CLICK ID" , eventIdArr.get(position));
        Intent intent = new Intent(getActivity(), Event_infoActivity.class);
        intent.putExtra("eventId", eventIdArr.get(position));
        startActivity(intent);

    }
}
