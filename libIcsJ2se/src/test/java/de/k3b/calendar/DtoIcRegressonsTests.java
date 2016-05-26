/*
 * Copyright (C) 2014- k3b
 *
 * This file is part of android.calendar.ics.adapter.
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 */
package de.k3b.calendar;

import net.fortuna.ical4j.data.ParserException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;

/**
 * Checks if outcome of ics export is the same as with last release.<br/>
 * If this test fails manually check if the result is still ok and
 * copy the new result from "but was ...." into this test.
 * This test must be updated if
 *  - TestDataUtils generate different(additional) data.
 *  - ical4j is updated to new australian timezone-info.
 *
 * Created by k3b on 02.06.2014.
 */

/*
Note: if you see this in android-studio-logcat when running this test:

	INFO: ical4j.properties not found.
	02.06.2014 10:56:17 net.fortuna.ical4j.model.TimeZoneRegistryImpl updateDefinition
	WARNUNG: Unable to retrieve updates for timezone: Australia/Sydney
	java.net.UnknownHostException: tzurl.org
		...
		at sun.net.www.http.HttpClient.openServer(HttpClient.java:395)
		...
		at net.fortuna.ical4j.model.TimeZoneRegistryImpl.updateDefinition(TimeZoneRegistryImpl.java:212)
		...
		at de.k3b.calendar.ics.EventDto2IcsFactory.getTimeZone(EventDto2IcsFactory.java:141)

This is due to that android studio 0.5.8 does not support resources in junit-tests.
Executing this test with gradle does not show this.

ical4j.properties tells ical4j not to use the internet to update timezone-infos.

*/
@Ignore("Generated ics content may differ in timezone definitions due to machine config")
public class DtoIcRegressonsTests {
    public static final long ONE_DAY_IN_MILLISECS = 24 * 60 * 60 *1000;
    private EventDtoSimple src = null;

    @Before
    public void setup() {
        src = TestDataUtils.createTestEventDto();
    }

    private static final String lastMininal = normalize("BEGIN:VCALENDAR\n" +
            "PRODID:jUnit-Tests\n" +
            "VERSION:2.0\n" +
            "CALSCALE:GREGORIAN\n" +
            "BEGIN:VEVENT\n" +
            "DTSTART:20000501T123456\n" +
            "DTEND:20000501T171234\n" +
            "SUMMARY:test title\n" +
            "DESCRIPTION:bla bla bla\n" +
            "DURATION:P1D\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR");

    /** same as lastMininal but with start/end one day later */
    private static final String lastMininalThisEvent = normalize("BEGIN:VCALENDAR\n" +
            "PRODID:jUnit-Tests\n" +
            "VERSION:2.0\n" +
            "CALSCALE:GREGORIAN\n" +
            "BEGIN:VEVENT\n" +
            "DTSTART:20000502T123456\n" + //!!!
            "DTEND:20000502T171234\n" + //!!!
            "SUMMARY:test title\n" +
            "DESCRIPTION:bla bla bla\n" +
            "DURATION:P1D\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR");
    /** same as lastMininalThisEvent but with recurrence data added */
    private static final String lastMininalThisAndAllFurtherEvents = normalize("BEGIN:VCALENDAR\n" +
            "PRODID:jUnit-Tests\n" +
            "VERSION:2.0\n" +
            "CALSCALE:GREGORIAN\n" +
            "BEGIN:VEVENT\n" +
            "DTSTART:20000502T123456\n" + //!!!
            "DTEND:20000502T171234\n" + //!!!
            "SUMMARY:test title\n" +
            "DESCRIPTION:bla bla bla\n" +
            "DURATION:P1D\n" +
            "RRULE:FREQ=YEARLY;BYMONTH=3;BYDAY=-1SU\n" + //!!!
            "RDATE:19610901T045612,19630901T045612\n" + //!!!
            "EXDATE:19710901T045612,19730901T045612\n" + //!!!
            "END:VEVENT\n" +
            "END:VCALENDAR");
    /** same as lastMininal but with recurrence data added. Note: start/end should be original and not updated */
    private static final String lastMininalAllEvents = normalize("BEGIN:VCALENDAR\n" +
            "PRODID:jUnit-Tests\n" +
            "VERSION:2.0\n" +
            "CALSCALE:GREGORIAN\n" +
            "BEGIN:VEVENT\n" +
            "DTSTART:20000501T123456\n" + //!!!
            "DTEND:20000501T171234\n" + //!!!
            "SUMMARY:test title\n" +
            "DESCRIPTION:bla bla bla\n" +
            "DURATION:P1D\n" +
            "RRULE:FREQ=YEARLY;BYMONTH=3;BYDAY=-1SU\n" +
            "RDATE:19610901T045612,19630901T045612\n" +
            "EXDATE:19710901T045612,19730901T045612\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR");
    private static final String lastdefault = normalize("BEGIN:VCALENDAR\n" +
            "PRODID:jUnit-Tests\n" +
            "VERSION:2.0\n" +
            "CALSCALE:GREGORIAN\n" +
            "BEGIN:VEVENT\n" +
            "UID:4711\n" +
            "DTSTART:20000501T123456\n" +
            "DTEND:20000501T171234\n" +
            "SUMMARY:test title\n" +
            "DESCRIPTION:bla bla bla\n" +
            "LOCATION:location\n" +
            "DURATION:P1D\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR");

