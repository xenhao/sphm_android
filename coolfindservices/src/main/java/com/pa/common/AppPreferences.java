package com.pa.common;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class AppPreferences implements Config {
    //public static final String KEY_PREFS_SMS_BODY = "sms_body";
    private static String APP_SHARED_PREFS =APP_PREFERENCES ;//AppPreferences.class.getSimpleName(); //  Name of the file -.xml
    private SharedPreferences _sharedPrefs;
    private Editor _prefsEditor;

    public AppPreferences(Context context) {
        // Shared Preference by using package name to have multiple distribution within a phone
        APP_SHARED_PREFS = context.getPackageName();
        this._sharedPrefs = context.getSharedPreferences(APP_SHARED_PREFS, Activity.MODE_PRIVATE);
        this._prefsEditor = _sharedPrefs.edit();
    }

    public String getPref(String key) {
        return _sharedPrefs.getString(key, "");
    }

    public void savePref(String key,String text) {
        _prefsEditor.putString(key, text);
        _prefsEditor.commit();
    }
    
    public String getIntPref(String key) {
        return _sharedPrefs.getString(key, "0");
    }
    
    
}
