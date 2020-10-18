package com.example.eventz.notificationService;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.eventz.preferences.User;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    String TAG = "FCM_PAYLOAD-";
    User user = new User();
    String userType, userName, userEmail, userId, token;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
//        SharedPreferences sp = user.retrieveUserData(getApplicationContext());
//        userId = sp.getString("USER_ID", "User Email");
//        token = sp.getString("KEY_TOKEN", "");


        Log.i("NOTIFICATION", String.valueOf(remoteMessage.getData()));
//        Log.d(TAG, "From: " + remoteMessage.getFrom());
//        Map<String, String> payload = remoteMessage.getData();
//        Log.d(TAG, "Payload" + payload);
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);

    }
}
