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

    public static String getIcs(final EventFilter filter, final EventDto... events) {
        return getIcs(filter, 0,0, events);
    }

    public static String getIcs(final EventFilter filter, long dtStart, long dtEnd, final EventDto... events) {

        EventDto2IcsFactory dto2Ics = new EventDto2IcsFactory(filter, "jUnit-Tests");
        if (events != null) {
            for(EventDto event : events) {
                dto2Ics.addEvent(event, dtStart, dtEnd);
            }
        }
        return dto2Ics.getCalendar().toString();
    }
}
