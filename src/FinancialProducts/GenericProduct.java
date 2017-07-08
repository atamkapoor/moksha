package FinancialProducts;

import Calendars.TradingCalendar;

import java.util.Currency;

/**
 * Created by atamkapoor on 2017-07-04.
 */

public class GenericProduct {
    private String description;
    private TradingCalendar tradingCalendar;
    private Currency currency;

    public void setDescription(String desc){
        description = desc;
    }

    public String getDescription(){
        return description;
    }

    public void setTradingCalendar(TradingCalendar cdr){
        tradingCalendar = cdr;
    }

    public TradingCalendar getTradingCalendar(){
        return tradingCalendar;
    }

    public void setCurrency(Currency ccy){
        currency = ccy;
    }

    public Currency getCurrency(){
        return currency;
    }

}