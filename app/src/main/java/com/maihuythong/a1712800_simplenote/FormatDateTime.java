package com.maihuythong.a1712800_simplenote;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FormatDateTime {

    public static String dateFromLong(long time){
        DateFormat format = new SimpleDateFormat("EEE, dd MM yyyy 'at' hh:mm:ss aaa", Locale.US);
        return format.format(new Date(time));
    }
}
