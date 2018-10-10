package com.example.user.ipcis;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;


public class Currency {

    private int mCurrencyFlag;
    private String mCurrencyName;
    private int mQuantity;
    private double mStockPrice;
    private double mExchangerSell;
    private double mExchangerBuy;
    private int mExchangerOrStock;


    public Currency(String currencyName, double price, int exchangerOrStock){
        mCurrencyName = currencyName;
        mStockPrice = flipPrice(price);
        //If Stock Then 3 fields
        //If Exchanger Then 4 Fields
        mExchangerOrStock = exchangerOrStock;

        setSellBuyQuantityStock(mCurrencyName, mStockPrice);
        mCurrencyFlag = setCurrencyFlag(mCurrencyName);

    }

    //flip the price from MYR to CURRENCY => CURRENCY to MYR
    private double flipPrice(double price){
        return 1/price;
    }

    //formats the price to accuracy of 3 decimal points
    public double formatPrice(double price){
        DecimalFormat formatter = new DecimalFormat("0.000");
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        formatter.setDecimalFormatSymbols(symbols);

        return Double.parseDouble(formatter.format(price));
    }

    private void setSellBuyQuantityStock (String currencyName,double price){
        //price varies depends on currency
        switch (currencyName){
            case "USD":
                setQuantityStockSellBuy(1, price, -0.01, 0.01);
                return;
            case "EUR":
                setQuantityStockSellBuy(1, price, -0.01, 0.01);
                return;
            case "SGD":
                setQuantityStockSellBuy(1, price, -0.01, 0.01);
                return;
            case "IDR":
                setQuantityStockSellBuy(1000000, price, -0.01, 0.01);
                return;
            case "THB":
                setQuantityStockSellBuy(100, price, -0.01, 0.01);
                return;
            case "PHP":
                setQuantityStockSellBuy(100, price, -0.01, 0.01);
                return;
            case "HKD":
                setQuantityStockSellBuy(100, price, -0.01, 0.01);
                return;
            case "AUD":
                setQuantityStockSellBuy(1, price, -0.01, 0.01);
                return;
            case "INR":
                setQuantityStockSellBuy(100, price, -0.01, 0.01);
                return;
            case "JPY":
                setQuantityStockSellBuy(100, price, -0.01, 0.01);
                return;
            case "KRW":
                setQuantityStockSellBuy(10000, price, -0.01, 0.01);
                return;
            case "CNY":
                setQuantityStockSellBuy(100, price, -0.01, 0.01);
                return;
            case "GBP":
                setQuantityStockSellBuy(1, price, -0.01, 0.01);
                return;
        }
    }

    // to make setSellBuyQuantityStock shorter and flexible
    // weBuyDiff - is negative value because we sell cheaper than stock
    // weSellDiff - is positive value because we buy more expensive than both we sell and stock price
    public void setQuantityStockSellBuy(int quantity, double price, double weSellDiff, double weBuyDiff){
        mQuantity = quantity;
        mStockPrice = formatPrice(mQuantity * price);
        mExchangerBuy = formatPrice(mQuantity * price + weBuyDiff);
        mExchangerSell = formatPrice(mQuantity * price + weSellDiff);
        return;
    }


    public int setCurrencyFlag(String currencyName){
        switch (currencyName){
            case "USD":
                return R.drawable.flag_united_states_of_america;
            case "EUR":
                return R.drawable.flag_eu;
            case "SGD":
                return R.drawable.flag_sg;
            case "IDR":
                return R.drawable.flag_indo;
            case "THB":
                return R.drawable.flag_thai;
            case "PHP":
                return R.drawable.flag_phil;
            case "HKD":
                return R.drawable.flag_hk;
            case "AUD":
                return R.drawable.flag_au;
            case "INR":
                return R.drawable.flag_india;
            case "JPY":
                return R.drawable.flag_japan;
            case "KRW":
                return R.drawable.flag_skor;
            case "CNY":
                return R.drawable.flag_chn;
            case "GBP":
                return R.drawable.flag_uk;
            default:
                return R.drawable.flag_circle;
        }
    }

    public int getCurrencyFlag() {
        return mCurrencyFlag;
    }

    public int getType(){
        return mExchangerOrStock;
    }

    public String getCurrencyName(){
        return mCurrencyName;
    }

    public int getQuantity(){
        return mQuantity;
    }

    public double getExchangerSellPrice(){
        return mExchangerSell;
    }

    public double getExchangerBuyPrice(){
        return mExchangerBuy;
    }

    public double getStockPrice(){
        return mStockPrice;
    }

}
