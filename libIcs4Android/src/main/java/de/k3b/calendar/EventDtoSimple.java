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

/**
 * Minimal implementation of EventDto to backup values of other EventDto-implementations.<br/>
 * This class has no direct dependency to android so it can be run in a j2se-junit-integration test.<br/><br/>
 * 
 * @author k3b
 */
public class EventDtoSimple implements EventDto {
	public String getId() {
		return Id;
	}

	public EventDtoSimple setId(String id) {
		Id = id;
		return this;
	}

	public long getDtstart() {
		return Dtstart;
	}

	public EventDtoSimple setDtstart(long dtstart) {
		Dtstart = dtstart;
		return this;
	}

	public long getDtend() {
		return Dtend;
	}

	public EventDtoSimple setDtend(long dtend) {
		Dtend = dtend;
		return this;
	}

	public String getTitle() {
		return Title;
	}

	public EventDtoSimple setTitle(String title) {
		Title = title;
		return this;
	}

	public String getDescription() {
		return Description;
	}

	public EventDtoSimple setDescription(String description) {
		Description = description;
		return this;
	}

	public String getEventLocation() {
		return EventLocation;
	}

	public EventDtoSimple setEventLocation(String eventLocation) {
		EventLocation = eventLocation;
		return this;
	}

	public String getEventTimezone() {
		return EventTimezone;
	}

	public EventDtoSimple setEventTimezone(String eventTimezone) {
		EventTimezone = eventTimezone;
		return this;
	}

	public String getDuration() {
		return Duration;
	}

	public EventDtoSimple setDuration(String duration) {
		Duration = duration;
		return this;
	}

	public String getRrule() {
		return Rrule;
	}

	public EventDtoSimple setRrule(String rrule) {
		Rrule = rrule;
		return this;
	}

	public String getOrganizer() {
		return Organizer;
	}

	public EventDtoSimple setOrganizer(String organizer) {
		Organizer = organizer;
		return this;
	}

	public String getCalendarId() {
		return CalendarId;
	}

	public EventDtoSimple setCalendarId(String calendarId) {
		this.CalendarId = calendarId;
		return this;
	}

	String Id;
	
	long Dtstart;

	long  Dtend;

	String Title;

	String Description;

	String EventLocation;

	String EventTimezone;

	String Duration;

	String Rrule;

	String Organizer;

	String CalendarId;
}
