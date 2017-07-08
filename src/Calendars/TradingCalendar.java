package Calendars;

import java.time.LocalDate;
import java.util.TreeSet;

/**
 * Created by atamkapoor on 2017-07-04.
 */
public class TradingCalendar {
    private TreeSet<LocalDate> holidays;

    public void setHolidays(TreeSet<LocalDate> holidays){
        this.holidays = holidays;
    }

    public TreeSet<LocalDate> getHolidays(){
        return holidays;
    }

}
