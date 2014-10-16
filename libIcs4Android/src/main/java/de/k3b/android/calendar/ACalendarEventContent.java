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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import de.k3b.android.Global;
import de.k3b.android.sql.AndroidContentValuesBinder;
import de.k3b.android.sql.ContentUriCursor;
import de.k3b.calendar.EventDto;
import de.k3b.calendar.EventDtoSimple;
import de.k3b.calendar.EventFilter;
import de.k3b.sql.EventRowBinder;

/**
 * Let an android specific calendarEvent cursor appear as a EventDto.
 * common api for ACalendarEventContent2 (for android2.x) and ACalendarEventContent4 (for android4.x)
 *
 * @author k3b
 *
 */
public abstract class ACalendarEventContent extends ACalendarCursor implements Closeable {
    protected ReminderCursor reminderCursor;

    /**
     * Creates a datasource that uses the ContentResolver from context or mock database if not null.
     * mockimplementation is for testing with local copy of events database. This way real events are not at risc or you can test it on an
     * emulator with no calendar.<br/>
     * To use copy existing events database file (/data/data/com.android.provider.calendar/databases/calendar.db )
     * to local apps database folder ( /data/data/de.k3b.calendar.adapter/databases/calendar.db ) .<br/>
     */
    public ACalendarEventContent(final Context ctx, final SQLiteDatabase mockDatabase,
                                 String... sqlColumnNames) {
        super(ctx, mockDatabase, sqlColumnNames);
        if ((sqlColumnNames == null) || (sqlColumnNames.length < EventRowBinder.COLUMN_COUNT)) {
            throw new IllegalArgumentException("String[] sqlColumnNames must match EventRowBinder");
        }
    }

    /// TODO implement insert/update/delete
    public ContentValues createValues(EventDto src, EventFilter filter) {
        ContentValues values = new ContentValues();
        AndroidContentValuesBinder columnBinder = new AndroidContentValuesBinder(values, this.COLUMS);

        EventRowBinder rowBinder = new EventRowBinder(columnBinder);
        rowBinder.bind(src, filter);

        return values;
    }

    // #9
    class ReminderCursor extends ContentUriCursor {
        private static final int col_MINUTES = 1;
        private static final int col_EVENT_ID = 2;
        private static final int col_METHOD = 3;

        public ReminderCursor(final Context ctx, final SQLiteDatabase mockDatabase,String... sqlColumnNames) {
            super(ctx, mockDatabase, sqlColumnNames);
        }

        public int getMinutes() {
            return (int) this.columnBinder.getLong(col_MINUTES);
        }

        String eventId;

        public Cursor queryByContentURI(final String eventId) {
            this.eventId = eventId;
            return super.queryByContentURI(ACalendarCursor.createContentUri("reminders"));
        }

        private String[] getSqlWhere(StringBuilder result, String eventId, List<String> minutes) {
            return getSqlWhere(result, eventId, minutes.toArray(new String[minutes.size()]));
        }

        private String[] getSqlWhere(StringBuilder result, String eventId, String... minutes) {
            ArrayList<String> parameters = new ArrayList<String>();

            result.append(COLUMS[col_EVENT_ID]).append("=? and ").append(COLUMS[col_METHOD]).append("=?");
            parameters.add(eventId);
            parameters.add(Integer.toString(1));

            if ((minutes != null) && (minutes.length > 0)) {
                result.append(" and ").append(COLUMS[col_MINUTES]).append(" in (?");
                parameters.add(minutes[0]);

                for (int i=1;i<minutes.length;i++) {
                    result.append(",?");
                    parameters.add(minutes[i]);
                }
                result.append(") ");
            }
            return parameters.toArray(new String[parameters.size()]);
        }

        @Override
        protected Cursor queryByContentURI(Uri uri,
                                           String sqlWhere, String... sqlWhereParameters) {
            StringBuilder newSqlWhere = new StringBuilder();
            String[] params = getSqlWhere(newSqlWhere, eventId);
            return super.queryByContentURI(uri, newSqlWhere.toString()
                    , params);
        }

        /** #9 downloads dependant data */
        public List<Integer> getAlarms(final String eventId, final List<Integer> alarmMinutesBeforeEventResult) {
            if (alarmMinutesBeforeEventResult != null) {
                if (Global.debugEnabled) {
                    Log.d(ACalendar2IcsEngine.TAG, "Downloading Reminders for Event " + eventId);
                }

                Cursor cursor = null;
                try {
                    // set to null for non mocked production
                    cursor = queryByContentURI(eventId);

                    if (cursor != null) {
                        // Use the cursor to step through the returned records
                        while (cursor.moveToNext()) {
                            alarmMinutesBeforeEventResult.add(getMinutes());
                        }
                    }
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
            } else {
                if (Global.debugEnabled) {
                    Log.d(ACalendar2IcsEngine.TAG, "No Reminders for Event " + eventId);
                }
            }
            return alarmMinutesBeforeEventResult;
        }

        /** return all items that are in allItems but not in subtractItems. diff({12345},{34567}) => {1,2} */
        private List<String> diff(final List<Integer> allItems, final List<Integer> subtractItems) {
            final List<String> result = new ArrayList<String>();

            for(Integer cur : allItems) {
                if (!subtractItems.contains(cur)) {
                    result.add(cur.toString());
                }
            }

            return result;
        }

        public void updateAlarms(final Uri uri, final String eventId, final List<Integer> newAlarms) {
            final List<Integer> currentAlarms = getAlarms(eventId, new ArrayList<Integer>());

            final List<String> deleteMinutes = diff(currentAlarms, newAlarms);
            final List<String> insertMinutes = diff(newAlarms, currentAlarms);
            deleteAlarms(uri, eventId, deleteMinutes);
            insertAlarms(uri, eventId, insertMinutes);
        }

        public void deleteAlarms(final Uri uri, final String eventId, final List<String> minutes) {
            if (minutes.size() > 0) {
                StringBuilder sqlWhere = new StringBuilder();
                String[] params = getSqlWhere(sqlWhere, eventId, minutes);
                executeDelete(uri,sqlWhere.toString(), params);
            }
        }

        public void insertAlarms(final Uri uri, final String eventId, final List<String> minutes) {
            if (minutes.size() > 0) {
                ContentValues values = new ContentValues();
                values.put(COLUMS[col_EVENT_ID], eventId);
                values.put(COLUMS[col_METHOD], "1");
                for (String minute : minutes) {
                    values.put(COLUMS[col_MINUTES], "1");
                    executeInsert(uri, values);
                }
            }
        }
    }

    /************* #9 alarm-reminder ********************/
    /** #9 creates a copy of the data and downlownloads dependent subdata
     * @param filter*/
    public EventDto loadFull(final EventFilter filter) {
        EventDtoSimple data = new EventDtoSimple(new EventRowBinder(columnBinder), filter);

        if (filter.getAlarms()) {
            this.reminderCursor.getAlarms(data.getId(), data.getAlarmMinutesBeforeEvent());
        }
        return data;
    }

}
