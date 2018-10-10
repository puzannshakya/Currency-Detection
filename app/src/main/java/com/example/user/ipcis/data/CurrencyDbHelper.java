package com.example.user.ipcis.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CurrencyDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "currencies.db";
    public static final int DATABASE_VERSION = 1;

    public CurrencyDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_CURRENCY_TABLE = "CREATE TABLE " + CurrencyContract.CurrencyEntry.TABLE_NAME + " ("
                + CurrencyContract.CurrencyEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + CurrencyContract.CurrencyEntry.COLUMN_CURRENCY_JSON + " TEXT NOT NULL);";
        db.execSQL(SQL_CREATE_CURRENCY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
