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
public interface EventFilter {
    public enum RecurrenceType {ThisEvent, ThisAndAllFurtherEvents, AllEvents};

    /** if id should be copied. Default=true */
	public abstract boolean getId();

    /** if calendar id should be copied. Default=false */
    public abstract boolean getCalendarId();

    /** if location should be copied. Default=true */
	public abstract boolean getEventLocation();

    /** if time zone should be copied. Default=false */
	public abstract boolean getEventTimezone();

    /** if organizer should be copied. Default=false */
    public abstract boolean getOrganizer();

    /** if alarms should be copied. Default=false */
    public abstract boolean getAlarms();

    /** if how recurrence should be handled. Default=ThisEvent */
	public abstract RecurrenceType getRecurrenceType();

}
