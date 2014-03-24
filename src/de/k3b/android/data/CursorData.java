package de.k3b.android.data;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import de.k3b.android.compat.CalendarContract;
import edu.emory.mathcs.backport.java.util.Arrays;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * Baseclass for cursor based dataaccess via content-uri.<br/>
 * It can used with contentResolver or with database
 */
public abstract class CursorData {
	protected static String providerAutority = "com.android.calendar"; // uri of content provider. my differ with android version below 4.0

	protected static String selectionById = "(" + CalendarContract.EventsColumns._ID + " = ? )";
	protected Cursor cur = null;
	protected ContentResolver contentResolver = null;
	protected SQLiteDatabase mockDatabase = null;

	private List<String> contentToTablePlurals;

	/**
	 * Creates a datasource that uses the ContentResolver from context
	 */
	public CursorData(Context ctx) {
		this.contentResolver = ctx.getContentResolver();
	}
	
	/**
	 * Creates a datasource that uses a
	 * mockimplementation for testing with local copy of events database. This way real events are not at risc or you can test it on an 
	 * emulator with no calendar.<br/>
	 * To use copy existing events database file (/data/data/com.android.provider.calendar/databases/calendar.db ) 
	 * to local apps database folder ( /data/data/org.dgtale.icsimport/databases/calendar.db ) .<br/>
	 */
	@SuppressWarnings("unchecked")
	public CursorData(SQLiteDatabase mockDatabase, String... contentToTablePlurals) {
		this.mockDatabase = mockDatabase;
		this.contentToTablePlurals = Arrays.asList(contentToTablePlurals);
	}

	/**
	 * gets the colums that belong to this CursorData.
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
		if (uriSegments.size() < 1) throw new IllegalArgumentException("ContentURI expected content://com.adnroid.calendar/{TABLE}[/{EVENTID}] but was " + uri.toString());
		
		providerAutority  = uri.getAuthority();
		String tableName = uriSegments.get(0);
		if (contentToTablePlurals.contains(tableName)) {
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

	public static Uri createContentUri(String... urlParts) {
		StringBuffer uri = new StringBuffer("content://" + providerAutority);
		for(String urlPart : urlParts) {
			uri.append("/").append(urlPart);
		}
		return Uri.parse(uri.toString());
	}
	
	public long getId() {return cur.getLong(0);}

	protected Date getDateTime(int columnIndex) {
		long ticks = cur.getLong(columnIndex);
		return (ticks == 0) ? null : new Date(ticks);
	}
}
