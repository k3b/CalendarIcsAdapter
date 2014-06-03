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

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import de.k3b.calendar.EventDto;
import de.k3b.calendar.EventDtoSimple;
import de.k3b.calendar.EventFilter;

/**
 * Let an android specific calendarEvent cursor appear as a EventDto.
 * common api for ACalendarCursorAsEventDto2 (for android2.x) and ACalendarCursorAsEventDto4 (for android4.x)
 *
 * @author k3b
 *
 */
public abstract class ACalendarCursorAsEventDto extends ACalendarCursor implements EventDto, Closeable {
    protected static final int col_DtStart = 1;
    protected static final int col_DtEnd = 2;
    protected static final int col_Title = 3;
    protected static final int col_Description = 4;
    protected static final int col_EventLocation = 5;
    protected static final int col_EventTimezone = 6;
    protected static final int col_Duration = 7;
    protected static final int col_RRule = 8;
    protected static final int col_RDate = 9;
    protected static final int col_Organizer = 10;
    protected static final int col_CalendarId = 11;
    protected static final int col_Alarm = 12;
    protected static final int col_ExtDates = 13;
    protected ReminderCursor reminderCursor;

    /**
     * Creates a datasource that uses the ContentResolver from context or mock database if not null.
     * mockimplementation is for testing with local copy of events database. This way real events are not at risc or you can test it on an
     * emulator with no calendar.<br/>
     * To use copy existing events database file (/data/data/com.android.provider.calendar/databases/calendar.db )
     * to local apps database folder ( /data/data/de.k3b.calendar.adapter/databases/calendar.db ) .<br/>
     */
    public ACalendarCursorAsEventDto(final Context ctx, final SQLiteDatabase mockDatabase,
                                     String... sqlColumnNames) {
        super(ctx, mockDatabase, sqlColumnNames);
    }

    /* (non-Javadoc)
 * @see de.k3b.calendar.adapter.EventData#getDtStart()
 */
    @Override
    public long getDtStart() {return currentCalendarContentDatabaseCursor.getLong(col_DtStart);}
    /* (non-Javadoc)
     * @see de.k3b.calendar.adapter.EventData#getDtEnd()
     */
    @Override
    public long getDtEnd() {return currentCalendarContentDatabaseCursor.getLong(col_DtEnd);}
    /* (non-Javadoc)
     * @see de.k3b.calendar.adapter.EventData#getTitle()
     */
    @Override
    public String getTitle() {return currentCalendarContentDatabaseCursor.getString(col_Title);}
    /* (non-Javadoc)
     * @see de.k3b.calendar.adapter.EventData#getDescription()
     */
    @Override
    public String getDescription() {return currentCalendarContentDatabaseCursor.getString(col_Description);}
    /* (non-Javadoc)
     * @see de.k3b.calendar.adapter.EventData#getEventLocation()
     */
    @Override
    public String getEventLocation() {return currentCalendarContentDatabaseCursor.getString(col_EventLocation);}
    /* (non-Javadoc)
     * @see de.k3b.calendar.adapter.EventData#getEventTimezone()
     */
    @Override
    public String getEventTimezone() {return currentCalendarContentDatabaseCursor.getString(col_EventTimezone);}
    /* (non-Javadoc)
     * @see de.k3b.calendar.adapter.EventData#getDuration()
     */
    @Override
    public String getDuration() {return currentCalendarContentDatabaseCursor.getString(col_Duration);}
    /* (non-Javadoc)
     * @see de.k3b.calendar.adapter.EventData#getRRule()
     */
    @Override
    public String getRRule() {return currentCalendarContentDatabaseCursor.getString(col_RRule);}

    @Override
    public String getRDate() {return currentCalendarContentDatabaseCursor.getString(col_RDate);}

    @Override
    public String getOrganizer() {return currentCalendarContentDatabaseCursor.getString(col_Organizer);}

    @Override
    public String getCalendarId() {return currentCalendarContentDatabaseCursor.getString(col_CalendarId);}

    /** #9 the alarm(s) should trigger x menutes before the event.
     * null means no alarms. This Method does not load the alarms */
    @Override
    public List<Integer> getAlarmMinutesBeforeEvent() {return (currentCalendarContentDatabaseCursor.getInt(col_Alarm) != 0) ? new ArrayList<Integer>():null;}

    /** #11 formatted as komma seperated list of iso-utc-dates. Example: '20090103T093000Z,20110101T093000Z' */
    @Override
    public String getExtDates() {
        return currentCalendarContentDatabaseCursor.getString(col_ExtDates);
    }

    // #9
    class ReminderCursor extends  ContentUriCursor {
        protected static final int col_MINUTES = 1;
        protected static final int col_EVENT_ID = 2;
        protected static final int col_METHOD = 3;

        public ReminderCursor(final Context ctx, final SQLiteDatabase mockDatabase,String... sqlColumnNames) {
            super(ctx, mockDatabase, sqlColumnNames);
        }

        public int getMinutes() {
            return currentCalendarContentDatabaseCursor.getInt(col_MINUTES);
        }

        String eventId;

        public Cursor queryByContentURI(final String eventId) {
            this.eventId = eventId;
            return super.queryByContentURI(ACalendarCursor.createContentUri("reminders"));
        }

        @Override
        protected Cursor queryByContentURI(Uri uri, String tableName,
                                           String sqlWhere, String... sqlWhereParameters) {
            sqlWhere = COLUMS[col_EVENT_ID] + "=? and " + COLUMS[col_METHOD] + "=?";

            return super.queryByContentURI(uri, tableName, sqlWhere
                    , this.eventId, Integer.toString(1));
        }
    }

    /************* #9 alarm-reminder ********************/
    /** #9 creates a copy of the data and downlownloads dependent subdata
     * @param filter*/
    public EventDto loadFull(final EventFilter filter) {
        EventDtoSimple data = new EventDtoSimple(this, filter);

        if (filter.getAlarms()) {
            this.addAlarms(data.getId(), data.getAlarmMinutesBeforeEvent());
        }
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
}
