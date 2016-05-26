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

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.Date;

import de.k3b.calendar.ics.IcsAsEventDto;
import de.k3b.util.DateTimeUtil;

/**
 * Tests the conversion from EventDto to iCal4j-ics-event and back to EventDto.
 * Created by k3b on 29.05.2014.
 */
public class DtoIcsDtoTests {
    private final Date xmasNoonDate = TestDataUtils.createDateCET(2000, 12, 24, 12,0,0);
    private final String xmasNoonString = "20001224T120000Z";

    /** controls, wich data elements will be exported. */
    private final EventFilter filter = EventFilterDto.ALL;

    private EventDtoSimple src = null;
    private String lastIcs;

    @Before
    public void setup() {
        src = TestDataUtils.createTestEventDto();
    }

    @Test
    public void shouldConvertDtStart() throws IOException, ParserException {
        src.setDtStart(xmasNoonDate.getTime());
        EventDto result = executeTest(src);
        Assert.assertEquals(this.lastIcs, DateTimeUtil.toIsoDate(src.getDtStart()), DateTimeUtil.toIsoDate(result.getDtStart()));
    }

    @Test
    public void shouldConvertDtEnd() throws IOException, ParserException {
        src.setDtEnd(xmasNoonDate.getTime());
        EventDto result = executeTest(src);
        Assert.assertEquals(this.lastIcs, DateTimeUtil.toIsoDate(src.getDtEnd()), DateTimeUtil.toIsoDate(result.getDtEnd()));
    }

    @Test
    public void shouldConvertId() throws IOException, ParserException {
        src.setId("some value");
        EventDto result = executeTest(src);
        Assert.assertEquals(this.lastIcs, src.getId(), result.getId());
    }

    @Test
    public void shouldConvertCalendarId() throws IOException, ParserException {
        src.setCalendarId("some value");
        EventDto result = executeTest(src);
        Assert.assertEquals(this.lastIcs, src.getCalendarId(), result.getCalendarId());
    }

    @Test
    public void shouldConvertDescription() throws IOException, ParserException {
        src.setDescription("some value");
        EventDto result = executeTest(src);
        Assert.assertEquals(this.lastIcs, src.getDescription(), result.getDescription());
    }

    @Test
    public void shouldConvertDuration() throws IOException, ParserException {
        src.setDuration("PT1H");
        EventDto result = executeTest(src);
        Assert.assertEquals(this.lastIcs, src.getDuration(), result.getDuration());
    }

    @Test
    public void shouldConvertEventLocation() throws IOException, ParserException {
        src.setEventLocation("some value");
        EventDto result = executeTest(src);
        Assert.assertEquals(this.lastIcs, src.getEventLocation(), result.getEventLocation());
    }

    @Test
    public void shouldConvertOrganizer() throws IOException, ParserException {
        src.setOrganizer("mailto:theo.test@url.org");
        EventDto result = executeTest(src);
        Assert.assertEquals(this.lastIcs, src.getOrganizer(), result.getOrganizer());
    }

    @Test
    public void shouldConvertRDate() throws IOException, ParserException {
        src.setRDate(xmasNoonString);
        EventDto result = executeTest(src);
        Assert.assertEquals(this.lastIcs, src.getRDate(), result.getRDate());
    }

    @Test
    public void shouldConvertRRule() throws IOException, ParserException {
        src.setRRule("FREQ=WEEKLY;WKST=SU;UNTIL=20141007T000000Z;BYDAY=TU,TH");
        EventDto result = executeTest(src);
        Assert.assertEquals(this.lastIcs, src.getRRule(), result.getRRule());
    }

    @Test
    public void shouldConvertTitle() throws IOException, ParserException {
        src.setTitle("some value");
        EventDto result = executeTest(src);
        Assert.assertEquals(this.lastIcs, src.getTitle(), result.getTitle());
    }

    @Test
    public void shouldConvertExtDates() throws IOException, ParserException {
        src.setExtDates(xmasNoonString + "," + xmasNoonString);
        EventDto result = executeTest(src);
        Assert.assertEquals(this.lastIcs, src.getExtDates(), result.getExtDates());
    }

    @Test
    public void shouldConvertAlarmMinutesBeforeEvent() throws IOException, ParserException {
        src.setAlarmMinutesBeforeEvent(1, 2, 5, 10);
        EventDto result = executeTest(src);
        Assert.assertEquals(this.lastIcs, src.getAlarmMinutesBeforeEvent(), result.getAlarmMinutesBeforeEvent());
    }

    @Test
    public void shouldConvertTimeZone() throws IOException, ParserException {
        src.setEventTimezone("Europe/Berlin");
        EventDto result = executeTest(src);
        Assert.assertEquals(this.lastIcs, src.getEventTimezone(), result.getEventTimezone());
    }


    @Test
    public void shouldNotHave2IdenticalTimeZones() throws IOException, ParserException {
        EventDtoSimple src2 = new EventDtoSimple()
                .setId("4712")
                .setCalendarId(src.getCalendarId())
                .setTitle("test title2")
                .setDtStart(TestDataUtils.createDateCET(2001, 5, 1, 12, 34, 56).getTime())
                .setDtEnd(TestDataUtils.createDateCET(2001, 5, 1, 17, 12, 34).getTime())
                .setEventTimezone(src.getEventTimezone());

        this.lastIcs = TestDataUtils.getIcs(filter, src, src2);

        Calendar vcalendar = getVCalendar(this.lastIcs);

        Assert.assertEquals(this.lastIcs, 3, vcalendar.getComponents().size());
    }


    /** local helper to executeTest dto -> ics -> dto */
    private EventDto executeTest(final EventDtoSimple src) throws IOException, ParserException {
        String ics = TestDataUtils.getIcs(filter, src);
        this.lastIcs = ics;
        return getDto(ics);
    }

    private EventDto getDto(final String ics) throws IOException, ParserException {
        Calendar vcalendar = getVCalendar(ics);
        return new EventDtoSimple(new IcsAsEventDto(vcalendar));
    }

    private Calendar getVCalendar(final String ics) throws IOException, ParserException {
        CalendarBuilder cb = new CalendarBuilder();
        StringReader reader = new StringReader(ics);
        return cb.build(reader);
    }

}