    private static final String lastMaximal = normalize("BEGIN:VCALENDAR\n"+
            "PRODID:jUnit-Tests\n"+
            "VERSION:2.0\n"+
            "CALSCALE:GREGORIAN\n"+
            "UID:3\n"+
            "BEGIN:VTIMEZONE\n"+
            "TZID:Australia/Sydney\n"+
            "TZURL:http://tzurl.org/zoneinfo/Australia/Sydney\n"+
            "X-LIC-LOCATION:Australia/Sydney\n"+
            "BEGIN:STANDARD\n"+
            "TZOFFSETFROM:+1100\n"+
            "TZOFFSETTO:+1000\n"+
            "TZNAME:AEST\n"+
            "DTSTART:20080406T030000\n"+
            "RRULE:FREQ=YEARLY;BYMONTH=4;BYDAY=1SU\n"+
            "END:STANDARD\n"+
            "BEGIN:DAYLIGHT\n"+
            "TZOFFSETFROM:+1000\n"+
            "TZOFFSETTO:+1100\n"+
            "TZNAME:AEDT\n"+
            "DTSTART:20081005T020000\n"+
            "RRULE:FREQ=YEARLY;BYMONTH=10;BYDAY=1SU\n"+
            "END:DAYLIGHT\n"+
            "BEGIN:STANDARD\n"+
            "TZOFFSETFROM:+100452\n"+
            "TZOFFSETTO:+1000\n"+
            "TZNAME:AEST\n"+
            "DTSTART:18950201T000000\n"+
            "RDATE:18950201T000000\n"+
            "END:STANDARD\n"+
            "BEGIN:DAYLIGHT\n"+
            "TZOFFSETFROM:+1000\n"+
            "TZOFFSETTO:+1100\n"+
            "TZNAME:AEDT\n"+
            "DTSTART:19170101T000100\n"+
            "RDATE:19170101T000100\n"+
            "RDATE:19420101T020000\n"+
            "RDATE:19420927T020000\n"+
            "RDATE:19431003T020000\n"+
            "RDATE:19711031T020000\n"+
            "RDATE:19721029T020000\n"+
            "RDATE:19731028T020000\n"+
            "RDATE:19741027T020000\n"+
            "RDATE:19751026T020000\n"+
            "RDATE:19761031T020000\n"+
            "RDATE:19771030T020000\n"+
            "RDATE:19781029T020000\n"+
            "RDATE:19791028T020000\n"+
            "RDATE:19801026T020000\n"+
            "RDATE:19811025T020000\n"+
            "RDATE:19821031T020000\n"+
            "RDATE:19831030T020000\n"+
            "RDATE:19841028T020000\n"+
            "RDATE:19851027T020000\n"+
            "RDATE:19861019T020000\n"+
            "RDATE:19871025T020000\n"+
            "RDATE:19881030T020000\n"+
            "RDATE:19891029T020000\n"+
            "RDATE:19901028T020000\n"+
            "RDATE:19911027T020000\n"+
            "RDATE:19921025T020000\n"+
            "RDATE:19931031T020000\n"+
            "RDATE:19941030T020000\n"+
            "RDATE:19951029T020000\n"+
            "RDATE:19961027T020000\n"+
            "RDATE:19971026T020000\n"+
            "RDATE:19981025T020000\n"+
            "RDATE:19991031T020000\n"+
            "RDATE:20000827T020000\n"+
            "RDATE:20011028T020000\n"+
            "RDATE:20021027T020000\n"+
            "RDATE:20031026T020000\n"+
            "RDATE:20041031T020000\n"+
            "RDATE:20051030T020000\n"+
            "RDATE:20061029T020000\n"+
            "RDATE:20071028T020000\n"+
            "END:DAYLIGHT\n"+
            "BEGIN:STANDARD\n"+
            "TZOFFSETFROM:+1100\n"+
            "TZOFFSETTO:+1000\n"+
            "TZNAME:AEST\n"+
            "DTSTART:19170325T020000\n"+
            "RDATE:19170325T020000\n"+
            "RDATE:19420329T020000\n"+
            "RDATE:19430328T020000\n"+
            "RDATE:19440326T020000\n"+
            "RDATE:19720227T030000\n"+
            "RDATE:19730304T030000\n"+
            "RDATE:19740303T030000\n"+
            "RDATE:19750302T030000\n"+
            "RDATE:19760307T030000\n"+
            "RDATE:19770306T030000\n"+
            "RDATE:19780305T030000\n"+
            "RDATE:19790304T030000\n"+
            "RDATE:19800302T030000\n"+
            "RDATE:19810301T030000\n"+
            "RDATE:19820404T030000\n"+
            "RDATE:19830306T030000\n"+
            "RDATE:19840304T030000\n"+
            "RDATE:19850303T030000\n"+
            "RDATE:19860316T030000\n"+
            "RDATE:19870315T030000\n"+
            "RDATE:19880320T030000\n"+
            "RDATE:19890319T030000\n"+
            "RDATE:19900304T030000\n"+
            "RDATE:19910303T030000\n"+
            "RDATE:19920301T030000\n"+
            "RDATE:19930307T030000\n"+
            "RDATE:19940306T030000\n"+
            "RDATE:19950305T030000\n"+
            "RDATE:19960331T020000\n"+
            "RDATE:19970330T020000\n"+
            "RDATE:19980329T020000\n"+
            "RDATE:19990328T020000\n"+
            "RDATE:20000326T020000\n"+
            "RDATE:20010325T020000\n"+
            "RDATE:20020331T020000\n"+
            "RDATE:20030330T020000\n"+
            "RDATE:20040328T020000\n"+
            "RDATE:20050327T020000\n"+
            "RDATE:20060402T030000\n"+
            "RDATE:20070325T020000\n"+
            "END:STANDARD\n"+
            "BEGIN:STANDARD\n"+
            "TZOFFSETFROM:+1000\n"+
            "TZOFFSETTO:+1000\n"+
            "TZNAME:AEST\n"+
            "DTSTART:19710101T000000\n"+
            "RDATE:19710101T000000\n"+
            "END:STANDARD\n"+
            "END:VTIMEZONE\n"+
            "BEGIN:VEVENT\n"+
            "UID:4711\n"+
            "DTSTART;TZID=Australia/Sydney:20000501T203456\n"+
            "DTEND;TZID=Australia/Sydney:20000502T011234\n"+
            "SUMMARY:test title\n"+
            "DESCRIPTION:bla bla bla\n"+
            "LOCATION:location\n"+
            "ORGANIZER:mailto:max.mustermann@url.org\n"+
            "DURATION:P1D\n"+
            "RRULE:FREQ=YEARLY;BYMONTH=3;BYDAY=-1SU\n"+
            "RDATE:19610901T045612,19630901T045612\n"+
            "EXDATE:19710901T045612,19730901T045612\n"+
            "BEGIN:VALARM\n"+
            "TRIGGER:-PT5M\n"+
            "END:VALARM\n"+
            "BEGIN:VALARM\n"+
            "TRIGGER:-PT10M\n"+
            "END:VALARM\n"+
            "END:VEVENT\n"+
            "END:VCALENDAR");

