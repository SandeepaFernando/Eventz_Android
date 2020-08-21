package com.example.eventz.preferences;

import android.content.Context;
import android.content.SharedPreferences;


public class User {
    private static String Token;
    private static String userName;
    //private static String user_Preferences;
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

    public String getUserName(){
        return userName;
    }

    public void setUserName(String uName){
        userName = uName;
    }

    public void store_data(String Token,String userName, Context context) {

        this.context = context;
        sharedPreferences = context.getSharedPreferences("UserIn", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        editor.putString("KEY_TOKEN", Token);
        editor.putString("USERNAME", userName);
        editor.apply();

    }

    public SharedPreferences retrieveUserData(Context context) {
        sharedPreferences = context.getSharedPreferences("UserIn", Context.MODE_PRIVATE);
        return sharedPreferences;

    }

}
