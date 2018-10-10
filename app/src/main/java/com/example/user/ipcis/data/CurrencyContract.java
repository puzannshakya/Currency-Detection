package com.example.user.ipcis.data;

import android.provider.BaseColumns;

public final class CurrencyContract {

    private CurrencyContract(){}

    public static final class CurrencyEntry implements BaseColumns {

        public final static String TABLE_NAME = "currencies";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_CURRENCY_JSON = "json";
    }
}
