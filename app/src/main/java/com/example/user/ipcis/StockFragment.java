package com.example.user.ipcis;


import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.user.ipcis.data.CurrencyData;
import com.example.user.ipcis.data.CurrencyDbHelper;
import com.example.user.ipcis.data.CurrencyContract;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import android.app.LoaderManager;


public class StockFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private final String URL_NPR = "https://api.myjson.com/bins/l1pvo";

    private TextView mBuyTextView;
    private TextView mSellTextView;
    private LinearLayout mTopBarLinearLayout;
    private CurrencyAdapter mAdapter;

    private final int STOCK_ID = 1;
    private final int CURRENCY_LOADER_ID = 3;

    private CurrencyDbHelper mDbHelper;

    private LinearLayout refreshLinearLayout;

    private ProgressDialog progressDialog;
    private SQLiteDatabase db;
    private int recordsCount = -1;
    private Cursor mCountCursor;
    public LoaderManager loaderManager;

    private boolean isStartedLoader = false;

    public final String LOG_TAG = this.getClass().getSimpleName();

    public StockFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        PreferenceManager.getDefaultSharedPreferences(getContext())
                .registerOnSharedPreferenceChangeListener(this);
        super.onResume();
    }

    @Override
    public void onDestroy() {
        PreferenceManager.getDefaultSharedPreferences(getContext())
                .unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_stock, container, false);
        //Find reference to the list view
        ListView currencyListView = (ListView) rootView.findViewById(R.id.list);

        //Hides linear layout top bar Flag/Quantity/Price
        mTopBarLinearLayout = (LinearLayout) rootView.findViewById(R.id.top_bar);
        mTopBarLinearLayout.setVisibility(View.GONE);

        //Hide notification cation. Notifies that page needs to be refreshed
        refreshLinearLayout = (LinearLayout) rootView.findViewById(R.id.noDataNotification);

        refreshLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Kludge - in emulator when no WiFi but LTE and no Airplane mode, it tries to access
                //network and send the http request
                MainActivity.isFirstRun = true;
                getActivity().recreate();
            }
        });
        refreshLinearLayout.setVisibility(View.GONE);


        //Assigning the text to top line last two TextViews (it varies between activities)
        mBuyTextView = (TextView) rootView.findViewById(R.id.buyPrice);
        mSellTextView = (TextView) rootView.findViewById(R.id.sellPrice);

        //Only stock price mentioned in this Fragment
        mBuyTextView.setText(R.string.price);
        //Top line has only two columns in this fragment
        mSellTextView.setVisibility(View.GONE);

        progressDialog = new ProgressDialog(getActivity());

        //Initializing database Helper
        mDbHelper = new CurrencyDbHelper(getContext());

        db = mDbHelper.getReadableDatabase();

        //Get a reference to the connectivity manage to check state of network connectivity
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        //Get details on currently active default data networks
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        //On first open attempt to download new data
        if (MainActivity.isFirstRun) {
            if (networkInfo != null && networkInfo.isConnected()) {
                loaderManager = getActivity().getLoaderManager();
                loaderManager.initLoader(CURRENCY_LOADER_ID, null, resultLoader);
            }
        }

        //Create a new adapter with empty List of currency
        mAdapter = new CurrencyAdapter(getContext(), 0, new ArrayList<Currency>());

        currencyListView.setAdapter(mAdapter);

        //On first open don't try to display data
        if (!MainActivity.isFirstRun || !(networkInfo != null && networkInfo.isConnected())) {
            displayInfo();
        }

        return rootView;
    }


    private void displayInfo() {
        //Getting Reference on DB to read from
        SQLiteDatabase dbDisplay = mDbHelper.getReadableDatabase();

        //Initializing cursor to read from DB
        Cursor cursor = dbDisplay.query(CurrencyContract.CurrencyEntry.TABLE_NAME, null, null, null, null, null, null);
        if (cursor.getCount() == 0) {
            mAdapter.clear();
            refreshLinearLayout.setVisibility(View.VISIBLE);
        } else {
            try {
                Log.e(LOG_TAG, "Database is not empty, executing");
                int jsonColumnIndex = cursor.getColumnIndex(CurrencyContract.CurrencyEntry.COLUMN_CURRENCY_JSON);

                cursor.moveToLast();
                String jsonStringOfCurrencies = cursor.getString(jsonColumnIndex);

                List<Currency> currencies = parseJSON(jsonStringOfCurrencies);

                //Update the adapter
                mAdapter.clear();
                if (currencies != null && !currencies.isEmpty()) {
                    mAdapter.addAll(currencies);
                }
            } catch (Exception e) {
                Log.e(LOG_TAG, "Error displaying data" + e);
            } finally {
                cursor.close();
            }
        }
    }

    private List<Currency> parseJSON(String jsonString) throws JSONException {
        List<Currency> currencies = new ArrayList<Currency>();
        try {
            JSONObject currenciesJSON = new JSONObject(jsonString);
            //Parse JSON file and put Data in ArrayList
            JSONObject rates = currenciesJSON.getJSONObject("rates");
            if (rates != null) {
                double priceOnStock;
                for (int i = 0; i < CurrencyData.curAcronyms.length; i++) {
                    priceOnStock = getPrice(CurrencyData.curAcronyms[i], rates);
                    //TODO separate methods on PARSE, FILL with Data form DB (on pref change and no wifi), get data from web

                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                    if (sharedPreferences.getBoolean(CurrencyData.curAcronyms[i].toLowerCase(), true)) {
                        currencies.add(new Currency(CurrencyData.curAcronyms[i], priceOnStock, STOCK_ID));
                    }
                }
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error parsing JSON" + e);
        }

        return currencies;
    }

    private double getPrice(String currencyAcronym, JSONObject rates) {
        double priceOnStock = 0.000;
        try {
            priceOnStock = rates.getDouble(currencyAcronym);
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error getting price " + e);
        }
        return priceOnStock;
    }


    private LoaderManager.LoaderCallbacks<JSONObject> resultLoader = new LoaderManager.LoaderCallbacks<JSONObject>() {
        @Override
        public Loader<JSONObject> onCreateLoader(int id, Bundle args) {
            progressDialog.setMessage(getString(R.string.getting_rates));
            progressDialog.setCancelable(false);
            progressDialog.show();
            isStartedLoader = true;
            timerDelayRemoveDialog(6000, progressDialog);
            return new CurrencyLoader(getContext(), URL_NPR);
        }

        @Override
        public void onLoadFinished(Loader<JSONObject> loader, JSONObject JsonOfCurrencies) {
            sendJsonToDatabase(JsonOfCurrencies);
            displayInfo();
            progressDialog.dismiss();
            //Loader has ended, so we don't refresh data twice
            isStartedLoader = false;
            //Finished loading and no need to load on opening of StockFragment
            MainActivity.isFirstRun = false;
        }

        @Override
        public void onLoaderReset(Loader<JSONObject> loader) {
            progressDialog.dismiss();
            isStartedLoader = false;
        }
    };

    //Find Last Record ID
    private long findIdOfLastRecord() {
        //Get all rows
        Cursor cursor = db.query(CurrencyContract.CurrencyEntry.TABLE_NAME, null, null, null, null, null, null);
        long bufId = -1;
        try {
            //Get ID of _ID row
            int idColumnIndex = cursor.getColumnIndex(CurrencyContract.CurrencyEntry._ID);
            //Move to last record
            cursor.moveToLast();
            //Get an ID of last records
            bufId = cursor.getLong(idColumnIndex);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error getting last record" + e);
        } finally {
            cursor.close();
        }
        return bufId;
    }

    public void sendJsonToDatabase(JSONObject currenciesJSON) {
        //read the JSONobject and fill the List of currencies;
        try {
            String jsonString = currenciesJSON.toString();
            writeInDatabase(jsonString);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error sending data to DB " + e);
        }
    }

    private void writeInDatabase(String jsonString) {
        SQLiteDatabase dbInserter = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(CurrencyContract.CurrencyEntry.COLUMN_CURRENCY_JSON, jsonString);
        //Check if table is empty, then INSERT new record
        //If table is not, UPDATE on the place of last record
        if (countRecords() == -1 || countRecords() == 0) {
            long newRowId = dbInserter.insert(CurrencyContract.CurrencyEntry.TABLE_NAME, null, values);
        } else {
            //Update table_name set COLUMN_CURRENCY_JSON = jsonString where _id = findIdOfLastRecord
            String selection = "_id = ?";
            String selectionArgs[] = {"" + findIdOfLastRecord()};
            dbInserter.update(CurrencyContract.CurrencyEntry.TABLE_NAME, values, selection, selectionArgs);
        }


    }

    private int countRecords() {
        try {
            mCountCursor = db.query(CurrencyContract.CurrencyEntry.TABLE_NAME, null, null, null, null, null, null);
            recordsCount = mCountCursor.getCount();
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error getting number of records" + e);
        } finally {
            mCountCursor.close();
        }
        return recordsCount;
    }

    public void timerDelayRemoveDialog(long time, final ProgressDialog pd) {
        if (isStartedLoader) {
            try {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        pd.dismiss();
                        loaderManager.destroyLoader(CURRENCY_LOADER_ID);
                        displayInfo();
                        isStartedLoader = false;
                    }
                }, time);
            } catch (Exception e) {
                Log.e(LOG_TAG, "Time Delay Remover Dialog error " + e);
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        //once Updated Preferences - update list of displayed currencies
        displayInfo();
    }
}