package com.example.eventz.home.ui.gallery;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.toolbox.Volley;
import com.example.eventz.R;
import com.example.eventz.preferences.User;

import java.util.ArrayList;
import java.util.Objects;

public class GalleryFragment extends Fragment {

    private GalleryViewModel galleryViewModel;
    View root;

    User user = new User();
    String userOBJ;
    String token;
    String userType;
    String URL = "http://192.168.1.100:8000/events";

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                ViewModelProviders.of(this).get(GalleryViewModel.class);


        // ***********this line should be inside of if(Vendor or organizer)
        root = inflater.inflate(R.layout.fragment_gallery, container, false);
        //******************


        //----------------------------------------------------------------------------------------------------------------------


        SharedPreferences sp = user.retrieveUserOBJ(Objects.requireNonNull(getActivity()));
        userOBJ = sp.getString("USEROBJ", "");
        Log.i(" Update-User OBJ-SP", userOBJ);

        SharedPreferences sp1 = user.retrieveUserData(Objects.requireNonNull(getActivity()));
        userType = sp1.getString("USERTYPE", "");
        token = sp1.getString("KEY_TOKEN", "");
        Log.i(" Update-SP", userType + " " + token);

//        if (userType.equals("3")) {
//
//            root = inflater.inflate(R.layout.fragment_home, container, false);
//            //textView = root.findViewById(R.id.text_home);
//            eventIdArr = new ArrayList<>();
//            mevnetList = new ArrayList<>();
//            mRecyclerView = root.findViewById(R.id.recycler_view_vendor_home);
//            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//            requestQueue = Volley.newRequestQueue(Objects.requireNonNull(getActivity()));
//
//            getAllEvents();
//
//        }


//        final TextView textView = root.findViewById(R.id.text_gallery);
//        galleryViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });


        return root;
    }
}
