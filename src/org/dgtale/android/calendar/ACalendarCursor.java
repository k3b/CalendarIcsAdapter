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

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * Baseclass for all Android Calendar-Provider-Cursors.<br/><br/>
 * @author k3b
 */
public abstract class ACalendarCursor extends ContentUriCursor {
	protected static String providerAutority = "com.android.calendar"; // uri of content provider. my differ with android version below 4.0

	/**
	 * Creates a datasource that uses the ContentResolver from context
	 */
	public ACalendarCursor(Context ctx) {
		super(ctx);
	}
	
	/**
	 * Creates a datasource that uses a
	 * mockimplementation for testing with local copy of events database. This way real events are not at risc or you can test it on an 
	 * emulator with no calendar.<br/>
	 * To use copy existing events database file (/data/data/com.android.provider.calendar/databases/calendar.db ) 
	 * to local apps database folder ( /data/data/org.dgtale.calendar.adapter/databases/calendar.db ) .<br/>
	 */
	public ACalendarCursor(SQLiteDatabase mockDatabase) {
		super(mockDatabase,"calendar", "event");
	}
	
	public static Uri createContentUri(String... urlParts) {
		StringBuffer uri = new StringBuffer("content://" + providerAutority);
		for(String urlPart : urlParts) {
			uri.append("/").append(urlPart);
		}
		return Uri.parse(uri.toString());
	}
	
	/**
	 * @param uri i.e. "content://com.adnroid.calendar/events/608" for event with _id=608.
	 * @return opend cursor that must be closed by caller
	 */
	public Cursor getByContentURI(Uri uri) throws IllegalArgumentException {
		if ((uri != null) && (uri.getAuthority() != null)) {
			providerAutority  = uri.getAuthority();
		}
		
		return super.getByContentURI(uri);
	}
}
