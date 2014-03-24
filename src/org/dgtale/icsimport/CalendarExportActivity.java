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
package org.dgtale.icsimport;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Iterator;

import de.k3b.android.compat.CalendarContract;
import de.k3b.android.data.CursorData;
import de.k3b.android.data.calendar.CalendarExportEngine;

//import android.provider.CalendarContract from android 4.0 is replaced by local CalendarContract so it is runnable from android 2.1 

public class CalendarExportActivity extends Activity {
	/**
	 * true: use local calendar db (for testing); false: use contentProvider for production
	 */
	private static final boolean USE_MOCK_CALENDAR = true;

	// see http://stackoverflow.com/questions/3721963/how-to-add-calendar-events-in-android
    private static final String CONTENT_TYPE_EVENT = "vnd.android.cursor.item/event";

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

		Uri data = intent.getData();
		
		if ((USE_MOCK_CALENDAR) && (data == null)) {
			// data = CursorData.createContentUri("event","68");
			data = CursorData.createContentUri("event");
		}
		
		if (data != null) {
			try {
				Log.d(CalendarExportEngine.TAG, "opening " + data);
				
				CalendarExportEngine engine = new CalendarExportEngine(this.getApplication(), USE_MOCK_CALENDAR);
				
				Calendar result = engine.export(data);
				
				if (result != null) {
					// Log.d(CalendarExportEngine.TAG, result.toString());
					writeStringToTextFile(this.getDir("log", Context.MODE_PRIVATE), "last.ics", result.toString());
				}
				
			} catch (Exception e) {
				Log.e(CalendarExportEngine.TAG, "error processing " + data + " : " + e);
				e.printStackTrace();
			}
		}
		Log.d(CalendarExportEngine.TAG, "done");
		this.finish();
    }
	
	private void writeStringToTextFile(File dir, String fileName, String content){
		File file = new File(dir, fileName);
		try{
		    FileOutputStream f1 = new FileOutputStream(file,false); //True = Append to file, false = Overwrite
		    PrintStream p = new PrintStream(f1);
		    p.print(content);
		    p.close();
		    f1.close();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}   
	}

}
