/*
 * Copyright (C) 2014- k3b
 * 
 * This file is part of CalendarIcsAdapter.
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
package org.dgtale.android.calendar;

import java.util.Date;
import java.util.List;

import org.dgtale.android.compat.CalendarContract;

import edu.emory.mathcs.backport.java.util.Arrays;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * Baseclass for cursor based dataaccess via content-uri.<br/>
 * It can used with contentResolver or with a local (mock)database that simulates the contentProviderAPI.<br/><br/>
 * @author k3b
 */
public abstract class ContentUriCursor {
	protected static String selectionById = "(" + CalendarContract.EventsColumns._ID + " = ? )";
	protected Cursor cur = null;
	protected ContentResolver contentResolver = null;
	protected SQLiteDatabase mockDatabase = null;

	private List<String> contentToTablePlurals;

	/**
	 * Creates a datasource that uses the ContentResolver from context
	 */
	public ContentUriCursor(Context ctx) {
		this.contentResolver = ctx.getContentResolver();
	}
	
	/**
	 * Creates a datasource that uses a
	 * mockimplementation for testing with local copy of events database. This way real events are not at risc or you can test it on an 
	 * emulator with no calendar.<br/>
	 * To use copy existing events database file (/data/data/com.android.provider.calendar/databases/calendar.db ) 
	 * to local apps database folder ( /data/data/org.dgtale.calendar.adapter/databases/calendar.db ) .<br/>
	 */
	@SuppressWarnings("unchecked")
	public ContentUriCursor(SQLiteDatabase mockDatabase, String... contentToTablePlurals) {
		this.mockDatabase = mockDatabase;
		this.contentToTablePlurals = Arrays.asList(contentToTablePlurals);
	}

	public void close() {
		if (cur != null) {
			cur.close();
			cur = null;
		}
		contentResolver = null;
		
		if (mockDatabase != null) {
			mockDatabase.close();
		}
	}
	
	/**
	 * gets the colums that belong to this ContentUriCursor.
	 * col 0 should be "_id"
	 */
	abstract protected String[] getColums();	


	/**
	 * @param uri i.e. "content://com.adnroid.calendar/events/608" for event with _id=608.
	 * @return opend cursor that must be closed by caller
	 */
	public Cursor getByContentURI(Uri uri) throws IllegalArgumentException {
		if (uri == null) throw new IllegalArgumentException("ContentURI must not be null");
		List<String> uriSegments = uri.getPathSegments();
		String uriAsString = uri.toString();
		if ((uriSegments.size() < 1) || (!uriAsString.startsWith("content:"))) {
			throw new IllegalArgumentException("ContentURI expected content://com.adnroid.calendar/{TABLE}[/{EVENTID}] but was " + uriAsString);
		}
		
		String tableName = uriSegments.get(0);
		if ((contentToTablePlurals != null) && contentToTablePlurals.contains(tableName)) {
			tableName += "s";
		}
		String id = (uriSegments.size() == 1) ? null : uriSegments.get(1);

		String sqlWhere = (id == null) ? null : selectionById; // all or one certain item
		String[] sqlWhereparameters = (id == null) ? null : new String[] {id};

		return getByContentURI(uri, tableName, sqlWhere, sqlWhereparameters);
	}

	protected Cursor getByContentURI(Uri uri, String tableName,
			String sqlWhere, String[] sqlWhereparameters) {
		if (contentResolver != null) {
			cur = contentResolver.query(uri, getColums(), sqlWhere, sqlWhereparameters, null);
		} else {
			cur = this.mockDatabase.query(tableName, getColums(), sqlWhere, sqlWhereparameters, null,null,null);			
		}
		return cur;
	}

	public String getId() {return cur.getString(0);}

	protected Date getDateTime(int columnIndex) {
		long ticks = cur.getLong(columnIndex);
		return (ticks == 0) ? null : new Date(ticks);
	}
}
