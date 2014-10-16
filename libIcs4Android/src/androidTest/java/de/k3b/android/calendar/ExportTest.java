package de.k3b.android.calendar;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.test.AndroidTestCase;

import junit.framework.Assert;

import net.fortuna.ical4j.model.Calendar;

import de.k3b.calendar.EventDtoSimple;
import de.k3b.calendar.EventFilter;
import de.k3b.calendar.EventFilterDto;
import de.k3b.util.DateTimeUtil;

public class ExportTest extends AndroidTestCase {
    public static final long ONE_DAY_IN_MILLISECS = 24 * 60 * 60 *1000;

    private static final String lastExport = normalize("BEGIN:VCALENDAR\n" +
            "PRODID:-//de.k3b.calendar.adapter//iCal4j 1.0//EN\n" +
            "VERSION:2.0\n" +
            "CALSCALE:GREGORIAN\n" +
            "UID:1\n" +
            "BEGIN:VTIMEZONE\n" +
            "TZID:Australia/Sydney\n" +
            "TZURL:http://tzurl.org/zoneinfo/Australia/Sydney\n" +
            "X-LIC-LOCATION:Australia/Sydney\n" +
            "BEGIN:STANDARD\n" +
            "TZOFFSETFROM:+1100\n" +
            "TZOFFSETTO:+1000\n" +
            "TZNAME:EST\n" +
            "DTSTART:20080406T030000\n" +
            "RRULE:FREQ=YEARLY;BYMONTH=4;BYDAY=1SU\n" +
            "END:STANDARD\n" +
            "BEGIN:DAYLIGHT\n" +
            "TZOFFSETFROM:+1000\n" +
            "TZOFFSETTO:+1100\n" +
            "TZNAME:EST\n" +
            "DTSTART:20081005T020000\n" +
            "RRULE:FREQ=YEARLY;BYMONTH=10;BYDAY=1SU\n" +
            "END:DAYLIGHT\n" +
            "BEGIN:STANDARD\n" +
            "TZOFFSETFROM:+100452\n" +
            "TZOFFSETTO:+1000\n" +
            "TZNAME:EST\n" +
            "DTSTART:18950201T000000\n" +
            "RDATE:18950201T000000\n" +
            "END:STANDARD\n" +
            "BEGIN:DAYLIGHT\n" +
            "TZOFFSETFROM:+1000\n" +
            "TZOFFSETTO:+1100\n" +
            "TZNAME:EST\n" +
            "DTSTART:19170101T004128\n" +
            "RDATE:19170101T004128\n" +
            "RDATE:19420101T020000\n" +
            "RDATE:19420927T020000\n" +
            "RDATE:19431003T020000\n" +
            "RDATE:19711031T020000\n" +
            "RDATE:19721029T020000\n" +
            "RDATE:19731028T020000\n" +
            "RDATE:19741027T020000\n" +
            "RDATE:19751026T020000\n" +
            "RDATE:19761031T020000\n" +
            "RDATE:19771030T020000\n" +
            "RDATE:19781029T020000\n" +
            "RDATE:19791028T020000\n" +
            "RDATE:19801026T020000\n" +
            "RDATE:19811025T020000\n" +
            "RDATE:19821031T020000\n" +
            "RDATE:19831030T020000\n" +
            "RDATE:19841028T020000\n" +
            "RDATE:19851027T020000\n" +
            "RDATE:19861019T020000\n" +
            "RDATE:19871025T020000\n" +
            "RDATE:19881030T020000\n" +
            "RDATE:19891029T020000\n" +
            "RDATE:19901028T020000\n" +
            "RDATE:19911027T020000\n" +
            "RDATE:19921025T020000\n" +
            "RDATE:19931031T020000\n" +
            "RDATE:19941030T020000\n" +
            "RDATE:19951029T020000\n" +
            "RDATE:19961027T020000\n" +
            "RDATE:19971026T020000\n" +
            "RDATE:19981025T020000\n" +
            "RDATE:19991031T020000\n" +
            "RDATE:20000827T020000\n" +
            "RDATE:20011028T020000\n" +
            "RDATE:20021027T020000\n" +
            "RDATE:20031026T020000\n" +
            "RDATE:20041031T020000\n" +
            "RDATE:20051030T020000\n" +
            "RDATE:20061029T020000\n" +
            "RDATE:20071028T020000\n" +
            "END:DAYLIGHT\n" +
            "BEGIN:STANDARD\n" +
            "TZOFFSETFROM:+1100\n" +
            "TZOFFSETTO:+1000\n" +
            "TZNAME:EST\n" +
            "DTSTART:19170325T024028\n" +
            "RDATE:19170325T024028\n" +
            "RDATE:19420329T020000\n" +
            "RDATE:19430328T020000\n" +
            "RDATE:19440326T020000\n" +
            "RDATE:19720227T030000\n" +
            "RDATE:19730304T030000\n" +
            "RDATE:19740303T030000\n" +
            "RDATE:19750302T030000\n" +
            "RDATE:19760307T030000\n" +
            "RDATE:19770306T030000\n" +
            "RDATE:19780305T030000\n" +
            "RDATE:19790304T030000\n" +
            "RDATE:19800302T030000\n" +
            "RDATE:19810301T030000\n" +
            "RDATE:19820404T030000\n" +
            "RDATE:19830306T030000\n" +
            "RDATE:19840304T030000\n" +
            "RDATE:19850303T030000\n" +
            "RDATE:19860316T030000\n" +
            "RDATE:19870315T030000\n" +
            "RDATE:19880320T030000\n" +
            "RDATE:19890319T030000\n" +
            "RDATE:19900304T030000\n" +
            "RDATE:19910303T030000\n" +
            "RDATE:19920301T030000\n" +
            "RDATE:19930307T030000\n" +
            "RDATE:19940306T030000\n" +
            "RDATE:19950305T030000\n" +
            "RDATE:19960331T020000\n" +
            "RDATE:19970330T020000\n" +
            "RDATE:19980329T020000\n" +
            "RDATE:19990328T020000\n" +
            "RDATE:20000326T020000\n" +
            "RDATE:20010325T020000\n" +
            "RDATE:20020331T020000\n" +
            "RDATE:20030330T020000\n" +
            "RDATE:20040328T020000\n" +
            "RDATE:20050327T020000\n" +
            "RDATE:20060402T030000\n" +
            "RDATE:20070325T020000\n" +
            "END:STANDARD\n" +
            "BEGIN:STANDARD\n" +
            "TZOFFSETFROM:+1000\n" +
            "TZOFFSETTO:+1000\n" +
            "TZNAME:EST\n" +
            "DTSTART:19710101T000000\n" +
            "RDATE:19710101T000000\n" +
            "END:STANDARD\n" +
            "END:VTIMEZONE\n" +
            "BEGIN:VEVENT\n" +
            "UID:1\n" +
            "DTSTART;TZID=Australia/Sydney:20000501T203456\n" +
            "DTEND;TZID=Australia/Sydney:20000502T011234\n" +
            "SUMMARY:TITLE\n" +
            "DESCRIPTION:DESCRIPTION\n" +
            "LOCATION:eventLocation\n" +
            "ORGANIZER:mailto:max.mustermann@url.org\n" +
            "DURATION:P1D\n" +
            "RRULE:FREQ=YEARLY;BYMONTH=3;BYDAY=-1SU\n" +
            "RDATE:19610901T045612,19630901T045612\n" +
            "EXDATE:19710901T045612,19730901T045612\n" +
            "BEGIN:VALARM\n" +
            "TRIGGER:-PT30M\n" +
            "END:VALARM\n" +
            "BEGIN:VALARM\n" +
            "TRIGGER:-PT120M\n" +
            "END:VALARM\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR");

    public void testRegressionExportFull() {
        final Uri data = ACalendarCursor.createContentUri("events", "1");
        EventFilter filter = EventFilterDto.ALL;

        long dtstart = DateTimeUtil.createDate(2000, 5, 1, 12, 34, 56).getTime() + ONE_DAY_IN_MILLISECS;
        long dtend = DateTimeUtil.createDate(2000, 5, 1, 17,12,34).getTime() + ONE_DAY_IN_MILLISECS;

        boolean useMockCalendar = true;

        ACalendar2IcsEngine engine = new ACalendar2IcsEngine(this.getContext(), filter, useMockCalendar);
        Calendar vcalendar = engine.export(data, dtstart, dtend);

        String result = normalize(vcalendar.toString());
        Assert.assertEquals(lastExport, result);
    }

    private static String normalize(String s) {
        return s
                .replace("\r\n", "\n")
                .replace("\t", " ")
                .replace("  ", " ")
                .trim();
    }

}
