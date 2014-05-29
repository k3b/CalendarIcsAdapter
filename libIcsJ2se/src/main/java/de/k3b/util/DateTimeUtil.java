package de.k3b.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Date helper functions.<br/>
 * Created by k3b on 18.05.2014.
 */
public class DateTimeUtil {
    final private static java.text.DateFormat isoDateTimeformatter = new SimpleDateFormat(
            "yyyyMMdd'T'HHmmss'Z'", Locale.US);

    /** make it easier to create a date from its components */
    public static Date createDate(final int year, final int monthOfYear,
                                  final int dayOfMonth, final int hour, final int minute,
                                  final int second) {
        final Calendar c = Calendar.getInstance();
        c.set(year, monthOfYear - 1,dayOfMonth, hour, minute,second);
        return c.getTime();
    }


    /** compatibel to parseIsoDate with iso date format yyyyMMdd'T'HHmmss'Z' */
    public static String toIsoDate(final long date) {
        return toIsoDate(new Date(date));
    }

    /** compatibel to parseIsoDate with iso date format yyyyMMdd'T'HHmmss'Z' */
    public static String toIsoDate(final Date date) {
        return isoDateTimeformatter.format(date);
    }

    /** compatibel to toIsoDate with iso date format yyyyMMdd'T'HHmmss'Z' */
    public static  Date parseIsoDate(final String mDateSelectedForAdd)
            throws ParseException {
        return isoDateTimeformatter.parse(mDateSelectedForAdd);
    }

    public static long getDiffInSeconds(final Date date1, final Date date2) {
        if ((date1 == null) || (date2 == null)) return Long.MAX_VALUE;
        return ((date1.getTime() - date2.getTime()) / 1000);
    }
}
