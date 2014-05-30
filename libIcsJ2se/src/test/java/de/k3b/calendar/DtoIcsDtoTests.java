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

import de.k3b.util.DateTimeUtil;

/**
 * Tests the conversion from EventDto to iCal4j-ics-event and back to EventDto.
 * Created by k3b on 29.05.2014.
 */
public class DtoIcsDtoTests {
    private final Date xmasNoonDate = DateTimeUtil.createDate(2000, 12, 24, 12,0,0);
    private final String xmasNoonString = "20001224T120000Z";

    private EventDtoSimple src = null;
    private String lastIcs;

    @Before
    public void setup() {
        src = new EventDtoSimple()
                .setId("4711")
                .setCalendarId("3")
                .setTitle("test title")
                .setDtStart(DateTimeUtil.createDate(2000, 5, 1, 12,34,56).getTime())
                .setDtEnd(DateTimeUtil.createDate(2000, 5, 1, 17,12,34).getTime())
				.setEventLocation("location")
				.setOrganizer("mailto:max.mustermann@url.org")
				.setDuration("P1D")
				.setRDate("19610901T045612Z,19630901T045612Z")
                .setExtDates("19710901T045612Z,19730901T045612Z")
                .setEventTimezone("Australia/Sydney")
				.setRRule("FREQ=YEARLY;BYMONTH=3;BYDAY=-1SU");
    }

    @Test
    public void showIcs() throws IOException, ParserException {
        System.out.println(this.getIcs(src));
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
        src.setExtDates(xmasNoonString+","+xmasNoonString);
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

    /** local helper to executeTest dto -> ics -> dto */
    private EventDto executeTest(final EventDtoSimple src) throws IOException, ParserException {
        String ics = getIcs(src);
        this.lastIcs = ics;
        return getDto(ics);
    }

    private EventDto getDto(final String ics) throws IOException, ParserException {
        CalendarBuilder cb = new CalendarBuilder();
        StringReader reader = new StringReader(ics);
        Calendar vcalendar = cb.build(reader);
        return new EventDtoSimple(new IcsAsEventDto(vcalendar));
    }

    private String getIcs(final EventDto... events) {
        EventDto2IcsFactory dto2Ics = new EventDto2IcsFactory("jUnit-Tests");
        if (events != null) {
            for(EventDto event : events) {
                dto2Ics.addEvent(event);
            }
        }
        return dto2Ics.getCalendar().toString();
    }

}
