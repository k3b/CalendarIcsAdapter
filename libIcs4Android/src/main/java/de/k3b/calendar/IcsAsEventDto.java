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
package de.k3b.calendar;

import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.model.component.VAlarm;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Facade that makes a ical4j-vevent implementation specific ics appear as EventDto.<br/>
 * 
 * This class has no direct dependency to android so it can be run in a j2se-junit-integration test.<br/><br/>
 * 
 * @author Daniele Gobbetti and k3b
 */
public class IcsAsEventDto implements EventDto {
	private final VEvent event;

	public IcsAsEventDto(Calendar calendar) {
		this.event = (VEvent) calendar.getComponent(Component.VEVENT);
	}

	public IcsAsEventDto(VEvent event) {
		this.event = event;
	}

	@Override
	public String getId() {
		Uid value = (this.event == null) ? null : event.getUid();
		
		if (value != null) {
			return value.getValue();
		}
		return null;
	}

	@Override
	public long getDtStart() {
		return (this.event == null) ? null : getDate(event.getStartDate());
	}

	@Override
	public long getDtEnd() {
		return (this.event == null) ? null : getDate(event.getEndDate());
	}

	@Override
	public String getTitle() {
		Summary value = (this.event == null) ? null : event.getSummary();
		
		if (value != null) {
			return value.getValue();
		}
		return null;
	}

	@Override
	public String getDescription() {
		Description value = (this.event == null) ? null : event.getDescription();
		
		if (value != null) {
			return value.getValue();
		}
		return null;
	}

	@Override
	public String getEventLocation() {
		Location value = (this.event == null) ? null : event.getLocation();
		
		if (value != null) {
			return value.getValue();
		}
		return null;
	}

	@Override
	public String getDuration() {
		Duration value = (this.event == null) ? null : event.getDuration();
		
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
	public String getRRule() {
		RRule value = (this.event == null) ? null :  (RRule) event.getProperty(Property.RRULE);
		
		if (value != null) {
			return value.getValue();
		}
		return null;
	}

    @Override
    public String getRDate() {
        RDate value = (this.event == null) ? null :  (RDate) event.getProperty(Property.RDATE);

        if (value != null) {
            return value.getValue();
        }
        return null;
    }

    @Override
	public String getOrganizer() {
		Organizer value = (this.event == null) ? null : event.getOrganizer();
		
		if (value != null) {
			return value.getValue();
		}
		return null;
	}

	// not supported by ics-calendar
	@Override
	public String getCalendarId() {
		/*
		Uid value = (this.event == null) ? null : event.getUid();
		
		if (value != null) {
			return value.getValue();
		}
		*/
		return null;
	}

    /** #9 the alarm(s) should trigger x menutes before the event. null means no alarms. */
    @Override
    public List<Integer> getAlarmMinutesBeforeEvent() {
        // VEvent.Alarms[].Trigger as negatige Duration in minutes
        List<Integer> alarmMinutesBeforeEvent = null;
        ComponentList alarms = (this.event == null) ? null : event.getAlarms();

        if ((alarms != null) && (alarms.size() > 0)) {
            alarmMinutesBeforeEvent = new ArrayList<Integer>();
            final Iterator iterator = alarms.iterator();
            while (iterator.hasNext()) {
                final Component component = (Component) iterator.next();

                if ((component instanceof VAlarm)) {
                    // http://www.kanzaki.com/docs/ical/trigger.html
                    // Example TRIGGER:-P15M
                    // A trigger set 15 minutes prior to the start of the event or to-do.
                    Trigger trigger = ((VAlarm) component).getTrigger();

                    Dur duration = (trigger != null) ? trigger.getDuration() : null;
                    if (duration != null) {
                        int minutes = duration.getMinutes();
                        if (minutes <= 0) {
                            // minutes before event
                            alarmMinutesBeforeEvent.add(-minutes);
                        } else {
                            alarmMinutesBeforeEvent.add(minutes);
                        }
                    }
                }

            }
        }
        return alarmMinutesBeforeEvent;
    }

    private long getDate(DateProperty date) {
		return (date != null) ? date.getDate().getTime() : 0;
	}
}
