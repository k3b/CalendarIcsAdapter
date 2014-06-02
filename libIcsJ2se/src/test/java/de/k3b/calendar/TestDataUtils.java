package de.k3b.calendar;

import de.k3b.calendar.ics.EventDto2IcsFactory;
import de.k3b.util.DateTimeUtil;

/**
 * Created by EVE on 02.06.2014.
 */
public class TestDataUtils {
    public static EventDtoSimple createTestEventDto() {
        return new EventDtoSimple()
                .setId("4711")
                .setCalendarId("3")
                .setDescription("bla bla bla")
                .setTitle("test title")
                .setDtStart(DateTimeUtil.createDate(2000, 5, 1, 12, 34, 56).getTime())
                .setDtEnd(DateTimeUtil.createDate(2000, 5, 1, 17,12,34).getTime())
                .setEventLocation("location")
                .setOrganizer("mailto:max.mustermann@url.org")
                .setDuration("P1D")
                .setRDate("19610901T045612Z,19630901T045612Z")
                .setExtDates("19710901T045612Z,19730901T045612Z")
                .setEventTimezone("Australia/Sydney")
                .setRRule("FREQ=YEARLY;BYMONTH=3;BYDAY=-1SU")
                .setAlarmMinutesBeforeEvent(5,10);
    }

    public static String getIcs(final EventDto... events) {
        EventDto2IcsFactory dto2Ics = new EventDto2IcsFactory("jUnit-Tests");
        if (events != null) {
            for(EventDto event : events) {
                dto2Ics.addEvent(event);
            }
        }
        return dto2Ics.getCalendar().toString();
    }
}
