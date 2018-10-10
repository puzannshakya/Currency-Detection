package com.example.user.ipcis;


import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

public class QueryUtils {

    private static String LOG_TAG = QueryUtils.class.getName();

    private QueryUtils() {
    }

    //Access the server by the link given and gives JSON object with date and prices as an output
    public static JSONObject fetchCurrencyData(String requestUrl){
        URL url = createUrl(requestUrl);
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        }catch (IOException e){
            Log.e(LOG_TAG, "Problem with HTTP request", e);
        }
        JSONObject currencies = exactFeatureFromJson(jsonResponse);
        return currencies;
    }

    private static JSONObject exactFeatureFromJson(String currencyJson){

        if (TextUtils.isEmpty(currencyJson)){
            return null;
        }
        //Create a JSON object
        JSONObject baseJsonResponse = null;
        //Try to parse JSON response string.
        try{
            baseJsonResponse = new JSONObject(currencyJson);
        }catch (JSONException e){
            Log.e(LOG_TAG, "Error occurred parsing the JSON" + e);
        }
        return baseJsonResponse;
    }

    private static URL createUrl(String stringUrl){
        URL url = null;
        try{
            url = new URL(stringUrl);
        }catch (MalformedURLException e){
            Log.e(LOG_TAG, "Problem with creating URL" + e);
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        if (url == null){
            return jsonResponse;
        }

        InputStream inputStream = null;
        HttpURLConnection urlConnection = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.connect();
            if (urlConnection.getResponseCode() == 200){
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            }else{
                Log.e(LOG_TAG, "Http request error code: " + urlConnection.getResponseCode());
            }
        }catch (IOException e){
            Log.e(LOG_TAG, "Problem retrieving currency JSON result" + e);
        }finally {
            if (urlConnection != null){
                urlConnection.disconnect();
            }
            if (inputStream != null){
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null){
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null){
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

}
