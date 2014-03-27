/*
 * Copyright (C) 2013, 2014 - Daniele Gobbetti and k3b
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
package org.dgtale.calendar;

import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.DateProperty;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.Duration;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.Organizer;
import net.fortuna.ical4j.model.property.RRule;
import net.fortuna.ical4j.model.property.Summary;
import net.fortuna.ical4j.model.property.Uid;

/**
 * Facade that makes a ical4j-vevent implementation specific ics appear as EventDto.<br/>
 * 
 * This class has no direct dependency to android so it can be run in a j2se-junit-integration test.<br/><br/>
 * @author Daniele Gobbetti and k3b
 */
public class IcsAsEventDto implements EventDto {
	private VEvent event;

	public IcsAsEventDto(VEvent event) {
		this.event = event;
	}

	@Override
	public String getId() {
		Uid value = event.getUid();
		
		if (value != null) {
			return value.getValue();
		}
		return null;
	}

	@Override
	public long getDtstart() {
		return getDate(event.getStartDate());
	}

	@Override
	public long getDtend() {
		return getDate(event.getEndDate());
	}

	@Override
	public String getTitle() {
		Summary value = event.getSummary();
		
		if (value != null) {
			return value.getValue();
		}
		return null;
	}

	@Override
	public String getDescription() {
		Description value = event.getDescription();
		
		if (value != null) {
			return value.getValue();
		}
		return null;
	}

	@Override
	public String getEventLocation() {
		Location value = event.getLocation();
		
		if (value != null) {
			return value.getValue();
		}
		return null;
	}

	@Override
	public String getDuration() {
		Duration value = event.getDuration();
		
		if (value != null) {
			return value.getValue();
		}
		return null;
	}

	@Override
	public String getEventTimezone() {
//		VTimeZone value = () event.getProperty(Property.TZID);
//		
//		if (value != null) {
//			return value.getName();
//		}
		return null;
	}

	@Override
	public String getRrule() {
		RRule value = (RRule) event.getProperty(Property.RRULE);
		
		if (value != null) {
			return value.getValue();
		}
		return null;
	}

	@Override
	public String getOrganizer() {
		Organizer value = event.getOrganizer();
		
		if (value != null) {
			return value.getValue();
		}
		return null;
	}

	@Override
	public String getCalendarId() {
		Uid value = event.getUid();
		
		if (value != null) {
			return value.getValue();
		}
		return null;
	}
	
	private long getDate(DateProperty date) {
		return (date != null) ? date.getDate().getTime() : 0;
	}
}
