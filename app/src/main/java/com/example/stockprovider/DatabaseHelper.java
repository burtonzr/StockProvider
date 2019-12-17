package com.example.stockprovider;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private final String CREATE_TABLE = "CREATE TABLE " + DBConnect.TABLE_NAME +
            " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
            DBConnect.StockName + " TEXT NOT NULL," +
            DBConnect.StockMarketPrice + " TEXT NOT NULL, " +
            DBConnect.StockChange + " TEXT NOT NULL, Symbol TEXT NOT NULL)";
    private final String DROP_TABLE = "DROP TABLE IF EXISTS " + DBConnect.TABLE_NAME;

    public DatabaseHelper(Context context) {
        super(context, "StockProvider.db", null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE);
        onCreate(db);
    }

    public boolean checkStockName(String StockName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Stocks WHERE StockName=?", new String[] {StockName});
        if(cursor.getCount() > 0) {
            return false;
        } else {
            return true;
        }
    }
}
