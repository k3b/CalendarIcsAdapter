/*
 * Copyright (C) 2013- Daniele Gobbetti
 * 
 * This file is part of icsimport.
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
package de.k3b.calendar.adapter;

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

import net.fortuna.ical4j.model.Calendar;

import de.k3b.android.data.calendar.CalendarExportEngine;
import de.k3b.android.data.calendar.CalendarsContentUriCursor;
import de.k3b.data.calendar.EventDTO;

//import android.provider.CalendarContract from android 4.0 is replaced by local CalendarContract so it is runnable from android 2.1 

public class Calendar2IcsActivity extends Activity {
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
			data = CalendarsContentUriCursor.createContentUri("events");
		}
		
		if (data != null) {
			try {
				Log.d(CalendarExportEngine.TAG, "opening " + data);
				
				CalendarExportEngine engine = new CalendarExportEngine(this.getApplication(), USE_MOCK_CALENDAR);
				
				Object result = engine.export(data);
				
				engine.close();
				
				if (result != null) {
					viewViaFileContent(result.toString());
					// viewViaFile(result.toString());
				}
				
			} catch (Exception e) {
				Log.e(CalendarExportEngine.TAG, "error processing " + data + " : " + e);
				e.printStackTrace();
			}
		}
		Log.d(CalendarExportEngine.TAG, "done");
		this.finish();
    }

	private void sendTo(String calendarEventContent) throws IOException {
		
		final Intent outIntent = new Intent();
		outIntent.setAction(Intent.ACTION_SEND);
		outIntent.putExtra(Intent.EXTRA_TEXT, calendarEventContent);
		outIntent.setType("text/calendar");
		this.startActivity(Intent.createChooser(outIntent, "Send to ..."));
		
		// erscheint leider als mail content und nicht als *.ics anlage vielleicht geht sendto oder extra_stream
	}
	

	// unfortunately the client has no permissions to read the file
	// and this adapter should not need public file writing permissions
	private void viewViaFile(String calendarEventContent) throws IOException {
		// making app local file visible to the world is depricated since android 17.
		// if there are problems in newer android version maybe a file contentprovider helps
		// https://developer.android.com/reference/android/support/v4/content/FileProvider.html
		File icsFIle = new File(this.getDir("export", Context.MODE_WORLD_READABLE), "current.ics");
		// Log.d(CalendarExportEngine.TAG, result.toString());
		writeStringToTextFile(icsFIle, calendarEventContent.toString());
		
		final Intent outIntent = new Intent();
		outIntent.setAction(Intent.ACTION_VIEW);
		outIntent.setData(Uri.fromFile(icsFIle));
		this.startActivity(Intent.createChooser(outIntent, "Send to ..."));
	}
	
	// unfortunately the client has no permissions to read the file
	// and this adapter should not need public file writing permissions
	private void viewViaFileContent(String calendarEventContent) throws IOException {
		// https://developer.android.com/reference/android/support/v4/content/FileProvider.html

		// concatenate the internal cache folder with the document its path and filename
		File path = new File(this.getCacheDir(), "ics");
		path.mkdirs();
		final File icsFIle = new File(path, "FromAndroidCalendar.ics");
		// Log.d(CalendarExportEngine.TAG, result.toString());
		writeStringToTextFile(icsFIle, calendarEventContent.toString());
		
		// let the FileProvider generate an URI for this private icsFIle
		final Uri uri = FileProvider.getUriForFile(this, "de.k3b.calendar.adapter", icsFIle);
		
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
