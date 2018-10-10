package com.example.user.ipcis;

import android.content.Context;
import android.content.AsyncTaskLoader;

import org.json.JSONObject;

public class CurrencyLoader extends AsyncTaskLoader<JSONObject> {

    private String mUrl;


    public CurrencyLoader(Context context, String url){
        super(context);
        mUrl = url;
    }

    @Override
    public JSONObject loadInBackground() {
        if (mUrl == null){
            return null;
        }
        JSONObject currenciesJson = QueryUtils.fetchCurrencyData(mUrl);
        return currenciesJson;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

}
