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

import java.text.ParseException;
import java.util.List;

import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.model.component.*;
import net.fortuna.ical4j.model.property.*;

/**
 * Factory that converts generic EventDto to iCal4j-Implementation specific ics.
 * This class has no direct dependency to android so it can be run in a j2se-junit-integration test.<br/><br/>
 * 
 * @author k3b
 */
public class EventDto2IcsFactory {
	/**
	 * i.e. "-//Ben Fortuna//iCal4j 1.0//EN"
	 */
	private String applicationID;
	
	private Calendar calendar = null;

	/**
	 * creates a calendar
	 * @param applicationID used to identify the datasource i.e. "-//Ben Fortuna//iCal4j 1.0//EN"
	 */
	public EventDto2IcsFactory(String applicationID) {
		this.applicationID = applicationID;
	}
	
	/**
	 * gets the generated calendar.
	 * Use getCalendar().toString() to get the ics-file content 
	 */
	public Calendar getCalendar() {
		if (this.calendar == null) {
			this.calendar = new Calendar();
			calendar.getProperties().add(new ProdId(applicationID));
			calendar.getProperties().add(Version.VERSION_2_0);
			calendar.getProperties().add(CalScale.GREGORIAN);
		}
		return calendar;
	}

	/**
	 * adds a generic EventDto to ical4j-calendar
	 */
	public VEvent addEvent(EventDto eventData, TimeZone timezone) {
		PropertyList eventProperties = new PropertyList(); // event.getProperties();
		if (eventData.getId() != null) eventProperties.add(new Uid("acal-"+eventData.getCalendarId()+"-"+eventData.getId()));
		if (eventData.getDtStart() != 0) eventProperties.add(new DtStart(new DateTime( eventData.getDtStart())));
		if (eventData.getDtEnd() != 0) eventProperties.add(new DtEnd(new DateTime( eventData.getDtEnd())));

		if (eventData.getTitle() != null) eventProperties.add(new Summary(eventData.getTitle()));
		if (eventData.getDescription() != null) eventProperties.add(new Description(eventData.getDescription()));
		if (eventData.getEventLocation() != null) eventProperties.add(new Location(eventData.getEventLocation()));
		
		if (timezone != null) eventProperties.add(timezone);
		if (eventData.getDuration() != null) eventProperties.add(new Duration(new Dur( eventData.getDuration())));
		
		if (eventData.getRRule() != null) {
			try {
				eventProperties.add(new RRule(eventData.getRRule()));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}


        // #9
        ComponentList valarms = null;
        List<Integer> alarms = eventData.getAlarmMinutesBeforeEvent();
        if (alarms != null) {
            valarms = new ComponentList();
            for (Integer alarm : alarms) {
                Dur durationInMinutes = new Dur(0,0,- alarm, 0);
                valarms.add(new VAlarm(durationInMinutes));
            }
        }

        VEvent event = new VEvent(eventProperties, valarms);
		getCalendar().getComponents().add(event);
		return event;
	}
}
