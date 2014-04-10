/*
 * Copyright (C) 2014- k3b
 * 
 * This file is part of android.calendar.ics.adapter.
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
package de.k3b.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import de.k3b.android.calendar.ics.adapter.R;

import de.k3b.android.calendar.*;
import de.k3b.calendar.*;

import net.fortuna.ical4j.model.Calendar;
//import android.provider.CalendarContract from android 4.0 is replaced by local CalendarContract so it is runnable from android 2.1 

/**
 * Invisible Pseudo-Activity that exports a ics-calendar-event-file from the android Calendar.<br/>
 * Supports Android 4.0 and up. Runs on most Android 2.1 and up that have a calendar and a calendar provider. <br/><br/>
 * 
 * delivers ics-file-content via uri 
 * 			content:de.k3b.calendar.adapter/ics/FromAndroidCalendar.ics 
 * that is readable by other android apps without the need that this app requires sd-card-write-permission.<br/><br/>
 *
 * @author k3b
 */
public class ACalendar2IcsActivity extends Activity {
	/**
	 * true: use local calendar db (for testing); false: use contentProvider for production
	 */
	private static final boolean USE_MOCK_CALENDAR = false;

	private ACalendar2IcsEngine engine = null;

	/**
	 * This will differ between ics and ical
	 */
	protected CharSequence getChooserCaption() {
		return this.getText(R.string.export_chooser_caption_ics);
	}

	/**
	 * This will differ between ics and ical
	 */
	protected String getExportFileName() {
		return this.getText(R.string.export_filename_ics).toString();
	}
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

		Uri data = intent.getData();
		
		if ((USE_MOCK_CALENDAR) && (data == null)) {
			data = ACalendarCursor.createContentUri("event","1");
			// data = ACalendarCursor.createContentUri("events");
		}
		
		if (data != null) {
			try {
				if (engine == null) {
					Log.d(ACalendar2IcsEngine.TAG, "creating ACalendar2IcsEngine");					
					engine = new ACalendar2IcsEngine(this.getApplication(), USE_MOCK_CALENDAR);
				}
				
				Log.d(ACalendar2IcsEngine.TAG, "opening " + data);					
				Calendar calendarEvent = engine.export(data);
				
				if (calendarEvent != null) {
					EventDto event = new IcsAsEventDto(calendarEvent);
					
					String mailSubject = getMailSubject(event); 
					String description = getMailDescription(event);
					Log.d(ACalendar2IcsEngine.TAG, "sending '" + mailSubject + "'");					
					sendIcsTo(mailSubject, description, calendarEvent.toString());
				}
				
			} catch (Exception e) {
				Log.e(ACalendar2IcsEngine.TAG, "error processing " + data + " : " + e);
				e.printStackTrace();
			}
		}
		Log.d(ACalendar2IcsEngine.TAG, "export done");
		this.finish();
    }

	@Override protected void onDestroy() 
	{
		if (engine != null) {
			Log.d(ACalendar2IcsEngine.TAG, "destroying ACalendar2IcsEngine");					
			engine.close();
		}
		engine = null;
		
		super.onDestroy();
	};

	private String getMailSubject(EventDto event) {
		if (event != null) {
			String date = "";

			long start = event.getDtstart();
			if (start != 0) {
				Date dtStart = (start == 0) ? null : new Date(start);
			
				Locale locale = Locale.getDefault();
				DateFormat shortTimeformatter = DateFormat.getDateInstance(java.text.DateFormat.SHORT, locale);
				date = shortTimeformatter.format(dtStart);
			}
			return String.format(this.getString(R.string.export_mail_subject).toString(),
							date,event.getTitle());
		}
		return null;
	}

	protected String getMailDescription(EventDto event) {
		if (event != null) {
			String date = "";

			long start = event.getDtstart();
			if (start != 0) {
				Date dtStart = (start == 0) ? null : new Date(start);
			
				Locale locale = Locale.getDefault();
				DateFormat dateFormatter = DateFormat.getDateInstance(java.text.DateFormat.FULL, locale);
				DateFormat timeFormatter = DateFormat.getTimeInstance(java.text.DateFormat.SHORT, locale);
				date = dateFormatter.format( dtStart) + " " + timeFormatter.format(dtStart);
			}
			return String.format(this.getString(R.string.export_mail_content).toString(),
							date,event.getEventLocation(), event.getDescription(), getAppVersionName());
		}
		return null;
	}
	
	private String getAppVersionName() {
		String versionName = "";
		try {

			versionName = "-" + this.getPackageManager()
					.getPackageInfo(this.getPackageName(), 0).versionName;
		} catch (final NameNotFoundException e) {
		}
		return getString(R.string.app_name) + versionName;
	}

	private void sendIcsTo(String mailSubject, String mailBody, String mailAttachmentContent) throws IOException {
		final File icsFIle = this.getOuputFile();
		// Log.d(ACalendar2IcsEngine.TAG, result.toString());
		this.writeStringToTextFile(icsFIle, mailAttachmentContent.toString());
		
		final Uri uri = this.getUriForFile(icsFIle);
		
		final Intent outIntent = new Intent()
			.setAction(Intent.ACTION_SEND)
			.setDataAndType(uri, this.getText(R.string.export_file_mime_type).toString())
			.putExtra(Intent.EXTRA_STREAM, uri)
			.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)
			.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		
		if (mailSubject != null) {
			outIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, mailSubject);
		}
		
		if (mailBody != null) {
			outIntent.putExtra(android.content.Intent.EXTRA_TEXT, mailBody);
		}
		
		this.startActivity(Intent.createChooser(outIntent, getChooserCaption()));
	}

	/**
	 * global readable File.<br/>
	 */
	private  Uri getUriForFile(final File icsFile) {
		final Uri uri = Uri.fromFile(icsFile);
		return uri;
	}

	private  void writeStringToTextFile(File file, String content) throws IOException{
	    FileOutputStream f1 = new FileOutputStream(file,false); //True = Append to file, false = Overwrite
	    PrintStream p = new PrintStream(f1);
	    p.print(content);
	    p.close();
	    f1.close();
	}
	
	/**
	 * cachefile content.FileProvider specific implementention that does not need local file permissions.<br/>
	 */
	private File getOuputFile() {
		final File path = getOutputDir();
		final File icsFIle = new File(path, getExportFileName());
		return icsFIle;
	}

	/**
	 * get or create dir where ics file will be stored.<br/>
	 */
	private File getOutputDir() {
		File path = new File(Environment.getExternalStorageDirectory(),this.getText(R.string.export_file_public_folder).toString()); 
		path.mkdirs();

		return path;
	}
}
