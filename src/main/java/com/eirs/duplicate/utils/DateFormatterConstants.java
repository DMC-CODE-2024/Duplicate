package com.eirs.duplicate.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;

public interface DateFormatterConstants {

    DateTimeFormatter requestIdDateFormat = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    DateTimeFormatter macraSuffixFullDateFormat = DateTimeFormatter.ofPattern("yyyyMMdd");
    DateTimeFormatter macraFileHeaderDateFormat = DateTimeFormatter.ofPattern("yyyyMMdd");
    DateTimeFormatter fileSuffixDateFormat = DateTimeFormatter.ofPattern("yyyyMMddHH");
    DateTimeFormatter simpleDateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    DateTimeFormatter notificationSmsDateFormat = DateTimeFormatter.ofPattern("dd MMM yyyy");
}
