package com.spring.boot.exam_service.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
    public static String convertMillisecondsToDate(long milliseconds) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date(milliseconds);
        return sdf.format(date);
    }
}
