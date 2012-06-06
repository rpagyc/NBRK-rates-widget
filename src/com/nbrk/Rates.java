package com.nbrk;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: X120e
 * Date: 11.05.12
 * Time: 9:16
 * To change this template use File | Settings | File Templates.
 */
public class Rates {

    private String pubDate;
    private String currency;
    private String rate;
    private String index;

    public Rates() {
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public Integer getFlag(String currency) {
        if (currency.equalsIgnoreCase("RUR")){
            return R.drawable.rur;
        } else if (currency.equalsIgnoreCase("USD")){
            return R.drawable.usd;
        } else if (currency.equalsIgnoreCase("EUR")){
            return R.drawable.eur;
        }
        return null;
    }

}
