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
package org.dgtale.android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.dgtale.R;
import org.dgtale.android.calendar.ACalendar2IcsEngine;
import org.dgtale.android.calendar.ACalendarCursor;
import org.dgtale.calendar.EventDto;

import net.fortuna.ical4j.model.Calendar;
//import android.provider.CalendarContract from android 4.0 is replaced by local CalendarContract so it is runnable from android 2.1 

/**
 * Invisible Pseudo-Activity that exports a ics-calendar-event-file from the android Calendar.<br/>
 * Supports Android 4.0 and up. Runs on most Android 2.1 and up that have a calendar and a calendar provider. <br/><br/>
 * 
 * delivers ics-file-content via uri 
 * 			content:org.dgtale.calendar.adapter/ics/FromAndroidCalendar.ics 
 * that is readable by other android apps without the need that this app requires sd-card-write-permission.<br/><br/>
 * @author k3b
 */
public class ACalendar2IcsActivity extends Activity {
	/**
	 * true: use local calendar db (for testing); false: use contentProvider for production
	 */
	private static final boolean USE_MOCK_CALENDAR = false;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

		Uri data = intent.getData();
		
		if ((USE_MOCK_CALENDAR) && (data == null)) {
			// data = ContentUriCursor.createContentUri("event","68");
			data = ACalendarCursor.createContentUri("events");
		}
		
		if (data != null) {
			try {
				Log.d(ACalendar2IcsEngine.TAG, "opening " + data);
				
				ACalendar2IcsEngine engine = new ACalendar2IcsEngine(this.getApplication(), USE_MOCK_CALENDAR);
				
				Object result = engine.export(data);
				
				engine.close();
				
				if (result != null) {
					viewViaFileContent(result.toString());
					// viewViaFile(result.toString());
				}
				
			} catch (Exception e) {
				Log.e(ACalendar2IcsEngine.TAG, "error processing " + data + " : " + e);
				e.printStackTrace();
			}
		}
		Log.d(ACalendar2IcsEngine.TAG, "done");
		this.finish();
    }
	
	// unfortunately the client has no permissions to read the file
	// and this adapter should not need public file writing permissions
	private void viewViaFileContent(String calendarEventContent) throws IOException {
		// https://developer.android.com/reference/android/support/v4/content/FileProvider.html

		// concatenate the internal cache folder with the document its path and filename
		File path = new File(this.getCacheDir(), "ics");
		path.mkdirs();
		final File icsFIle = new File(path, "FromAndroidCalendar.ics");
		// Log.d(ACalendar2IcsEngine.TAG, result.toString());
		writeStringToTextFile(icsFIle, calendarEventContent.toString());
		
		// let the FileProvider generate an URI for this private icsFIle
		final Uri uri = FileProvider.getUriForFile(this, "org.dgtale.calendar.adapter", icsFIle);
		
		final Intent outIntent = new Intent()
			.setAction(Intent.ACTION_SEND)
			.setDataAndType(uri, "text/calendar")
			.putExtra(Intent.EXTRA_STREAM, uri)
			.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)
			.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

		this.startActivity(Intent.createChooser(outIntent, "Send to ..."));
	}
	
	private void writeStringToTextFile(File file, String content) throws IOException{
	    FileOutputStream f1 = new FileOutputStream(file,false); //True = Append to file, false = Overwrite
	    PrintStream p = new PrintStream(f1);
	    p.print(content);
	    p.close();
	    f1.close();
	}

}
