package com.example.eventz.home.ui.gallery;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.eventz.MainActivity;
import com.example.eventz.R;
import com.example.eventz.preferences.User;
import com.example.eventz.register.RegVenderScrollingActivity;
import com.example.eventz.register.SkillAdapter;
import com.example.eventz.register.SkillItem;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class GalleryFragment extends Fragment implements SkillAdapter.onItemClickListener {

    private GalleryViewModel galleryViewModel;
    View root;

    User user = new User();
    String userOBJ;
    String token;
    String userType;
    String userid;
    String userName;
    String password;
    private EditText textInputEmail;
    private EditText textInputFName;
    private EditText textInputLName;
    private EditText textInputLocation;
    private EditText textInputminBudget;
    private EditText textInputmaxBudget;
    private RecyclerView mRecyclerView;
    private SkillAdapter mskillAdapter;
    private Button btnUpdate;
    private ArrayList<SkillItem> mSkillList;
    private ArrayList<String> skillIdArr;
    private ArrayList<String> skillNameArr;
    private RequestQueue mRequestQueueUpdatevendor;
    //String URL = "http://192.168.1.100:8000/events";

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                ViewModelProviders.of(this).get(GalleryViewModel.class);

        //----------------------------------------------------------------------------------------------------------------------

        SharedPreferences sp1 = user.retrieveUserData(Objects.requireNonNull(getActivity()));
        userType = sp1.getString("USERTYPE", "");
        token = sp1.getString("KEY_TOKEN", "");
        Log.i(" Update-SP", userType + " " + token);

        if (userType.equals("3")) {
            root = inflater.inflate(R.layout.fragment_gallery, container, false);

            textInputEmail = root.findViewById(R.id.text_input_email_edit);
            textInputFName = root.findViewById(R.id.text_input_FirstName_edit);
            textInputLName = root.findViewById(R.id.text_input_LastName_edit);
            textInputLocation = root.findViewById(R.id.text_input_location_edit);
            textInputmaxBudget = root.findViewById(R.id.text_input_max_edit);
            textInputminBudget = root.findViewById(R.id.text_input_min_edit);
            btnUpdate = root.findViewById(R.id.button_vendor_edit);
            mSkillList = new ArrayList<>();
            skillIdArr = new ArrayList<>();
            skillNameArr = new ArrayList<>();
            mRequestQueueUpdatevendor = Volley.newRequestQueue(getActivity());
            mRecyclerView = root.findViewById(R.id.recyclerView_edit);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


            SharedPreferences sp = user.retrieveUserOBJ(Objects.requireNonNull(getActivity()));
            userOBJ = sp.getString("USEROBJ", "");
            Log.i(" Update-User OBJ-SP", userOBJ);

            try {
                JSONObject obj = new JSONObject(userOBJ);
                userid = obj.getString("id");
                userName = obj.getString("username");
                String first_name = obj.getString("first_name");
                String last_name = obj.getString("last_name");
                String email = obj.getString("email");
                String location = obj.getString("location");
                String maxBudget = obj.getString("maxBudget");
                String minBudget = obj.getString("minBudget");

                textInputEmail.setText(email);
                textInputFName.setText(first_name);
                textInputLName.setText(last_name);
                textInputLocation.setText(location);
                textInputmaxBudget.setText(maxBudget);
                textInputminBudget.setText(minBudget);


                JSONArray skillArr = obj.getJSONArray("skills");
                for (int j = 0; j < skillArr.length(); j++) {
                    JSONObject jsonObject = skillArr.getJSONObject(j);
                    Log.i("INSIDEOBJECR", jsonObject.toString());
                    String skillID = String.valueOf(jsonObject.getInt("tagId"));
                    String tagName = jsonObject.getString("tagName");

                    skillIdArr.add(skillID);
                    skillNameArr.add(tagName);

                    mSkillList.add(new SkillItem(tagName));

                }

                Log.i("ARRRRR", skillIdArr.toString());
                Log.i("ARRRRR", skillNameArr.toString());

                mskillAdapter = new SkillAdapter(getActivity(), mSkillList);
                mRecyclerView.setAdapter(mskillAdapter);
                SkillAdapter.setOnItemClickListener(GalleryFragment.this);


                btnUpdate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updateVendor();
                    }
                });


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }


        // ***********this line should be inside of if(Vendor or organizer)
        //root = inflater.inflate(R.layout.fragment_gallery, container, false);
        //******************

        return root;
    }

    private void updateVendor() {
        String editEmail = String.valueOf(textInputEmail.getText());
        String editFname = String.valueOf(textInputFName.getText());
        String editLname = String.valueOf(textInputLName.getText());
        String editLocation = String.valueOf(textInputLocation.getText());
        String editMax = String.valueOf(textInputmaxBudget.getText());
        String editMin = String.valueOf(textInputminBudget.getText());

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
            json2.put("id", userid);
            json2.put("username", userName);
            json2.put("first_name", editFname);
            json2.put("last_name", editLname);
            json2.put("userType", "3");
            json2.put("email", editEmail);
            json2.put("is_staff", "1");
            json2.put("location", editLocation);
            json2.put("minBudget", editMin);
            json2.put("maxBudget", editMax);
            json2.put("skills", jsonSkillArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        outputjson = json2.toString();
        Log.i("OUTJSON", outputjson);

        String end_num = getString(R.string.url_end);
        String URL_REG = "http://192.168.1." + end_num + ":8000/updateVendor";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, URL_REG, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("RESPONS", response.toString());
                        try {
                            String res = response.getString("response");

//                            if (res.equals("Successfully registered")) {
//                                Intent intent = new Intent(RegVenderScrollingActivity.this, MainActivity.class);
//                                startActivity(intent);
//                                finish();
//                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "Registration Fail!, Try Again.", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error.getMessage());
                Toast.makeText(getActivity(), "Check your connectivity", Toast.LENGTH_LONG).show();
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

        };

        int socketTimeout = 500000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);

        mRequestQueueUpdatevendor.add(request);

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
