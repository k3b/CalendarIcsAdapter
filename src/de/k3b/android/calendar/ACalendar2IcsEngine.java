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

import de.k3b.android.calendar.ACalendarCursorAsEventDto;
import de.k3b.android.calendar.ACalendarMock;
import de.k3b.android.calendar.ics.adapter.R;
import de.k3b.calendar.EventDto;
import de.k3b.calendar.EventDto2IcsFactory;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.TimeZone;
import android.content.Context;
import android.database.*;
import android.database.sqlite.*;
import android.net.Uri;
import android.util.Log;

/**
 * Android specific engine that converts a Android-Calendar-Event to a ics-Calendar-Event string.<br/><br/>
 * @author k3b
 */
public class ACalendar2IcsEngine implements Closeable {
	public static final String TAG = "ICS-Export";

	final private SQLiteOpenHelper mock;
	final private ACalendarCursorAsEventDto eventData;

	private SQLiteDatabase writableDatabase = null;

	private Context ctx;
	
	public ACalendar2IcsEngine(Context ctx, boolean useMockCalendar) {
		this.ctx = ctx;
		mock = (useMockCalendar) ? new ACalendarMock(ctx) : null;
		writableDatabase  = (useMockCalendar) ? mock.getWritableDatabase() : null;
		this.eventData = (mock != null) ? new ACalendarCursorAsEventDto(writableDatabase) : new ACalendarCursorAsEventDto(ctx) ;
	}

	public Calendar export(Uri contentUri) {
		boolean hasData = false;
		EventDto2IcsFactory factory = new EventDto2IcsFactory(this.ctx.getText(R.string.app_ics_provider_name).toString());
		// set to null for non mocked production
		Cursor eventCursor = eventData.getByContentURI(contentUri);
		
		if (eventCursor != null) {		
			// Use the cursor to step through the returned records
			while (eventCursor.moveToNext()) {
				hasData = true;
				TimeZone timezone = getOrCreateTimeZone(eventData);
				factory.addEvent(eventData, timezone);
				Log.d(ACalendar2IcsEngine.TAG, "added event " + eventData.getTitle());
			}
			eventCursor.close();
		}
		
		return (hasData) ? factory.getCalendar() : null;
	}
	
	TimeZone getOrCreateTimeZone(EventDto data) {
		// not implemented yet
		return null; //??? if (data.getEventTimezone() != null) eventProperties.add(new Timez(data.getEventTimezone()));
	}

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
