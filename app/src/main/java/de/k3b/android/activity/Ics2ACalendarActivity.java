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
package de.k3b.android.activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.component.VEvent;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Iterator;

import de.k3b.android.Global;
import de.k3b.android.calendar.IcsImportIntentFactory;
import de.k3b.calendar.ics.IcsAsEventDto;

/**
 * Invisible Pseudo-Activity that imports a ics-calendar-event-file into the android Calendar via
 * propopulated Android-Create-Event-Form.<br/>
 * Supports Android 4.0 and up. Runs on most Android 2.1 and up that have a calendar and a calendar provider.<br/><br/>
 *
 * @author k3b
 */
public class Ics2ACalendarActivity extends Activity {

    private IcsImportIntentFactory importFactory;
    private IcsAsEventDto eventDto;
    private Iterator eventIterator;
    private String currentEventName;

    /**
     * loads filecontents from stream
     */
    private static InputStream getStreamFromOtherSource(Context context, Uri contentUri) {
        ContentResolver res = context.getApplicationContext().getContentResolver();
        Uri uri = Uri.parse(contentUri.toString());
        InputStream is;
        try {
            is = res.openInputStream(uri);
        } catch (FileNotFoundException e) {
            is = new ByteArrayInputStream(new byte[0]);
        }
        return is;
    }

    /**
     * gets file uri from activity intent and opens re-populated "Add Event-To-Calendar"-Activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        Uri data = intent.getData();

        if (Global.debugEnabled) {
            Log.d(IcsImportIntentFactory.TAG, "Ics2ACalendarActivity begin " + data);
        }
        startCalendarImportActivity(data);
        importNextEvent();
    }

    /**
     * opens pre-populated "Add Event-To-Calendar"-Activity from contents of file-uri.
     */
    void startCalendarImportActivity(Uri calendarEventFileUri) {
        this.eventIterator = null;
        if (calendarEventFileUri != null) {
            try {
                if (Global.debugEnabled) {
                    Log.d(IcsImportIntentFactory.TAG, "opening " + calendarEventFileUri);
                }

                //use ical4j to parse the event
                CalendarBuilder cb = new CalendarBuilder();
                Calendar vcalendar = cb.build(getStreamFromOtherSource(this, calendarEventFileUri));

                if (Global.debugEnabled) {
                    Log.d(IcsImportIntentFactory.TAG, "loaded " + vcalendar);
                }

                if (vcalendar != null) {
                    this.importFactory = new IcsImportIntentFactory();

                    this.eventDto = new IcsAsEventDto(vcalendar);
                    this.eventIterator = vcalendar.getComponents(Component.VEVENT).iterator();
                }

            } catch (Exception e) {
                Log.e(IcsImportIntentFactory.TAG, "error processing " + calendarEventFileUri + " : " + e);
                e.printStackTrace();
            }
        }
    }

    private boolean importNextEvent() {
        if ((eventIterator != null) && eventIterator.hasNext()) {
            VEvent event = (VEvent) eventIterator.next();
            this.currentEventName = event.getName() + ":" + event.getSummary();

            if (Global.debugEnabled) {
                Log.d(IcsImportIntentFactory.TAG, "processing event " + currentEventName);
            }
            eventDto.set(event);

            Intent insertIntent = importFactory.createImportIntent(this, eventDto, event);

            startActivityForResult(insertIntent, 1);
            // this.startActivity(insertIntent);
            return true;
        }
        eventIterator = null;
        if (Global.debugEnabled) {
            Log.d(IcsImportIntentFactory.TAG, "Ics2ACalendarActivity done");
        }

        this.finish();
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (Global.debugEnabled) {
            Log.d(IcsImportIntentFactory.TAG, "onActivityResult: " + currentEventName + " - " + data + " " + resultCode);
        }

        importNextEvent();
    }
}
