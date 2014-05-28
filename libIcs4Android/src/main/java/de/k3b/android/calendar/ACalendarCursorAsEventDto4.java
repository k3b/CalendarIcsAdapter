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

import de.k3b.calendar.EventDto;
import de.k3b.calendar.EventDtoSimple;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.provider.CalendarContract;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Facade that make a android-calendar-event-cursor appear as EventDto.
 * To load data with depenent data use loadFull().
 * <br/>
 * Note: ACalendarCursorAsEventDto2 for android2.x and ACalendarCursorAsEventDto4 for android4.x are identical, except
 * that 2.x uses de.k3b.android.compat.CalendarContract and 4.x uses android.provider.CalendarContract;
 * 
 * @author k3b
 */
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class ACalendarCursorAsEventDto4 extends ACalendarCursor implements ACalendarCursorAsEventDto { 
    private ReminderCursor reminderCursor;
    /**
     * Creates a datasource that uses the ContentResolver from context
     */
    public ACalendarCursorAsEventDto4(Context ctx) {
        super(ctx);
        reminderCursor = new ReminderCursor(ctx);
    }

    /**
     * Creates a datasource that uses a
     * mockimplementation for testing with local copy of events database. This way real events are not at risc or you can test it on an
     * emulator with no calendar.<br/>
     * To use copy existing events database file (/data/data/com.android.provider.calendar/databases/calendar.db )
     * to local apps database folder ( /data/data/de.k3b.calendar.adapter/databases/calendar.db ) .<br/>
     */
    public ACalendarCursorAsEventDto4(SQLiteDatabase mockDatabase) {
        super(mockDatabase);
        reminderCursor = new ReminderCursor(mockDatabase);
    }

    /**
     * gets the colums that belong to this ContentUriCursor
     */
    @Override
    protected String[] getColums() { return COLUMS; }

    // collumn names must match order in the getters below.
    // Warning: Adding further colums might break android 2.1 compatiblity.
    // These 11 colums where found in my android2.2 calendar-events-table.
    // See ACalendarMock.onCreate() for a list of android2.2 calendar-events columns
    private final String[] COLUMS = new String[] {
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
            CalendarContract.Events.EXDATE, // #11
    };

    /* (non-Javadoc)
     * @see de.k3b.calendar.adapter.EventData#getDtStart()
     */
    @Override
    public long getDtStart() {return currentCalendarContentDatabaseCursor.getLong(1);}
    /* (non-Javadoc)
     * @see de.k3b.calendar.adapter.EventData#getDtEnd()
     */
    @Override
    public long getDtEnd() {return currentCalendarContentDatabaseCursor.getLong(2);}
    /* (non-Javadoc)
     * @see de.k3b.calendar.adapter.EventData#getTitle()
     */
    @Override
    public String getTitle() {return currentCalendarContentDatabaseCursor.getString(3);}
    /* (non-Javadoc)
     * @see de.k3b.calendar.adapter.EventData#getDescription()
     */
    @Override
    public String getDescription() {return currentCalendarContentDatabaseCursor.getString(4);}
    /* (non-Javadoc)
     * @see de.k3b.calendar.adapter.EventData#getEventLocation()
     */
    @Override
    public String getEventLocation() {return currentCalendarContentDatabaseCursor.getString(5);}
    /* (non-Javadoc)
     * @see de.k3b.calendar.adapter.EventData#getEventTimezone()
     */
    @Override
    public String getEventTimezone() {return currentCalendarContentDatabaseCursor.getString(6);}
    /* (non-Javadoc)
     * @see de.k3b.calendar.adapter.EventData#getDuration()
     */
    @Override
    public String getDuration() {return currentCalendarContentDatabaseCursor.getString(7);}
    /* (non-Javadoc)
     * @see de.k3b.calendar.adapter.EventData#getRRule()
     */
    @Override
    public String getRRule() {return currentCalendarContentDatabaseCursor.getString(8);}

    @Override
    public String getRDate() {return currentCalendarContentDatabaseCursor.getString(9);}

    @Override
    public String getOrganizer() {return currentCalendarContentDatabaseCursor.getString(10);}

    @Override
    public String getCalendarId() {return currentCalendarContentDatabaseCursor.getString(11);}

    /** #9 the alarm(s) should trigger x menutes before the event.
     * null means no alarms. This Method does not load the alarms */
    @Override
    public List<Integer> getAlarmMinutesBeforeEvent() {return (currentCalendarContentDatabaseCursor.getInt(12) != 0) ? new ArrayList<Integer>():null;}

    /** #11 formatted as komma seperated list of iso-utc-dates. Example: '20090103T093000Z,20110101T093000Z' */
    @Override
    public String getExtDates() {
        return currentCalendarContentDatabaseCursor.getString(13);
    }

    /************* #9 alarm-reminder ********************/
    /** #9 creates a copy of the data and downlownloads dependent subdata */
    @Override
    public EventDto loadFull() {
        EventDtoSimple data = new EventDtoSimple(this);
        this.addAlarms(data.getId(), data.getAlarmMinutesBeforeEvent());
        return data;
    }

    /** #9 downloads dependant data */
    private void addAlarms(final String eventId, final List<Integer> alarmMinutesBeforeEvent) {
        if (alarmMinutesBeforeEvent != null) {
            if (Global.debugEnabled) {
                Log.d(ACalendar2IcsEngine.TAG, "Downloading Reminders for Event " + eventId);
            }

            Cursor eventCursor = null;
            try {
                // set to null for non mocked production
                eventCursor = reminderCursor.queryByContentURI(eventId);

                if (eventCursor != null) {
                    // Use the cursor to step through the returned records
                    while (eventCursor.moveToNext()) {
                        alarmMinutesBeforeEvent.add(reminderCursor.getMinutes());
                    }
                }
            } finally {
                if (eventCursor != null) {
                    eventCursor.close();
                }
            }
        } else {
            if (Global.debugEnabled) {
                Log.d(ACalendar2IcsEngine.TAG, "No Reminders for Event " + eventId);
            }
        }
    }

	// #9
    private class ReminderCursor extends  ContentUriCursor {

        public ReminderCursor(final Context ctx) {
            super(ctx);
        }
        public ReminderCursor(final SQLiteDatabase mockDatabase) {
            super(mockDatabase);
        }

        // collumn names must match order in the getters below.
        // Warning: Adding further colums might break android 2.1 compatiblity.
        private final String[] COLUMS = new String[]{
                CalendarContract.Reminders._ID,
                CalendarContract.Reminders.MINUTES,
//              CalendarContract.Reminders.EVENT_ID,
//              CalendarContract.Reminders.METHOD
        };

        @Override
        protected String[] getColums() {
            return COLUMS;
        }

        public int getMinutes() {
            return currentCalendarContentDatabaseCursor.getInt(1);
        }

        String eventId;

        public Cursor queryByContentURI(final String eventId) {
            this.eventId = eventId;
            return super.queryByContentURI(CalendarContract.Reminders.CONTENT_URI);
        }

        @Override
        protected Cursor queryByContentURI(Uri uri, String tableName,
                                           String sqlWhere, String... sqlWhereParameters) {
            sqlWhere = CalendarContract.Reminders.EVENT_ID + "=? and " + CalendarContract.Reminders.METHOD + "=?";

            return super.queryByContentURI(uri, tableName, sqlWhere
                    , this.eventId, Integer.toString(1));
        }
    }
}
