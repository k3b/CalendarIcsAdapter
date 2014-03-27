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

import org.dgtale.android.compat.CalendarContract;
import org.dgtale.calendar.EventDto;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Facade that make a android-calendar-event-cursor appear as EventDto.<br/>
 * @author k3b
 */
public class ACalendarCursorAsEventDto extends ACalendarCursor implements EventDto {
	/**
	 * Creates a datasource that uses the ContentResolver from context
	 */
	public ACalendarCursorAsEventDto(Context ctx) {
		super(ctx);
	}
	
	/**
	 * Creates a datasource that uses a
	 * mockimplementation for testing with local copy of events database. This way real events are not at risc or you can test it on an 
	 * emulator with no calendar.<br/>
	 * To use copy existing events database file (/data/data/com.android.provider.calendar/databases/calendar.db ) 
	 * to local apps database folder ( /data/data/org.dgtale.calendar.adapter/databases/calendar.db ) .<br/>
	 */
	public ACalendarCursorAsEventDto(SQLiteDatabase mockDatabase) {
		super(mockDatabase);
	}

	/**
	 * gets the colums that belong to this ContentUriCursor
	 */
	@Override
	protected String[] getColums() { return COLUMS; }	

	// collumn names must match order in the getters below
	private String[] COLUMS = new String[] {
			CalendarContract.EventsColumns._ID, 
			CalendarContract.EventsColumns.DTSTART,                           
			CalendarContract.EventsColumns.DTEND,                           
			CalendarContract.EventsColumns.TITLE,
			CalendarContract.EventsColumns.DESCRIPTION,                           
			CalendarContract.EventsColumns.EVENT_LOCATION,                  
			CalendarContract.EventsColumns.EVENT_TIMEZONE,                           
			CalendarContract.EventsColumns.DURATION,                           
			CalendarContract.EventsColumns.RRULE,
			CalendarContract.EventsColumns.ORGANIZER,
			CalendarContract.EventsColumns.CALENDAR_ID                          
		};

	/* (non-Javadoc)
	 * @see org.dgtale.calendar.adapter.EventData#getDtstart()
	 */
	@Override
	public long getDtstart() {return cur.getLong(1);}
	/* (non-Javadoc)
	 * @see org.dgtale.calendar.adapter.EventData#getDtend()
	 */
	@Override
	public long getDtend() {return cur.getLong(2);}
	/* (non-Javadoc)
	 * @see org.dgtale.calendar.adapter.EventData#getTitle()
	 */
	@Override
	public String getTitle() {return cur.getString(3);}
	/* (non-Javadoc)
	 * @see org.dgtale.calendar.adapter.EventData#getDescription()
	 */
	@Override
	public String getDescription() {return cur.getString(4);}
	/* (non-Javadoc)
	 * @see org.dgtale.calendar.adapter.EventData#getEventLocation()
	 */
	@Override
	public String getEventLocation() {return cur.getString(5);}
	/* (non-Javadoc)
	 * @see org.dgtale.calendar.adapter.EventData#getEventTimezone()
	 */
	@Override
	public String getEventTimezone() {return cur.getString(6);}
	/* (non-Javadoc)
	 * @see org.dgtale.calendar.adapter.EventData#getDuration()
	 */
	@Override
	public String getDuration() {return cur.getString(7);}
	/* (non-Javadoc)
	 * @see org.dgtale.calendar.adapter.EventData#getRrule()
	 */
	@Override
	public String getRrule() {return cur.getString(8);}
	@Override
	public String getOrganizer() {return cur.getString(9);}
	/*
	DTSTAMP:20131031T071858Z
	PRIORITY:1
	ATTENDEE;ROLE=REQ-PARTICIPANT;CN=BUYER:mailto:notimportant@example.com
	ATTENDEE;ROLE=REQ-PARTICIPANT;CN=TRAVELLER:notimportant@example.com
	ORGANIZER;CN=ORGANIZER:mailto:webmaster@trenitalia.it
	RRULE:FREQ=MONTHLY;WKST=MO;BYDAY=3SA
	*/

	@Override
	public String getCalendarId() {return cur.getString(10);}
}
