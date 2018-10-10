package com.example.user.ipcis;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.user.ipcis.data.CurrencyContract;
import com.example.user.ipcis.data.CurrencyDbHelper;

import org.json.JSONObject;


import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;



public class ConverterFragment extends Fragment {

    private ImageView convertMyrFlagIV;
    private ImageView convertOthersFlagIV;
    private Spinner convertOthersCurrencyNameSpinner;
    public EditText convertMyrET;
    public EditText convertOthersET;

    private final int EXCHANGER_ID = 2;
    private final int MYR_FLAG = 10;
    private final int OTHERS_FLAG = 20;

    private final int INDEX_OF_EU = 1;
    private final boolean isFirstOpenConverter = true;

    private int quantityOfCurrency = 0;
    private double priceOfCurrency = 0.000;

    private JSONObject currenciesJSON;

    private ImageView mButtonEtOthers;
    private ImageView mButtonEtMYR;

    private TextView mHowMuchMyr;
    private TextView mHowMuchOthers;
    private double otherCurrencyNominalPrice;

    private boolean isAlreadyOpened = false;

    public final String LOG_TAG = this.getClass().getSimpleName();


    public ConverterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_converter, container, false);

        convertOthersFlagIV = (ImageView) rootView.findViewById(R.id.convert_others_flag);

        convertOthersCurrencyNameSpinner = (Spinner) rootView.findViewById(R.id.convert_others_acronym_spinner);
        convertOthersET = (EditText) rootView.findViewById(R.id.convert_others_currency_amount);
        mButtonEtOthers = (ImageView) rootView.findViewById(R.id.convert_others_ET_Button);
        mButtonEtOthers.setVisibility(View.GONE);

        convertMyrFlagIV = (ImageView) rootView.findViewById(R.id.convert_MYR_flag);
        convertMyrFlagIV.setImageResource(R.drawable.flag_nepall);
        convertMyrET = (EditText) rootView.findViewById(R.id.convert_MYR_amount);
        mButtonEtMYR = (ImageView) rootView.findViewById(R.id.convert_MYR_ET_Button);
        mButtonEtMYR.setVisibility(View.GONE);

        mHowMuchMyr = (TextView) rootView.findViewById(R.id.howMuchMYR);
        mHowMuchOthers = (TextView) rootView.findViewById(R.id.howMuchOther);

        convertOthersET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(convertOthersET.hasFocus()){
                    try{
                        if ("".equals(convertOthersET.getText().toString())){
                            convertMyrET.setText("");
                            mButtonEtMYR.setVisibility(View.GONE);
                            mButtonEtOthers.setVisibility(View.GONE);
                            return;
                        }
                        parseJson(convertOthersCurrencyNameSpinner.getSelectedItem().toString(), OTHERS_FLAG);
                        double bufDouble =
                                calculateConversion(Double.parseDouble(convertOthersET.getText().toString()), OTHERS_FLAG);
                        convertMyrET.setText(String.valueOf(formatPrice(bufDouble)));

                        mButtonEtOthers.setVisibility(View.VISIBLE);
                        mButtonEtMYR.setVisibility(View.VISIBLE);

                    }catch (Exception e){
                        Log.e(LOG_TAG, "Error in other currencies ET" + e);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        convertMyrET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(convertMyrET.hasFocus()){
                    try{
                        if ("".equals(convertMyrET.getText().toString())){
                            convertOthersET.setText("");
                            mButtonEtOthers.setVisibility(View.GONE);
                            mButtonEtMYR.setVisibility(View.GONE);
                            return;
                        }
                        parseJson(convertOthersCurrencyNameSpinner.getSelectedItem().toString(), MYR_FLAG);
                        double bufDouble =
                                calculateConversion(Double.parseDouble(convertMyrET.getText().toString()), MYR_FLAG);
                        convertOthersET.setText(String.valueOf(formatPrice(bufDouble)));

                        mButtonEtOthers.setVisibility(View.VISIBLE);
                        mButtonEtMYR.setVisibility(View.VISIBLE);

                    }catch (Exception e){
                        Log.e(LOG_TAG, "Error in MYR ET " + e);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        convertOthersCurrencyNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try{
                    String curAcrName = convertOthersCurrencyNameSpinner.getSelectedItem().toString();
                    parseJson(curAcrName, OTHERS_FLAG);
                    setPriceNotes(curAcrName);
                    double bufDouble =
                            calculateConversion(Double.parseDouble(convertOthersET.getText().toString()), OTHERS_FLAG);
                    bufDouble = formatPrice(bufDouble);
                    convertMyrET.setText(String.valueOf(bufDouble));


                }catch (Exception e){
                    Log.e(LOG_TAG, "problem in changing currency " + e);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //On click of cross clear both EditTexts
        mButtonEtOthers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mButtonEtOthers.setVisibility(View.GONE);
                mButtonEtMYR.setVisibility(View.GONE);
                convertOthersET.setText("");
                convertMyrET.setText("");
            }
        });

        mButtonEtMYR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mButtonEtOthers.setVisibility(View.GONE);
                mButtonEtMYR.setVisibility(View.GONE);
                convertOthersET.setText("");
                convertMyrET.setText("");
            }
        });


        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        //if opened for first time then load data
        if (!isAlreadyOpened) {
            try {
                if (isVisibleToUser) {
                    loadJsonFromDb();
                    isAlreadyOpened = true;
                    //Kludge
                    if (isFirstOpenConverter) {
                        convertOthersCurrencyNameSpinner.setSelection(INDEX_OF_EU);
                    }
                }
            } catch (Exception e) {
                Log.e(LOG_TAG, "Exceprion on start occured " + e);
            }
        }
    }

    //Gets the Json String from DB and put in global object.
    private void loadJsonFromDb() {
        CurrencyDbHelper mDbHelper = new CurrencyDbHelper(getContext());
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor = db.query(CurrencyContract.CurrencyEntry.TABLE_NAME, null, null, null, null, null, null);
        try {
            if (cursor.getCount() != 0) {
                //find index of jsonString column in DB
                int jsonColumnIndex = cursor.getColumnIndex(CurrencyContract.CurrencyEntry.COLUMN_CURRENCY_JSON);
                //move to last record (freshest)
                cursor.moveToLast();
                //get String of json
                String jsonString = cursor.getString(jsonColumnIndex);
                //Convert string in JSON Object
                currenciesJSON = new JSONObject(jsonString);
            }
        }catch(Exception e){
            Log.e(LOG_TAG, "Error loading from Db " + e);
        }finally {
            cursor.close();
        }
    }

    private void parseJson(String currency, int MyrOrOthers){
        double price;
        int quantity;
        int myr=MyrOrOthers;
        try {
            //Get Inner Object from JSON object
            JSONObject rates = currenciesJSON.getJSONObject("rates");
            //Get price by passing acronym to Json, searching for price, converting to Double
            price = Double.parseDouble(rates.getString(currency));

            //Create object Currency to get sell / buy / stock price if required
            Currency bufCurrency = new Currency(currency, price, EXCHANGER_ID);

            quantity = bufCurrency.getQuantity();
            convertOthersFlagIV.setImageResource(bufCurrency.getCurrencyFlag());
            //getting price for under conversion info
            otherCurrencyNominalPrice = price;

            if(myr == MYR_FLAG){
                //price stays the same as in JSON ex: 0.424, not reversed 1/0.424 as in Currency.class
                price=otherCurrencyNominalPrice;
            }else if (myr == OTHERS_FLAG){
                price = bufCurrency.getStockPrice();
            }
            quantityOfCurrency = quantity;
            priceOfCurrency = price;

            Log.d("Converter",String.valueOf(quantityOfCurrency));
            Log.d("Converter",String.valueOf(priceOfCurrency));
        }catch (Exception e){
            Log.e(LOG_TAG, "Error parsing JSON " + e);
        }

    }

    //Pass the price from TextView Edited
    private double calculateConversion(double amountFrom, int MyrOrOthers){
        if (MyrOrOthers == OTHERS_FLAG) {
            return (priceOfCurrency / quantityOfCurrency) * amountFrom;
        }else {
            return  priceOfCurrency* amountFrom;
        }
    }

    //Truncating the number so 4.239 will be 1 = 4.23 not 4.24
    public double formatPrice(double price){
        DecimalFormat formatter = new DecimalFormat("0.0000");
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        return Double.parseDouble(formatter.format(Math.floor(price * 100) / 100));
    }

    private void setPriceNotes(String currencyName){
        String oneMyrIs = "1 NPR = " + otherCurrencyNominalPrice + " " + currencyName;
        String oneOtherIs = quantityOfCurrency + " " + currencyName + " = "
                + priceOfCurrency + " NPR" ;
        mHowMuchMyr.setText(oneMyrIs);
        mHowMuchOthers.setText(oneOtherIs);
    }

}
