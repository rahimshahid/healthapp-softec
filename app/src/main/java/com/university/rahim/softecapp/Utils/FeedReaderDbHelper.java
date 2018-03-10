package com.university.rahim.softecapp.Utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by RAHIM on 3/10/2018.
 */

public class FeedReaderDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "HealthAppDB.db";

    public FeedReaderDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS STEPS(time STRING, value INTEGER);");
        db.execSQL("CREATE TABLE IF NOT EXISTS LOCATIONS(lon REAL,lat REAL);");
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS STEPS");
        db.execSQL("DROP TABLE IF EXISTS LOCATIONS");
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
    public void Destroy(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS STEPS");
        db.execSQL("DROP TABLE IF EXISTS LOCATIONS");
    }
}