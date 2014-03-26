package de.k3b.android.data.calendar;

import de.k3b.android.data.calendar.CalendarMock;
import de.k3b.android.data.calendar.EventDataImpl;
import de.k3b.data.calendar.CalendarFactory;
import de.k3b.data.calendar.EventData;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.TimeZone;
import android.content.Context;
import android.database.*;
import android.database.sqlite.*;
import android.net.Uri;
import android.util.Log;

/**
 * converter from Android-Calendar-Events to iCal-Calendar-Events
 */
public class CalendarExportEngine {
	public static final String TAG = "ICS-Export";

	final private SQLiteOpenHelper mock;
	final private EventDataImpl eventData;

	private SQLiteDatabase writableDatabase = null;
	
	public CalendarExportEngine(Context ctx, boolean useMockCalendar) {
		mock = (useMockCalendar) ? new CalendarMock(ctx) : null;
		this.writableDatabase  = (useMockCalendar) ? mock.getWritableDatabase() : null;
		this.eventData = (mock != null) ? new EventDataImpl(writableDatabase) : new EventDataImpl(ctx) ;
	}

	public Calendar export(Uri contentUri) {
		boolean hasData = false;
		CalendarFactory factory = new CalendarFactory("-//org.dgtale.icsimport//iCal4j 1.0//EN");
		// set to null for non mocked production
		Cursor eventCursor = eventData.getByContentURI(contentUri);
		
		// Use the cursor to step through the returned records
		while (eventCursor.moveToNext()) {
			hasData = true;
			TimeZone timezone = getOrCreateTimeZone(eventData);
			factory.addEvent(eventData, timezone);
			Log.d(CalendarExportEngine.TAG, "added event " + eventData.getTitle());
		}
		eventCursor.close();
		
		return (hasData) ? factory.getCalendar() : null;
	}
	
	TimeZone getOrCreateTimeZone(EventData data) {
		// not implemented yet
		return null; //??? if (data.getEventTimezone() != null) eventProperties.add(new Timez(data.getEventTimezone()));
	}

	public void close() {
		if (writableDatabase != null) {
			writableDatabase.close();
			writableDatabase = null;
		}
	}
}