    private static final String vcsAndroid = normalize("BEGIN:VCALENDAR\n"+
            "PRODID:jUnit-Tests\n"+
            "VERSION:2.0\n"+
            "CALSCALE:GREGORIAN\n"+
            "UID:3\n"+
            "BEGIN:VTIMEZONE\n"+
            "TZID:Australia/Sydney\n"+
            "TZURL:http://tzurl.org/zoneinfo/Australia/Sydney\n"+
            "X-LIC-LOCATION:Australia/Sydney\n"+
            "BEGIN:STANDARD\n"+
            "TZOFFSETFROM:+1100\n"+
            "TZOFFSETTO:+1000\n"+
            "TZNAME:AEST\n"+
            "DTSTART:20080406T030000\n"+
            "RRULE:FREQ=YEARLY;BYMONTH=4;BYDAY=1SU\n"+
            "END:STANDARD\n"+
            "BEGIN:DAYLIGHT\n"+
            "TZOFFSETFROM:+1000\n"+
            "TZOFFSETTO:+1100\n"+
            "TZNAME:AEDT\n"+
            "DTSTART:20081005T020000\n"+
            "RRULE:FREQ=YEARLY;BYMONTH=10;BYDAY=1SU\n"+
            "END:DAYLIGHT\n"+
            "BEGIN:STANDARD\n"+
            "TZOFFSETFROM:+100452\n"+
            "TZOFFSETTO:+1000\n"+
            "TZNAME:AEST\n"+
            "DTSTART:18950201T000000\n"+
            "RDATE:18950201T000000\n"+
            "END:STANDARD\n"+
            "BEGIN:DAYLIGHT\n"+
            "TZOFFSETFROM:+1000\n"+
            "TZOFFSETTO:+1100\n"+
            "TZNAME:AEDT\n"+
            "DTSTART:19170101T000100\n"+
            "RDATE:19170101T000100\n"+
            "RDATE:19420101T020000\n"+
            "RDATE:19420927T020000\n"+
            "RDATE:19431003T020000\n"+
            "RDATE:19711031T020000\n"+
            "RDATE:19721029T020000\n"+
            "RDATE:19731028T020000\n"+
            "RDATE:19741027T020000\n"+
            "RDATE:19751026T020000\n"+
            "RDATE:19761031T020000\n"+
            "RDATE:19771030T020000\n"+
            "RDATE:19781029T020000\n"+
            "RDATE:19791028T020000\n"+
            "RDATE:19801026T020000\n"+
            "RDATE:19811025T020000\n"+
            "RDATE:19821031T020000\n"+
            "RDATE:19831030T020000\n"+
            "RDATE:19841028T020000\n"+
            "RDATE:19851027T020000\n"+
            "RDATE:19861019T020000\n"+
            "RDATE:19871025T020000\n"+
            "RDATE:19881030T020000\n"+
            "RDATE:19891029T020000\n"+
            "RDATE:19901028T020000\n"+
            "RDATE:19911027T020000\n"+
            "RDATE:19921025T020000\n"+
            "RDATE:19931031T020000\n"+
            "RDATE:19941030T020000\n"+
            "RDATE:19951029T020000\n"+
            "RDATE:19961027T020000\n"+
            "RDATE:19971026T020000\n"+
            "RDATE:19981025T020000\n"+
            "RDATE:19991031T020000\n"+
            "RDATE:20000827T020000\n"+
            "RDATE:20011028T020000\n"+
            "RDATE:20021027T020000\n"+
            "RDATE:20031026T020000\n"+
            "RDATE:20041031T020000\n"+
            "RDATE:20051030T020000\n"+
            "RDATE:20061029T020000\n"+
            "RDATE:20071028T020000\n"+
            "END:DAYLIGHT\n"+
            "BEGIN:STANDARD\n"+
            "TZOFFSETFROM:+1100\n"+
            "TZOFFSETTO:+1000\n"+
            "TZNAME:AEST\n"+
            "DTSTART:19170325T020000\n"+
            "RDATE:19170325T020000\n"+
            "RDATE:19420329T020000\n"+
            "RDATE:19430328T020000\n"+
            "RDATE:19440326T020000\n"+
            "RDATE:19720227T030000\n"+
            "RDATE:19730304T030000\n"+
            "RDATE:19740303T030000\n"+
            "RDATE:19750302T030000\n"+
            "RDATE:19760307T030000\n"+
            "RDATE:19770306T030000\n"+
            "RDATE:19780305T030000\n"+
            "RDATE:19790304T030000\n"+
            "RDATE:19800302T030000\n"+
            "RDATE:19810301T030000\n"+
            "RDATE:19820404T030000\n"+
            "RDATE:19830306T030000\n"+
            "RDATE:19840304T030000\n"+
            "RDATE:19850303T030000\n"+
            "RDATE:19860316T030000\n"+
            "RDATE:19870315T030000\n"+
            "RDATE:19880320T030000\n"+
            "RDATE:19890319T030000\n"+
            "RDATE:19900304T030000\n"+
            "RDATE:19910303T030000\n"+
            "RDATE:19920301T030000\n"+
            "RDATE:19930307T030000\n"+
            "RDATE:19940306T030000\n"+
            "RDATE:19950305T030000\n"+
            "RDATE:19960331T020000\n"+
            "RDATE:19970330T020000\n"+
            "RDATE:19980329T020000\n"+
            "RDATE:19990328T020000\n"+
            "RDATE:20000326T020000\n"+
            "RDATE:20010325T020000\n"+
            "RDATE:20020331T020000\n"+
            "RDATE:20030330T020000\n"+
            "RDATE:20040328T020000\n"+
            "RDATE:20050327T020000\n"+
            "RDATE:20060402T030000\n"+
            "RDATE:20070325T020000\n"+
            "END:STANDARD\n"+
            "BEGIN:STANDARD\n"+
            "TZOFFSETFROM:+1000\n"+
            "TZOFFSETTO:+1000\n"+
            "TZNAME:AEST\n"+
            "DTSTART:19710101T000000\n"+
            "RDATE:19710101T000000\n"+
            "END:STANDARD\n"+
            "END:VTIMEZONE\n"+
            "BEGIN:VEVENT\n"+
            "UID:4711\n"+
            "DTSTART;TZID=Australia/Sydney:20000501T203456\n"+
            "DTEND;TZID=Australia/Sydney:20000502T011234\n"+
            "SUMMARY:test title\n"+
            "DESCRIPTION:bla bla bla\n"+
            "LOCATION:location\n"+
            "ORGANIZER:mailto:max.mustermann@url.org\n"+
            "DURATION:P1D\n"+
            "RRULE:FREQ=YEARLY;BYMONTH=3;BYDAY=-1SU\n"+
            "RDATE:19610901T045612,19630901T045612\n"+
            "EXDATE:19710901T045612,19730901T045612\n"+
            "BEGIN:VALARM\n"+
            "TRIGGER:-PT5M\n"+
            "END:VALARM\n"+
            "BEGIN:VALARM\n"+
            "TRIGGER:-PT10M\n"+
            "END:VALARM\n"+
            "END:VEVENT\n"+
            "END:VCALENDAR");

