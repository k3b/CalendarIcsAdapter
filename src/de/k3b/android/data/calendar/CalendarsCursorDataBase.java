package de.k3b.android.data.calendar;

import java.util.ArrayList;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import de.k3b.android.data.CursorData;

public abstract class CalendarsCursorDataBase extends CursorData {

	/**
	 * Creates a datasource that uses the ContentResolver from context
	 */
	public CalendarsCursorDataBase(Context ctx) {
		super(ctx);
	}
	
	/**
	 * Creates a datasource that uses a
	 * mockimplementation for testing with local copy of events database. This way real events are not at risc or you can test it on an 
	 * emulator with no calendar.<br/>
	 * To use copy existing events database file (/data/data/com.android.provider.calendar/databases/calendar.db ) 
	 * to local apps database folder ( /data/data/org.dgtale.icsimport/databases/calendar.db ) .<br/>
	 */
	public CalendarsCursorDataBase(SQLiteDatabase mockDatabase) {
		super(mockDatabase,"calendar", "event");
	}

}
