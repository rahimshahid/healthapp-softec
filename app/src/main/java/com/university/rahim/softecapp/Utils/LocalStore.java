package com.university.rahim.softecapp.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by RAHIM on 3/10/2018.
 */

public class LocalStore {

    public static int getUserWeight(Context c) {
        SharedPreferences Prefs = PreferenceManager.getDefaultSharedPreferences(c);
        return Prefs.getInt("weight",0);
    }

    public static void setUserWeight(Context c, int weight) {
        SharedPreferences Prefs = PreferenceManager.getDefaultSharedPreferences(c);
        Prefs.edit().putInt("weight", weight).apply();
    }

    public static int getUserHeight(Context c) {
        SharedPreferences Prefs = PreferenceManager.getDefaultSharedPreferences(c);
        return Prefs.getInt("height",0);
    }

    public static void setUserHeight(Context c, int height) {
        SharedPreferences Prefs = PreferenceManager.getDefaultSharedPreferences(c);
        Prefs.edit().putInt("height", height).apply();
    }
}
