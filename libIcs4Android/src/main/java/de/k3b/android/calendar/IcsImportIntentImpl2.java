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
package de.k3b.android.calendar;

import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Dur;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Clazz;
import android.content.Context;
import android.content.Intent;
import de.k3b.android.compat.CalendarContract;
import de.k3b.calendar.EventDto;
import de.k3b.calendar.IcsAsEventDto;

/**
 * Factory to convert ics to calendar import Intent.<br/>
 * Note: IcsImportIntentFactory2 for android2.x and IcsImportIntentFactory4 for android4.x are identical, except
 * that 2.x uses de.k3b.android.compat.CalendarContract and 4.x uses android.provider.CalendarContract;
 * 
 * @author k3b
 */
class IcsImportIntentImpl2  {
	// see http://stackoverflow.com/questions/3721963/how-to-add-calendar-events-in-android
    private static final String CONTENT_TYPE_EVENT = "vnd.android.cursor.item/event";
	
	public Intent createImportIntent(Context context, final EventDto eventDto, VEvent event) {
		Intent insertIntent = createEventIntent(context, eventDto);
		insertIntent.putExtra(CalendarContract.Events.ACCESS_LEVEL, getAccessLevel(event.getClassification()));
		return insertIntent;
	}

	/**
	 * translates a ical4j-Calendar-Event into Android-"Add Event-To-Calendar"-Intent
	 */
	private Intent createEventIntent(Context context, EventDto event) {
		Intent insertIntent = new Intent(Intent.ACTION_EDIT).setType(CONTENT_TYPE_EVENT);
		addBeginEnd(insertIntent, event.getDtStart(), event.getDtEnd(), event.getDuration());
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

		
		String rule = event.getRRule();
		if (rule != null) {
			insertIntent.putExtra(CalendarContract.Events.RRULE, rule);
		}

		return insertIntent;
	}

	/**
	 * Assumes allday if enddate is null or diff between end-start has 0 hours and 0 minutes.<br/>
	 */
    private void addBeginEnd(Intent insertIntent, long startDate,
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

	/**
	 * Assumes allday if enddate is null or diff between end-start has 0 hours and 0 minutes.<br/>
	 */
	private boolean isAllDay(Dur duration) {
		return duration.getHours() == 0 && duration.getMinutes() == 0;
	}

	/**
	 * translates from ical4jClazz to Android-Accesslevel
	 */
	private int getAccessLevel(Clazz ical4jClazz)
	{
		if (ical4jClazz == Clazz.CONFIDENTIAL) return CalendarContract.Events.ACCESS_DEFAULT;
		if (ical4jClazz == Clazz.PUBLIC) return CalendarContract.Events.ACCESS_PUBLIC;
		if (ical4jClazz == Clazz.PRIVATE) return CalendarContract.Events.ACCESS_PRIVATE;
		return CalendarContract.Events.ACCESS_DEFAULT;
	}


}
