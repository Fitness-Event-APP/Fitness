package com.example.fitnessevent.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

    private static final int MILLISECONDS_IN_A_DAY = 1000 * 60 * 60 * 24;
    private static final int MILLISECONDS_IN_A_MINUTE = 1000 * 60;

    private static long getMillisForDate(Date date) {
        return date.getTime();
    }

    public static int getDifferenceInDays(Date firstDate, Date secondDate) {
        return getDifferenceInDays(firstDate.getTime(), secondDate.getTime());
    }

    private static int getDifferenceInDays(long firstDate, long secondDate) {
        return Math.round((firstDate - secondDate) / MILLISECONDS_IN_A_DAY);
    }

    public static int diffSeconds(long d1, long d2) {
        long diff = Math.abs(d1 - d2);
        diff = diff / 1000;
        return (int)diff;
    }

    public static int diffSeconds(long d1) {
        return diffSeconds(System.currentTimeMillis(), d1);
    }

    public static String getEventDate(String dateNaiveIso8601) {
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(dateNaiveIso8601);
            SimpleDateFormat formatter = new SimpleDateFormat("EEE, MMM d, ''yy");
            return formatter.format(date);
        } catch (ParseException e) {
            return Constants.EMPTY_STRING;
        }
    }
}
