package com.example.eventz.preferences;

import android.content.Context;
import android.content.SharedPreferences;


public class User {
    private static String Token;
    private static String userName;
    private static String Type;
    private static String userobj;
    private static String userId;
    private static String userEmail;
    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public User() {
        Token = null;

    }

    public String getToken() {
        return Token;
    }

    public void setToken(String token) {
        Token = token;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String uName) {
        userName = uName;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getUserobj() {
        return userobj;
    }

    public void setUserobj(String user_obj) {
        userobj = user_obj;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userid) {
        userId = userid;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String usremail) {
        userEmail = usremail;
    }

    public void store_data(String Token, String userName, String Type, String userId, String email, Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("UserIn", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        editor.putString("KEY_TOKEN", Token);
        editor.putString("USERNAME", userName);
        editor.putString("USERTYPE", Type);
        editor.putString("USER_ID", userId);
        editor.putString("USER_EMAIL", email);
        editor.apply();

    }

    public SharedPreferences retrieveUserData(Context context) {
        sharedPreferences = context.getSharedPreferences("UserIn", Context.MODE_PRIVATE);
        return sharedPreferences;

    }

    public void store_userObj(String obj, Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("UserOBJ", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        editor.putString("USEROBJ", obj);
        editor.apply();

    }

    public SharedPreferences retrieveUserOBJ(Context context) {
        sharedPreferences = context.getSharedPreferences("UserOBJ", Context.MODE_PRIVATE);
        return sharedPreferences;

    }

}
