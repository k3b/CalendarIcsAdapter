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

import de.k3b.android.Global;
import de.k3b.android.calendar.ics.R;
import de.k3b.android.compat.Compat;
import de.k3b.calendar.EventDto;
import de.k3b.calendar.EventFilter;
import de.k3b.calendar.ics.EventDto2IcsFactory;

import net.fortuna.ical4j.model.Calendar;

import android.content.Context;
import android.database.*;
import android.database.sqlite.*;
import android.net.Uri;
import android.util.Log;

/**
 * Android specific export engine that converts Android-Calendar-Event(s) to a ics-Calendar-Event string.<br/><br/>
 * 
 * @author k3b
 */
public class ACalendar2IcsEngine implements Closeable {
	/**
	 * uses as logging-prefix
	 */
	public static final String TAG = "ICS-Export";
	private static final String URI_TRANSLATION_PREFIX = "content://com.mediatek.calendarimporter/";

	/**
	 * where data comes from
	 */
	final private ACalendarEventContent eventData;

    /** controls, wich data elements will be exported */
    private final EventFilter filter;

    /**
	 * null if there is not contentprovider-mocking.
	 */
	final private SQLiteOpenHelper mock;

	/**
	 * null if there is not contentprovider-mocking.
	 */
	private SQLiteDatabase writableDatabase = null;

	/**
	 * used to access resources
	 */
	private final Context ctx;

	/**
	 * creates the engige that either uses mock-database or contentprovider
	 */
	public ACalendar2IcsEngine(Context ctx, final EventFilter filter, boolean useMockCalendar) {
		this.ctx = ctx;
        this.filter = filter;
		mock = (useMockCalendar) ? new ACalendarMock(ctx) : null;
		writableDatabase  = (useMockCalendar) ? mock.getWritableDatabase() : null;
		
		if (Compat.isCalendarContract4Available()) {
			this.eventData = new ACalendarEventContent4(ctx, writableDatabase);
		} else {
			this.eventData = new ACalendarEventContent2(ctx, writableDatabase) ;
		}
	}

	/**
	 * converts an android-calendar-event identified by contentUri to ics-Calendar
	 */
	public Calendar export(Uri contentUri, long dtStart, long dtEnd) {
		boolean hasData = false;

		String uriValue = (contentUri == null) ? null : contentUri.toString();
		if ((uriValue != null) && uriValue.startsWith(URI_TRANSLATION_PREFIX)) {
			contentUri = ACalendarCursor.createContentUri("events", uriValue.substring(URI_TRANSLATION_PREFIX.length()));
		}
		EventDto2IcsFactory factory = new EventDto2IcsFactory(filter, this.ctx.getText(R.string.app_ics_provider_name).toString());
        Cursor eventCursor = null;
        try {
            // set to null for non mocked production
            eventCursor = eventData.queryByContentURI(contentUri);

            if (eventCursor != null) {
                // Use the cursor to step through the returned records
                while (eventCursor.moveToNext()) {
                    hasData = true;
                    EventDto data = eventData.loadFull(this.filter);
                    factory.addEvent(data, dtStart, dtEnd);
                    if (Global.debugEnabled) {
                        Log.d(ACalendar2IcsEngine.TAG, "added event " + data.getTitle());
                    }
                }
            }
        } finally {
            if (eventCursor != null) {
                eventCursor.close();
            }
        }
        return (hasData) ? factory.getCalendar() : null;
	}

	/**
	 * closes all allocated resources
	 */
	public void close() {
		if (eventData != null) {
			eventData.close();
		}
		if (writableDatabase != null) {
			writableDatabase.close();
		}
		
		if (mock != null) {
			mock.close();
		}
	}
}
