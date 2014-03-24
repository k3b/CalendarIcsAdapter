package de.k3b.android.data.calendar;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Encapsulation of the Database create/open/close/upgrade
 */
public class CalendarMock extends SQLiteOpenHelper {
	public CalendarMock(final Context context) {
		super(context, "calendar.db", null, 1);
		context.getDir("databases", Context.MODE_PRIVATE); // create dir if it does not exist
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
}
