/*
 * Copyright (C) 2014- k3b
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
package org.dgtale.android.calendar;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Simulates the api of an android calendar content provider that is compatibel with Samsung-Android2.2 Calendar.
 * Used internally for testing on emulator that has no calendar-app and no calendar-provder.<br/><br/>
 * @author k3b
 */
public class ACalendarMock extends SQLiteOpenHelper {
	public ACalendarMock(final Context context) {
		super(context, "calendar.db", null, 1);
		context.getDir("databases", Context.MODE_PRIVATE); // create dir if it does not exist
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		
		db.execSQL("CREATE TABLE Attendees (_id INTEGER PRIMARY KEY,event_id INTEGER,attendeeName TEXT,attendeeEmail TEXT,attendeeStatus INTEGER,attendeeRelationship INTEGER,attendeeType INTEGER)");
		db.execSQL("CREATE TABLE CalendarAlerts (_id INTEGER PRIMARY KEY,event_id INTEGER,begin INTEGER NOT NULL,end INTEGER NOT NULL,alarmTime INTEGER NOT NULL,creationTime INTEGER NOT NULL,receivedTime INTEGER NOT NULL,notifyTime INTEGER NOT NULL,state INTEGER NOT NULL,minutes INTEGER,UNIQUE (alarmTime, begin, event_id))");
		db.execSQL("CREATE TABLE CalendarCache (_id INTEGER PRIMARY KEY,key TEXT NOT NULL,value TEXT)");
		db.execSQL("CREATE TABLE CalendarMetaData (_id INTEGER PRIMARY KEY,localTimezone TEXT,minInstance INTEGER,maxInstance INTEGER)");
		db.execSQL("CREATE TABLE Calendars (_id INTEGER PRIMARY KEY,_sync_account TEXT,_sync_account_type TEXT,_sync_id TEXT,_sync_version TEXT,_sync_time TEXT,_sync_local_id INTEGER,_sync_dirty INTEGER,_sync_mark INTEGER,url TEXT,name TEXT,displayName TEXT,hidden INTEGER NOT NULL DEFAULT 0,color INTEGER,access_level INTEGER,selected INTEGER NOT NULL DEFAULT 1,sync_events INTEGER NOT NULL DEFAULT 0,location TEXT,timezone TEXT,ownerAccount TEXT, organizerCanRespond INTEGER NOT NULL DEFAULT 1)");
		db.execSQL("CREATE TABLE Events (_id INTEGER PRIMARY KEY,_sync_account TEXT,_sync_account_type TEXT,_sync_id TEXT,_sync_version TEXT,_sync_time TEXT,_sync_local_id INTEGER,_sync_dirty INTEGER,_sync_mark INTEGER,calendar_id INTEGER NOT NULL,htmlUri TEXT,title TEXT,eventLocation TEXT,description TEXT,eventStatus INTEGER,selfAttendeeStatus INTEGER NOT NULL DEFAULT 0,commentsUri TEXT,dtstart INTEGER,dtend INTEGER,eventTimezone TEXT,duration TEXT,allDay INTEGER NOT NULL DEFAULT 0,visibility INTEGER NOT NULL DEFAULT 0,transparency INTEGER NOT NULL DEFAULT 0,hasAlarm INTEGER NOT NULL DEFAULT 0,hasExtendedProperties INTEGER NOT NULL DEFAULT 0,rrule TEXT,rdate TEXT,exrule TEXT,exdate TEXT,originalEvent TEXT,originalInstanceTime INTEGER,originalAllDay INTEGER,lastDate INTEGER,hasAttendeeData INTEGER NOT NULL DEFAULT 0,guestsCanModify INTEGER NOT NULL DEFAULT 0,guestsCanInviteOthers INTEGER NOT NULL DEFAULT 1,guestsCanSeeGuests INTEGER NOT NULL DEFAULT 1,organizer STRING,deleted INTEGER NOT NULL DEFAULT 0,dtstart2 INTEGER,dtend2 INTEGER,eventTimezone2 TEXT,syncAdapterData TEXT)");
		db.execSQL("CREATE TABLE EventsRawTimes (_id INTEGER PRIMARY KEY,event_id INTEGER NOT NULL,dtstart2445 TEXT,dtend2445 TEXT,originalInstanceTime2445 TEXT,lastDate2445 TEXT,UNIQUE (event_id))");
		db.execSQL("CREATE TABLE ExtendedProperties (_id INTEGER PRIMARY KEY,event_id INTEGER,name TEXT,value TEXT)");
		db.execSQL("CREATE TABLE Instances (_id INTEGER PRIMARY KEY,event_id INTEGER,begin INTEGER,end INTEGER,startDay INTEGER,endDay INTEGER,startMinute INTEGER,endMinute INTEGER,UNIQUE (event_id, begin, end))");
		db.execSQL("CREATE TABLE Reminders (_id INTEGER PRIMARY KEY,event_id INTEGER,minutes INTEGER,method INTEGER NOT NULL DEFAULT 0)");
		db.execSQL("CREATE TABLE _sync_state (_id INTEGER PRIMARY KEY,account_name TEXT NOT NULL,account_type TEXT NOT NULL,data TEXT,UNIQUE(account_name, account_type))");
		db.execSQL("CREATE TABLE _sync_state_metadata (version INTEGER)");
		db.execSQL("CREATE TABLE android_metadata (locale TEXT)");
		db.execSQL("CREATE INDEX attendeesEventIdIndex ON Attendees (event_id)");
		db.execSQL("CREATE INDEX calendarAlertsEventIdIndex ON CalendarAlerts (event_id)");
		db.execSQL("CREATE INDEX eventSyncAccountAndIdIndex ON Events (_sync_account_type, _sync_account, _sync_id)");
		db.execSQL("CREATE INDEX eventsCalendarIdIndex ON Events (calendar_id)");
		db.execSQL("CREATE INDEX extendedPropertiesEventIdIndex ON ExtendedProperties (event_id)");
		db.execSQL("CREATE INDEX instancesStartDayIndex ON Instances (startDay)");
		db.execSQL("CREATE INDEX remindersEventIdIndex ON Reminders (event_id)");
		db.execSQL("CREATE VIEW view_events AS SELECT Events._id AS _id,htmlUri,title,description,eventLocation,eventStatus,selfAttendeeStatus,commentsUri,dtstart,dtend,duration,eventTimezone,allDay,visibility,timezone,selected,access_level,transparency,color,hasAlarm,hasExtendedProperties,rrule,rdate,exrule,exdate,originalEvent,originalInstanceTime,originalAllDay,lastDate,hasAttendeeData,calendar_id,guestsCanInviteOthers,guestsCanModify,guestsCanSeeGuests,organizer,deleted,Events._sync_id AS _sync_id,Events._sync_version AS _sync_version,Events._sync_dirty AS _sync_dirty,Events._sync_account AS _sync_account,Events._sync_account_type AS _sync_account_type,Events._sync_time AS _sync_time,Events._sync_local_id AS _sync_local_id,Events._sync_mark AS _sync_mark,url,ownerAccount,sync_events FROM Events JOIN Calendars ON (Events.calendar_id=Calendars._id)");
		db.execSQL("CREATE TRIGGER calendar_cleanup DELETE ON Calendars BEGIN DELETE FROM Events WHERE calendar_id = old._id;END");
		db.execSQL("CREATE TRIGGER events_insert AFTER INSERT ON Events BEGIN UPDATE Events SET _sync_account=(SELECT _sync_account FROM Calendars WHERE Calendars._id=new.calendar_id),_sync_account_type=(SELECT _sync_account_type FROM Calendars WHERE Calendars._id=new.calendar_id) WHERE Events._id=new._id;END");
		db.execSQL("CREATE TRIGGER events_cleanup_delete DELETE ON Events BEGIN DELETE FROM Instances WHERE event_id = old._id;DELETE FROM EventsRawTimes WHERE event_id = old._id;DELETE FROM Attendees WHERE event_id = old._id;DELETE FROM Reminders WHERE event_id = old._id;DELETE FROM CalendarAlerts WHERE event_id = old._id;DELETE FROM ExtendedProperties WHERE event_id = old._id;END");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
}
 