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
package de.k3b.calendar;

import java.util.List;

/**
 * calendar-event-abstraction that is independant from Android-Calendar-Event and form iCal4j-VEvent-ics-Implementation.
 * This class has no direct dependency to android so it can be used in a j2se-junit-integration tests.<br/><br/>
 * 
 * @author k3b
 */
public interface EventDto {
	public abstract String getId();
	
	public abstract long getDtStart();

	public abstract long getDtEnd();

	public abstract String getTitle();

	public abstract String getDescription();

	public abstract String getEventLocation();

	public abstract String getEventTimezone();

	public abstract String getDuration();

	public abstract String getRRule();

    public abstract String getRDate();

    /** #11 formatted as komma seperated list of iso-utc-dates. Example: '20090103T093000Z,20110101T093000Z' */
    public abstract String getExtDates();

    public abstract String getOrganizer();

    public abstract String getCalendarId();

    /** #9 the alarm(s) should trigger x menutes before the event. null means no alarms. Corresponds to VEvent.Alarms[].Trigger */
    public abstract List<Integer> getAlarmMinutesBeforeEvent();
}
