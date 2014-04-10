/*
 * Copyright (C) 2013, 2014 - Daniele Gobbetti and k3b
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

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Iterator;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Dur;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Clazz;
import de.k3b.android.compat.CalendarContract;
import de.k3b.calendar.EventDto;
import de.k3b.calendar.IcsAsEventDto;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

/**
 * Invisible Pseudo-Activity that imports a ics-calendar-event-file into the android Calendar.
 * Supports Android 4.0 and up. Runs on most Android 2.1 and up that have a calendar and a calendar provider.<br/><br/>
 * @author k3b
 */
public class Ics2ACalendarActivity extends Activity {
	static final String TAG = "ICS-Import";

	// see http://stackoverflow.com/questions/3721963/how-to-add-calendar-events-in-android
    private static final String CONTENT_TYPE_EVENT = "vnd.android.cursor.item/event";
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

		Uri data = intent.getData();
		
		Log.d(TAG, "Ics2ACalendarActivity begin " + data);
		startCalendarImportActivity(this, data);
		Log.d(TAG, "Ics2ACalendarActivity done" + data);

		this.finish();
    }
	
	static void startCalendarImportActivity(Context context, Uri calendarEventUri) {
		if (calendarEventUri != null) {
			try {
				Log.d(TAG, "opening " + calendarEventUri);
				//use ical4j to parse the event
				CalendarBuilder cb = new CalendarBuilder();
				Calendar calendar = cb.build(getStreamFromOtherSource(context, calendarEventUri));
	
				if (calendar != null) {
	
					Iterator<?> i = calendar.getComponents(Component.VEVENT).iterator();
	
					while (i.hasNext()) {
						VEvent event = (VEvent) i.next();
	
						Log.d(TAG, "processing event " + event.getName());
	
						Intent insertIntent = createEventIntent(context, event);
						insertIntent.putExtra(CalendarContract.Events.ACCESS_LEVEL, getAccessLevel(event.getClassification()));
	
	
						context.startActivity(insertIntent);
	
					}
				}
	
			} catch (Exception e) {
				Log.e(TAG, "error processing " + calendarEventUri + " : " + e);
				e.printStackTrace();
			}
		}
	}

	private static Intent createEventIntent(Context context, VEvent _event) {
		EventDto event = new IcsAsEventDto(_event);

		Intent insertIntent = new Intent(Intent.ACTION_EDIT).setType(CONTENT_TYPE_EVENT);
		addBeginEnd(insertIntent, event.getDtstart(), event.getDtend(), event.getDuration());
		if (event.getTitle() != null)
			insertIntent.putExtra(CalendarContract.Events.TITLE, event.getTitle());

		if (event.getDescription() != null)
			insertIntent.putExtra(CalendarContract.Events.DESCRIPTION, event.getDescription());

		if (event.getEventLocation() != null) 
			insertIntent.putExtra(CalendarContract.Events.EVENT_LOCATION, event.getEventLocation());

		if (event.getId() != null) {
			insertIntent.putExtra(CalendarContract.Events.ORIGINAL_ID, event.getId());
			insertIntent.putExtra("event_id", event.getId());
		
		}
		// X-MICROSOFT-CDO-BUSYSTATUS:BUSY

		
		String rule = event.getRrule();
		if (rule != null) {
			insertIntent.putExtra(CalendarContract.Events.RRULE, rule);
		}

		return insertIntent;
	}

	/**
	 * Assumes allday if enddate is null or diff between end-start has 0 hours and 0 minutes.<br/>
	 * calculates enddate from startdate+duration if neccessary.<br/>
	 */
    private static void addBeginEnd(Intent insertIntent, long startDate,
    		long endDate, String duration) {
    	
		boolean allDay = false;
		if (startDate != 0) {
			insertIntent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startDate);
			//insertIntent.putExtra(CalendarContract.Events.DTSTART, startDate.getDate().getTime());

			if (endDate == 0) { 
				allDay = true;
			}
		}
		
		if (endDate != 0) {
			insertIntent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endDate);
			// insertIntent.putExtra(CalendarContract.Events.DTEND, endDate.getDate().getTime());
			if ((startDate != 0)) {
				if (isAllDay(new Dur(new DateTime(startDate), new DateTime(endDate)))) {
					allDay = true;					
				}
			}
		}

		if (duration != null) {
			insertIntent.putExtra(CalendarContract.Events.DURATION, duration);		
		}

		if (allDay) {
			insertIntent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true);		
		}
	}

	private static boolean isAllDay(Dur duration) {
		return duration.getHours() == 0 && duration.getMinutes() == 0;
	}

	private static int getAccessLevel(Clazz clazz)
	{
		if (clazz == Clazz.CONFIDENTIAL) return CalendarContract.Events.ACCESS_DEFAULT;
		if (clazz == Clazz.PUBLIC) return CalendarContract.Events.ACCESS_PUBLIC;
		if (clazz == Clazz.PRIVATE) return CalendarContract.Events.ACCESS_PRIVATE;
		return CalendarContract.Events.ACCESS_DEFAULT;
	}
	
	protected static InputStream getStreamFromOtherSource(Context context, Uri contentUri) throws FileNotFoundException {
	    ContentResolver res = context.getApplicationContext().getContentResolver();
	    Uri uri = Uri.parse(contentUri.toString());
	    InputStream is;
	    try {
		    is = res.openInputStream(uri);
	    } catch (FileNotFoundException e) {
		    is = new ByteArrayInputStream(new byte[0]);
	    }
	    return is;
    }
}
