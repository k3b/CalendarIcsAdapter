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
package de.k3b.android.calendar;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.provider.CalendarContract;

/**
 * Facade that make a android-calendar-event-cursor appear as EventDto.
 * To load data with depenent data use loadFull().
 * <br/>
 * Note: ACalendarEventContent2 for android2.x and ACalendarEventContent4 for android4.x are identical, except
 * that 2.x uses de.k3b.android.compat.CalendarContract and 4.x uses android.provider.CalendarContract;
 * 
 * @author k3b
 */
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class ACalendarEventContent4 extends ACalendarEventContent {
    /**
     * Creates a datasource that uses the ContentResolver from context or mock database if not null.
     * mockimplementation is for testing with local copy of events database. This way real events are not at risc or you can test it on an
     * emulator with no calendar.<br/>
     * To use copy existing events database file (/data/data/com.android.provider.calendar/databases/calendar.db )
     * to local apps database folder ( /data/data/de.k3b.calendar.adapter/databases/calendar.db ) .<br/>
     */
    public ACalendarEventContent4(final Context ctx, final SQLiteDatabase mockDatabase) {
        super(ctx, mockDatabase,
                CalendarContract.Events._ID,
                CalendarContract.Events.DTSTART,
                CalendarContract.Events.DTEND,
                CalendarContract.Events.TITLE,
                CalendarContract.Events.DESCRIPTION,
                CalendarContract.Events.EVENT_LOCATION,
                CalendarContract.Events.EVENT_TIMEZONE,
                CalendarContract.Events.DURATION,
                CalendarContract.Events.RRULE,
                CalendarContract.Events.RDATE,
                CalendarContract.Events.ORGANIZER,
                CalendarContract.Events.CALENDAR_ID,
                CalendarContract.Events.HAS_ALARM,
                CalendarContract.Events.EXDATE);

        this.reminderCursor = new ReminderCursor(ctx, mockDatabase,
                CalendarContract.Reminders._ID,
                CalendarContract.Reminders.MINUTES,
                CalendarContract.Reminders.EVENT_ID,
                CalendarContract.Reminders.METHOD);
    }
}
