package com.example.stockprovider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import static com.example.stockprovider.DBConnect.TABLE_NAME;

public class Provider extends ContentProvider {
    private SQLiteDatabase mDatabase;
    public static final String PROVIDER = "com.example.stockprovider.Provider";
    public static final String URL = "content://" + PROVIDER + "/Stocks";
    public static final Uri CONTENT_URI = Uri.parse(URL);

    @Override
    public boolean onCreate() {
        Context context = getContext();
        DatabaseHelper mDatabaseHelper = new DatabaseHelper(context);
        mDatabase = mDatabaseHelper.getWritableDatabase();
        return (mDatabaseHelper != null ? true : false);
    }

    @Override
    public int update(Uri uri, ContentValues values, String sels, String[] args) {
        return 0;
    }

    @Override
    public int delete(Uri uri, String sel, String args[]) {
        return 0;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Context context = getContext();
        DatabaseHelper mDatabaseHelper = new DatabaseHelper(context);
        mDatabase = mDatabaseHelper.getWritableDatabase();
        long rowID = mDatabase.insert(TABLE_NAME, "", values);
        if(rowID > 0) {
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }
        throw new SQLException("Failed to add a record into " + uri);
    }

    @Override
    public Cursor query(Uri uri, String[] projections, String sel, String[] args, String sortOrder) {
        Cursor cursor = mDatabase.query(TABLE_NAME, projections, sel, args, null, null, sortOrder);
        getContext().getContentResolver().notifyChange(uri, null);
        return cursor;
    }
}
