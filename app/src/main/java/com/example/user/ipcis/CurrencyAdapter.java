package com.example.user.ipcis;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class CurrencyAdapter extends ArrayAdapter<Currency> {

    private final int EXCHANGER_ID = 2;
    private final int STOCK_ID = 1;
    private SharedPreferences sharedPreferences;


    public CurrencyAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Currency> currency) {
        super(context, 0,  currency);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null){
            if(getItem(position).getType() == STOCK_ID){
                listItemView = LayoutInflater.from(getContext()).inflate(R.layout.currency_item_two, parent, false);
            }else {
                listItemView = LayoutInflater.from(getContext()).inflate(R.layout.currency_item, parent, false);
            }
        }

        Currency currentCurrency = getItem(position);

        if (currentCurrency.getType() == STOCK_ID) {
            //has 3 column
            ImageView currencyFlag = (ImageView) listItemView.findViewById(R.id.stock_flag_new);
            currencyFlag.setImageResource(currentCurrency.getCurrencyFlag());
            //set image currentCurrency.getCurrencyFlag();

            TextView currencyName = (TextView) listItemView.findViewById(R.id.stock_currency_name_new);
            currencyName.setText(currentCurrency.getCurrencyName());

            TextView currencyQuantity = (TextView) listItemView.findViewById(R.id.stock_amount_new);
            String addToQuantity = getContext().getString(R.string.for_quantity);
            currencyQuantity.setText(addToQuantity + " " + currentCurrency.getQuantity());

            TextView buyPrice = (TextView) listItemView.findViewById(R.id.stock_price_new);
            buyPrice.setText("" + currentCurrency.getStockPrice());

        } else if (currentCurrency.getType() == EXCHANGER_ID) {
            //has 4 columns
            ImageView currencyFlag = (ImageView) listItemView.findViewById(R.id.currencyFlag);
            currencyFlag.setImageResource(currentCurrency.getCurrencyFlag());

            TextView currencyName = (TextView) listItemView.findViewById(R.id.currencyName);
            currencyName.setText(currentCurrency.getCurrencyName());

            TextView currencyQuantity = (TextView) listItemView.findViewById(R.id.quantity);
            currencyQuantity.setText("" + currentCurrency.getQuantity());

            TextView buyPrice = (TextView) listItemView.findViewById(R.id.buyPrice);
            buyPrice.setText("" + currentCurrency.getExchangerBuyPrice());

            TextView sellPrice = (TextView) listItemView.findViewById(R.id.sellPrice);
            sellPrice.setVisibility(View.VISIBLE);
            sellPrice.setText("" + currentCurrency.getExchangerSellPrice());
        }
        return listItemView;
    }
}
