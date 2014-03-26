package de.k3b.android.data.calendar;

import de.k3b.android.compat.CalendarContract;
import de.k3b.data.calendar.EventData;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class EventDataImpl extends CalendarsCursorDataBase implements EventData {
	/**
	 * Creates a datasource that uses the ContentResolver from context
	 */
	public EventDataImpl(Context ctx) {
		super(ctx);
	}
	
	/**
	 * Creates a datasource that uses a
	 * mockimplementation for testing with local copy of events database. This way real events are not at risc or you can test it on an 
	 * emulator with no calendar.<br/>
	 * To use copy existing events database file (/data/data/com.android.provider.calendar/databases/calendar.db ) 
	 * to local apps database folder ( /data/data/org.dgtale.icsimport/databases/calendar.db ) .<br/>
	 */
	public EventDataImpl(SQLiteDatabase mockDatabase) {
		super(mockDatabase);
	}

	/**
	 * gets the colums that belong to this CursorData
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
	 * @see org.dgtale.icsimport.EventData#getDtstart()
	 */
	@Override
	public long getDtstart() {return cur.getLong(1);}
	/* (non-Javadoc)
	 * @see org.dgtale.icsimport.EventData#getDtend()
	 */
	@Override
	public long getDtend() {return cur.getLong(2);}
	/* (non-Javadoc)
	 * @see org.dgtale.icsimport.EventData#getTitle()
	 */
	@Override
	public String getTitle() {return cur.getString(3);}
	/* (non-Javadoc)
	 * @see org.dgtale.icsimport.EventData#getDescription()
	 */
	@Override
	public String getDescription() {return cur.getString(4);}
	/* (non-Javadoc)
	 * @see org.dgtale.icsimport.EventData#getEventLocation()
	 */
	@Override
	public String getEventLocation() {return cur.getString(5);}
	/* (non-Javadoc)
	 * @see org.dgtale.icsimport.EventData#getEventTimezone()
	 */
	@Override
	public String getEventTimezone() {return cur.getString(6);}
	/* (non-Javadoc)
	 * @see org.dgtale.icsimport.EventData#getDuration()
	 */
	@Override
	public String getDuration() {return cur.getString(7);}
	/* (non-Javadoc)
	 * @see org.dgtale.icsimport.EventData#getRrule()
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
	public int getCalendarId() {return cur.getInt(10);}
}
