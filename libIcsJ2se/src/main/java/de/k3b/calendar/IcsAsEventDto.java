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
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.component.VAlarm;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.*;

import java.util.*;

import de.k3b.util.DateTimeUtil;

/**
 * Facade that makes a ical4j-vevent implementation specific ics appear as EventDto.<br/>
 * 
 * This class has no direct dependency to android so it can be run in a j2se-junit-integration test.<br/><br/>
 * 
 * @author Daniele Gobbetti and k3b
 */
public class IcsAsEventDto implements EventDto {
	private VEvent event;
    private String calendarId = null;

    public IcsAsEventDto(Calendar calendar) {
        Property calendarId = calendar.getProperty(Property.UID);
        this.calendarId = (calendarId != null) ? calendarId.getValue() : null;
        set((VEvent) calendar.getComponent(Component.VEVENT));
	}

    public IcsAsEventDto set(final VEvent event) {
        this.event = event;
        return this;
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
        DateProperty vdate = (this.event == null) ? null : event.getStartDate();
        TimeZone vtimezone = (vdate != null) ? vdate.getTimeZone() : null;

		return (vtimezone != null) ? vtimezone.getID() : null;
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
        PropertyList strDates = (this.event != null) ? this.event.getProperties(ExDate.RDATE) : null;
        return getDates(strDates);
    }

    /** #11 formatted as komma seperated list of iso-utc-dates. Example: '20090103T093000Z,20110101T093000Z' */
    @Override
    public String getExtDates() {
        PropertyList strDates = (this.event != null) ? this.event.getProperties(ExDate.EXDATE) : null;
        return getDates(strDates);
    }

    private String getDates(final PropertyList strDates) {
        if ((strDates != null) && (strDates.size() > 0)) {
            StringBuilder sb = new StringBuilder();
            for(Object _ex : strDates) {
                DateListProperty ex = (DateListProperty) _ex;
                DateList dates = ex.getDates();

                for(Object _date : dates) {
                    if (sb.length() > 0) {
                        sb.append(",");
                    }
                    sb.append(DateTimeUtil.toIsoDate((Date) _date));
                }
            }
            return sb.toString();
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
		return calendarId;
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
