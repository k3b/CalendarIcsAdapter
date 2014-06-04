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
package de.k3b.android.sql;

import java.io.Closeable;
import java.util.Date;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import de.k3b.android.Global;
import de.k3b.sql.ColumnBinder;

/**
 * Baseclass for cursor based dataaccess via content-uri.<br/>
 * It can used with calendarContentResolver or with a local (mock)database that simulates the contentProviderAPI.<br/><br/>
 * 
 * @author k3b
 */
public abstract class ContentUriCursor implements Closeable {
    protected static final int col_ID = 0;
    private static final String TAG = "k3b-sql";

    // collumn names must match order of the consts col_XXX.
    protected final String[] COLUMS;

	private ContentResolver calendarContentResolver = null;
	private SQLiteDatabase mockedCalendarContentDatabase = null;

    protected Cursor currentCalendarContentDatabaseCursor = null;

    protected ColumnBinder columnBinder = null;

	/**
	 * Creates a datasource that uses the ContentResolver from context or mock database if not null.
     * mockimplementation is for testing with local copy of events database. This way real events are not at risc or you can test it on an
     * emulator with no calendar.<br/>
     * To use copy existing events database file (/data/data/com.android.provider.calendar/databases/calendar.db )
     * to local apps database folder ( /data/data/de.k3b.calendar.adapter/databases/calendar.db ) .<br/>
	 */
	public ContentUriCursor(Context ctx, SQLiteDatabase mockDatabase,String... sqlColumnNames) {
		this.calendarContentResolver = (mockDatabase == null) ? ctx.getContentResolver() : null;
        this.mockedCalendarContentDatabase = mockDatabase;
        COLUMS = sqlColumnNames;
    }
	
	/**
	 * frees all allocated resources
	 */
	public void close() {
        columnBinder = null;
		if (currentCalendarContentDatabaseCursor != null) {
			currentCalendarContentDatabaseCursor.close();
			currentCalendarContentDatabaseCursor = null;
		}
		calendarContentResolver = null;
		
		if (mockedCalendarContentDatabase != null) {
			mockedCalendarContentDatabase.close();
		}
	}

    protected String getSqlFilterToFindById() { return "(" + COLUMS[col_ID]  + " = ? )"; };

	/**
	 * @param uri i.e. "content://com.adnroid.calendar/events/608" for event with _id=608.
	 * @return opend cursor that must be closed by caller
	 */
	public Cursor queryByContentURI(Uri uri) throws IllegalArgumentException {
		if (uri == null) throw new IllegalArgumentException("ContentURI must not be null");
		List<String> uriSegments = uri.getPathSegments();
		String uriAsString = uri.toString();
		if ((uriSegments.size() < 1) || (!uriAsString.startsWith("content:"))) {
			throw new IllegalArgumentException("ContentURI expected content://" +
					"com.android.{providertype}" +
					"/{TABLE}[/{EVENTID}] but was " + uriAsString);
		}
		
		String tableName = uriSegments.get(0);
		String id = (uriSegments.size() == 1) ? null : uriSegments.get(1);

		String sqlWhere = (id == null) ? null : getSqlFilterToFindById(); // all or one certain item
		String[] sqlWhereparameters = (id == null) ? null : new String[] {id};

		return queryByContentURI(uri, tableName, sqlWhere, sqlWhereparameters);
	}

	/**
	 * Local Query executor that queries either the ContentResolver or the mocked database
	 */
	protected Cursor queryByContentURI(Uri uri, String tableName,
			String sqlWhere, String... sqlWhereParameters) {
        if (Global.debugEnabled) {
            StringBuilder debugMessage = new StringBuilder()
                    .append("queryByContentURI(uri='").append(uri)
                    .append("', tableName='").append(tableName)
                    .append("', sqlWhere='").append(sqlWhere)
                    .append("', params=");
            if (sqlWhereParameters != null) {
                for(String p:sqlWhereParameters) {
                    debugMessage.append("'") .append(p).append("', ");
                }
            }
            debugMessage.append(")");
            Log.d(TAG, debugMessage.toString());
        }

        if (calendarContentResolver != null) {
			currentCalendarContentDatabaseCursor = calendarContentResolver.query(uri, COLUMS, sqlWhere, sqlWhereParameters, null);
		} else {
			currentCalendarContentDatabaseCursor = this.mockedCalendarContentDatabase.query(tableName, COLUMS, sqlWhere, sqlWhereParameters, null,null,null);
		}
        this.columnBinder = new AndroidCursorBinder(currentCalendarContentDatabaseCursor);
		return currentCalendarContentDatabaseCursor;
	}

	public String getId() {return columnBinder.getString(0);}

	protected Date getDateTime(int columnIndex) {
		long ticks = columnBinder.getLong(columnIndex);
		return (ticks == 0) ? null : new Date(ticks);
	}
}
