package com.example.rental;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Methods {

    public static String getFormattedTime(String format, long millisecond) {
        return (new SimpleDateFormat(format, Locale.getDefault())).format(new Date(millisecond));
    }

}