    @Test
    public void shouldBeSameMinimal() throws IOException, ParserException {
        EventFilterDto filter = EventFilterDto.MINIMAL;
        EventDtoSimple sut = new EventDtoSimple(src, filter);
        String result = getIcs(sut, filter);
        Assert.assertEquals(lastMininal, result);
    }
    @Test
    public void shouldBeSameDefault() throws IOException, ParserException {
        EventFilterDto filter = EventFilterDto.DEFAULTS;
        EventDtoSimple sut = new EventDtoSimple(src, filter);
        String result = getIcs(sut, filter);
        Assert.assertEquals(lastdefault, result);
    }

    @Test
    public void shouldBeSameMaximal() throws IOException, ParserException {
        final EventFilter filter = EventFilterDto.ALL;
        EventDtoSimple sut = new EventDtoSimple(src,filter);
        String result = getIcs(sut, filter);
        Assert.assertEquals(lastMaximal, result);
    }

    // vcsAndroid
    @Test
    public void shouldParseVcsAndroid() throws IOException, ParserException {
        final EventFilter filter = EventFilterDto.ALL;
        EventDtoSimple sut = new EventDtoSimple(src,filter);
        String result = getIcs(sut, filter);
        Assert.assertEquals(vcsAndroid, result);
    }

    @Test
    public void shouldBeSameFixThisEvent() throws IOException, ParserException {
        EventFilterDto filter = new EventFilterDto(EventFilterDto.MINIMAL).setRecurrenceType(EventFilter.RecurrenceType.ThisEvent);
        String result = getIcsWithDatePlusOneDay(filter);
        Assert.assertEquals(lastMininalThisEvent, result);
    }

