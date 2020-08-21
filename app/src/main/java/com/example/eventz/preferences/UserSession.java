package com.example.eventz.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class UserSession {
    // Shared preferences file name
    public static final String PREFER_NAME = "Reg";
    // All Shared Preferences Keys
    public static final String IS_USER_LOGIN = "IsUserLoggedIn";
    // User name (make variable public to access from outside)
    public static final String KEY_NAME = "UName";
    // password
    public static final String KEY_PASSWORD = "Pass";
    // Shared Preferences reference
    SharedPreferences pref;
    // Editor reference for Shared preferences
    Editor editor;
    // Context
    Context _context;

    // Email address (make variable public to access from outside)
    //public static final String KEY_EMAIL = "Email";
    // Shared preferences mode
    int PRIVATE_MODE = 0;

    // Constructor
    public UserSession(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREFER_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    //Create login session
    public void createUserLoginSession(String uName, String uPassword) {
        // Storing login value as TRUE
        editor.putBoolean(IS_USER_LOGIN, true);

        // Storing name in preferences
        editor.putString(KEY_NAME, uName);

        // Storing email in preferences
        editor.putString(KEY_PASSWORD, uPassword);

        // commit changes
        editor.apply();
    }

    // Check for login
    public boolean isUserLoggedIn() {
        return pref.getBoolean(IS_USER_LOGIN, false);
    }
}
