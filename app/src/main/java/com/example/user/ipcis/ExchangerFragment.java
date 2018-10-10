package com.example.user.ipcis;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
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

public class ExchangerFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private TextView mBuyTextView;
    private TextView mSellTextView;
    private CurrencyAdapter mAdapter;

    private final int EXCHANGER_ID = 2;

    private CurrencyDbHelper mDbHelper;

    public LinearLayout refreshLinearLayout;

    public final String LOG_TAG = this.getClass().getSimpleName();

    public ExchangerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_stock, container, false);
        //Find reference to the list view
        ListView currencyListView = (ListView) rootView.findViewById(R.id.list);

        //Hide notification cation. Notifies that page needs to be refreshed
        refreshLinearLayout = (LinearLayout) rootView.findViewById(R.id.noDataNotification);
        refreshLinearLayout.setVisibility(View.GONE);

        //Assigning the text to top line last two TextViews (it varies between activities)
        mBuyTextView = (TextView) rootView.findViewById(R.id.buyPrice);
        mSellTextView = (TextView) rootView.findViewById(R.id.sellPrice);

        //Adding one more column
        //Changing name of Price column to  buy column
        mBuyTextView.setText(R.string.buy);
        //Top line has 4 columns in this fragment
        mSellTextView.setVisibility(View.VISIBLE);
        mSellTextView.setText(R.string.sell);

        //Create a new adapter with empty List of currency
        mAdapter = new CurrencyAdapter(getContext(), 0, new ArrayList<Currency>());

        currencyListView.setAdapter(mAdapter);

        mDbHelper = new CurrencyDbHelper(getContext());

        displayInfo();

        return rootView;
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
                .registerOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }

    private void displayInfo(){

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        Cursor cursor = db.query(CurrencyContract.CurrencyEntry.TABLE_NAME, null, null, null, null, null, null);

        //if request is empty then ask to connect to network and refresh page
        if (cursor.getCount() == 0){
            mAdapter.clear();
            refreshLinearLayout.setVisibility(View.VISIBLE);
        //Else - continue exacting data from database
        }else {
            try {
                int jsonColumnIndexId = cursor.getColumnIndex(CurrencyContract.CurrencyEntry.COLUMN_CURRENCY_JSON);
                cursor.moveToLast();
                String jsonString = cursor.getString(jsonColumnIndexId);

                List<Currency> currencies = ComposeListOfCurrencies(jsonString);

                mAdapter.clear();
                if (currencies != null && !currencies.isEmpty()) {
                    mAdapter.addAll(currencies);
                }
            } catch (Exception e) {
                Log.e(LOG_TAG, "Error displaying info " + e);
            } finally {
                cursor.close();
            }
        }

    }

    //parses
    private List<Currency> ComposeListOfCurrencies(String jsonString){
        //Create an empty List of currencies
        List<Currency> currencies = new ArrayList<Currency>();

        //Create, read the JSONobject and fill the List of currencies;
        try{
            JSONObject currenciesJSON = new JSONObject(jsonString);
            JSONObject rates = currenciesJSON.getJSONObject("rates");
            if (rates != null){
                double priceOnStock;
                for (int i = 0; i < CurrencyData.curAcronyms.length; i++) {
                    priceOnStock = getPrice(CurrencyData.curAcronyms[i], rates);
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                    if (sharedPreferences.getBoolean(CurrencyData.curAcronyms[i].toLowerCase(), true)) {
                        currencies.add(new Currency(CurrencyData.curAcronyms[i], priceOnStock, EXCHANGER_ID));
                    }
                }
            }
        }catch (Exception e){
            Log.e(LOG_TAG, "List of currencies error " + e);
        }
        return currencies;

    }


    private double getPrice (String currencyAcronym, JSONObject rates){
        double priceOnStock = 0.000;
        try {
            priceOnStock = rates.getDouble(currencyAcronym);
        }catch (JSONException e){
            Log.e(LOG_TAG, "Error getting price" + e);
        }
        return priceOnStock;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        displayInfo();
    }
}