    @Test
    public void shouldBeSameFixThisAndAllFurtherEvents() throws IOException, ParserException {
        EventFilterDto filter = new EventFilterDto(EventFilterDto.MINIMAL).setRecurrenceType(EventFilter.RecurrenceType.ThisAndAllFurtherEvents);
        String result = getIcsWithDatePlusOneDay(filter);
        Assert.assertEquals(lastMininalThisAndAllFurtherEvents, result);
    }

    @Test
    public void shouldBeSameFixAllEvents() throws IOException, ParserException {
        EventFilterDto filter = new EventFilterDto(EventFilterDto.MINIMAL).setRecurrenceType(EventFilter.RecurrenceType.AllEvents);
        String result = getIcsWithDatePlusOneDay(filter);
        Assert.assertEquals(lastMininalAllEvents, result);
    }

    private String getIcsWithDatePlusOneDay(final EventFilterDto filter) {
        EventDtoSimple sut = new EventDtoSimple(src, filter);
        return normalize(TestDataUtils.getIcs(filter, sut.getDtStart() + ONE_DAY_IN_MILLISECS, sut.getDtEnd() + ONE_DAY_IN_MILLISECS, sut));
    }

    private String getIcs(final EventDtoSimple sut, final EventFilter filter) {
        return normalize(TestDataUtils.getIcs(filter, sut));
    }

    private static String normalize(String s) {
        return s
                .replace("\r\n", "\n")
                .replace("\t", " ")
                .replace("  ", " ")
                .trim();
    }
}
