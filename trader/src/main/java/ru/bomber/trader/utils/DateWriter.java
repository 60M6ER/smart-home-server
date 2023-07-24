package ru.bomber.trader.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateWriter {

    private static final TimeZone TZ = TimeZone.getTimeZone("Europe/Moscow");
    private static final String DF_STRING = "yyyy-MM-dd HH:mm:ss";
    private static DateFormat DF;


    private static DateFormat getDF() {
        if (DF == null) {
            DF = new SimpleDateFormat(DF_STRING);
            DF.setLenient(false);
            DF.setTimeZone(TZ);
        }

        return DF;
    }

    public static String getMessage(String message) {
        Date curDate = new Date();
        StringBuilder sb = new StringBuilder(getDF().format(curDate));
        sb.append(". ").append(message);

        return sb.toString();
    }
}
