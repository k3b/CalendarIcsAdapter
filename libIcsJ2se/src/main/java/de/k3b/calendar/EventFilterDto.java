package de.k3b.calendar;
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
/**
 * Filter that controlls which event elements should be processed.<br/><br/>
 *
 * @author k3b
 */
public class EventFilterDto implements EventFilter {
    private boolean id = true;
    private boolean calendarId = false;
    private boolean eventLocation = true;
    private boolean eventTimezone = false;
    private boolean organizer = false;
    private boolean alarms = false;
    private RecurrenceType recurrenceType = RecurrenceType.ThisEvent;

    public static final EventFilterDto ALL = new EventFilterDto().setAll(true);
    public static final EventFilterDto DEFAULTS = new EventFilterDto();
    public static final EventFilterDto MINIMAL = new EventFilterDto().setAll(false);

    public EventFilterDto() {};

    public EventFilterDto(EventFilter src) {setAll(src);};

    /**
     * if id should be copied. Default=true
     */
    @Override
    public boolean getId() {
        return id;
    }

    /**
     * if calendar id should be copied. Default=false
     */
    @Override
    public boolean getCalendarId() {
        return calendarId;
    }

    /**
     * if location should be copied. Default=true
     */
    @Override
    public boolean getEventLocation() {
        return eventLocation;
    }

    /**
     * if time zone should be copied. Default=false
     */
    @Override
    public boolean getEventTimezone() {
        return eventTimezone;
    }

    /**
     * if organizer should be copied. Default=false
     */
    @Override
    public boolean getOrganizer() {
        return organizer;
    }

    /**
     * if alarms should be copied. Default=false
     */
    @Override
    public boolean getAlarms() {
        return alarms;
    }

    /**
     * if how recurrence should be handled. Default=ThisEvent
     */
    @Override
    public RecurrenceType getRecurrenceType() {
        return recurrenceType;
    }
    /**
     * if id should be copied. Default=true
     */

    public EventFilterDto setId(boolean enabled) {
        id = enabled; return this;
    }

    /**
     * if calendar id should be copied. Default=false
     */

    public EventFilterDto setCalendarId(boolean enabled) {
        calendarId = enabled; return this;
    }

    /**
     * if location should be copied. Default=true
     */

    public EventFilterDto setEventLocation(boolean enabled) {
        eventLocation = enabled; return this;
    }

    /**
     * if time zone should be copied. Default=false
     */

    public EventFilterDto setEventTimezone(boolean enabled) {
        eventTimezone = enabled; return this;
    }

    /**
     * if organizer should be copied. Default=false
     */

    public EventFilterDto setOrganizer(boolean enabled) {
        organizer = enabled; return this;
    }

    /**
     * if alarms should be copied. Default=false
     */

    public EventFilterDto setAlarms(boolean enabled) {
        alarms = enabled; return this;
    }

    /**
     * if how recurrence should be handled. Default=ThisEvent
     */

    public EventFilterDto setRecurrenceType(RecurrenceType enabled) {
        recurrenceType = enabled; return this;
    }

    /** set all to the same value */
    public EventFilterDto setAll(boolean enabled) {
        setId(enabled);
        setCalendarId(enabled);
        setEventLocation(enabled);
        setEventTimezone(enabled);
        setOrganizer(enabled);
        setAlarms(enabled);
        setRecurrenceType((enabled)  ? RecurrenceType.AllEvents : RecurrenceType.ThisEvent);
        return this;
    }

    /** copy operator */
    public EventFilterDto setAll(EventFilter src) {
        setId(src.getId());
        setCalendarId(src.getCalendarId());
        setEventLocation(src.getEventLocation());
        setEventTimezone(src.getEventTimezone());
        setOrganizer(src.getOrganizer());
        setAlarms(src.getAlarms());
        setRecurrenceType(src.getRecurrenceType());
        return this;
    }
}
