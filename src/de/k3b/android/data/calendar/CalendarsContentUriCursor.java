package de.k3b.android.data.calendar;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import de.k3b.android.data.ContentUriCursor;

public abstract class CalendarsContentUriCursor extends ContentUriCursor {
	protected static String providerAutority = "com.android.calendar"; // uri of content provider. my differ with android version below 4.0

	/**
	 * Creates a datasource that uses the ContentResolver from context
	 */
	public CalendarsContentUriCursor(Context ctx) {
		super(ctx);
	}
	
	/**
	 * Creates a datasource that uses a
	 * mockimplementation for testing with local copy of events database. This way real events are not at risc or you can test it on an 
	 * emulator with no calendar.<br/>
	 * To use copy existing events database file (/data/data/com.android.provider.calendar/databases/calendar.db ) 
	 * to local apps database folder ( /data/data/de.k3b.calendar.adapter/databases/calendar.db ) .<br/>
	 */
	public CalendarsContentUriCursor(SQLiteDatabase mockDatabase) {
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
