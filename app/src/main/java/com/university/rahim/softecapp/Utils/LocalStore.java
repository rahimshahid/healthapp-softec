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

    public static int getSteps(Context c) {
        SharedPreferences Prefs = PreferenceManager.getDefaultSharedPreferences(c);
        return Prefs.getInt("steps",0);
    }

    public static void setSteps(Context c, int steps) {
        SharedPreferences Prefs = PreferenceManager.getDefaultSharedPreferences(c);
        Prefs.edit().putInt("steps", steps).apply();
    }

    public static float getCals(Context c) {
        SharedPreferences Prefs = PreferenceManager.getDefaultSharedPreferences(c);
        return Prefs.getFloat("cals",0);
    }

    public static void setCals(Context c, float steps) {
        SharedPreferences Prefs = PreferenceManager.getDefaultSharedPreferences(c);
        Prefs.edit().putFloat("cals", steps).apply();
    }

    public static float getDistance(Context c) {
        SharedPreferences Prefs = PreferenceManager.getDefaultSharedPreferences(c);
        return Prefs.getFloat("dist",0);
    }

    public static void setDistance(Context c, float steps) {
        SharedPreferences Prefs = PreferenceManager.getDefaultSharedPreferences(c);
        Prefs.edit().putFloat("dist", steps/1000).apply();
    }

    public static void setWorkoutProgress(Context c, int height) {
        SharedPreferences Prefs = PreferenceManager.getDefaultSharedPreferences(c);
        Prefs.edit().putInt("prog", height).apply();
    }

    public static int getWorkoutProgess(Context c) {
        SharedPreferences Prefs = PreferenceManager.getDefaultSharedPreferences(c);
        return Prefs.getInt("prog",0);
    }
}
