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
package de.k3b.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import net.fortuna.ical4j.model.Calendar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import de.k3b.android.calendar.ACalendar2IcsEngine;
import de.k3b.android.calendar.ACalendarCursor;
import de.k3b.android.Global;
import de.k3b.android.calendar.ics.adapter.R;
import de.k3b.calendar.EventDto;
import de.k3b.calendar.EventFilter;
import de.k3b.calendar.EventFilterDto;
import de.k3b.calendar.ics.IcsAsEventDto;
//import android.provider.CalendarContract from android 4.0 is replaced by local CalendarContract so it is runnable from android 2.1 

/**
 * Invisible Pseudo-Activity that exports a ics-calendar-event-file from the android Calendar.<br/>
 * Supports Android 4.0 and up. Runs on most Android 2.1 and up that have a calendar and a calendar provider. <br/><br/>
 *
 * @author k3b
 */
public class ACalendar2IcsActivity extends Activity {
    private ACalendar2IcsEngine engine = null;

    /** controls, wich data elements will be exported.
     * TODO: make configurable via gui */
    private final EventFilter filter = EventFilterDto.ALL;

    /**
     * This will differ between ics and ical
     */
    protected CharSequence getChooserCaption() {
        return this.getText(R.string.export_chooser_caption_ics);
    }

    /**
     * This will differ between ics and ical
     */
    protected String getExportFileName() {
        return this.getText(R.string.export_filename_ics).toString();
    }

    /**
     * Gets the calendar-event-content-uri from activity intent and exports it to opens re-populated "Add Event-To-Calendar"-Activity.
     * if Global.USE_MOCK_CALENDAR==true it opens "content://com.android.calendar/events/1" from mock-database
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        Uri data = intent.getData();

        if ((Global.USE_MOCK_CALENDAR) && (data == null)) {
            data = ACalendarCursor.createContentUri("events", "1");
            // data = ACalendarCursor.createContentUri("events");
        }

        // #6 fix export: recurring event:email-subject date should be occurence-start (long-extra-beginTime) instead of rule-start
        // CalendarContract.EXTRA_EVENT_BEGIN_TIME or EXTRA_EVENT_END_TIME
        long beginTime = intent.getLongExtra("beginTime", 0);
        long endTime = intent.getLongExtra("endTime", 0);

        if (data != null) {
            try {
                if (engine == null) {
                    if (Global.debugEnabled) {
                        Log.d(ACalendar2IcsEngine.TAG, "creating ACalendar2IcsEngine");
                    }
                    engine = new ACalendar2IcsEngine(this.getApplication(), filter, Global.USE_MOCK_CALENDAR);
                }

                if (Global.debugEnabled) {
                    Log.d(ACalendar2IcsEngine.TAG, "opening " + data);
                }
                Calendar vcalendar = engine.export(data, beginTime, endTime);

                if (vcalendar != null) {
                    EventDto event = new IcsAsEventDto(vcalendar);

                    String mailSubject = getMailSubject(event, beginTime);
                    String description = getMailDescription(event);
                    if (Global.debugEnabled) {
                        Log.d(ACalendar2IcsEngine.TAG, "sending '" + mailSubject + "'");
                    }
                    sendIcsTo(mailSubject, description, vcalendar.toString());
                }

            } catch (Exception e) {
                Log.e(ACalendar2IcsEngine.TAG, "error processing " + data + " : " + e);
                e.printStackTrace();
            }
        }
        if (Global.debugEnabled) {
            Log.d(ACalendar2IcsEngine.TAG, "export done");
        }
        this.finish();
    }

    /**
     * closes all allocated resources.
     */
    @Override
    protected void onDestroy() {
        if (engine != null) {
            if (Global.debugEnabled) {
                Log.d(ACalendar2IcsEngine.TAG, "destroying ACalendar2IcsEngine");
            }
            engine.close();
        }
        engine = null;

        super.onDestroy();
    }

    /**
     * calculates mail-subject from event.
     * If the ics is send via email-attachment, the send mail app is pre-populated with a mail-subject
     */
    private String getMailSubject(EventDto event, long beginTime) {
        if (event != null) {
            String date = "";

            long start = (hasRecurrence(event)) ? event.getDtStart() : beginTime;
            if (start != 0) {
                Date dtStart = new Date(start);

                Locale locale = Locale.getDefault();
                DateFormat shortTimeformatter = DateFormat.getDateInstance(java.text.DateFormat.SHORT, locale);
                date = shortTimeformatter.format(dtStart);
            }
            return String.format(this.getString(R.string.export_mail_subject).toString(),
                    date, event.getTitle());
        }
        return null;
    }

    private static boolean hasRecurrence(EventDto event) {
        if (event != null) {
            return !isEmpty(event.getRRule()) || !isEmpty(event.getRDate()) ;
        }
        return false;
    }

    private static boolean isEmpty(String value) {
        return ((value == null) || (value.length() == 0));
    }

    /**
     * calculates mail-description from event.
     * If the ics is send via email-attachment, the send mail app is pre-populated with content.
     */
    protected String getMailDescription(EventDto event) {
        if (event != null) {
            String date = "";

            long start = event.getDtStart();
            if (start != 0) {
                Date dtStart = new Date(start);

                Locale locale = Locale.getDefault();
                DateFormat dateFormatter = DateFormat.getDateInstance(java.text.DateFormat.FULL, locale);
                DateFormat timeFormatter = DateFormat.getTimeInstance(java.text.DateFormat.SHORT, locale);
                date = dateFormatter.format(dtStart) + " " + timeFormatter.format(dtStart);
            }
            return String.format(this.getString(R.string.export_mail_content).toString(),
                    date, event.getEventLocation(), event.getDescription(), getAppVersionName());
        }
        return null;
    }

    /**
     * used to add a banner to the mail - content.
     */
    private String getAppVersionName() {
        String versionName = "";
        try {

            PackageManager packageManager = this.getPackageManager();
            if (packageManager != null) {
                versionName = "-" + packageManager
                        .getPackageInfo(this.getPackageName(), 0).versionName;
            }
        } catch (final NameNotFoundException e) {
        }
        return getString(R.string.app_name) + versionName;
    }

    /**
     * Opens android "sendTo"-chooser with propopulated data:
     */
    private void sendIcsTo(String mailSubject, String mailBody, String mailAttachmentContent) throws IOException {
        final File icsFIle = this.getOutputFile();
        this.writeStringToTextFile(icsFIle, mailAttachmentContent.toString());

        final Uri uri = this.getUriForFile(icsFIle);

        final Intent outIntent = new Intent()
                .setAction(Intent.ACTION_SEND)
                .setDataAndType(uri, this.getText(R.string.export_file_mime_type).toString())
                .putExtra(Intent.EXTRA_STREAM, uri)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        if (mailSubject != null) {
            outIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, mailSubject);
        }

        if (mailBody != null) {
            outIntent.putExtra(android.content.Intent.EXTRA_TEXT, mailBody);
        }

        if (Global.debugEnabled) {
            Log.d(ACalendar2IcsEngine.TAG, "Starting intent " + outIntent);
        }
        this.startActivity(Intent.createChooser(outIntent, getChooserCaption()));
    }

    /**
     * global readable File.<br/>
     */
    private Uri getUriForFile(final File icsFile) {
        final Uri uri = Uri.fromFile(icsFile);
        return uri;
    }

    /**
     * writes attachment-content-to global-readable file
     */
    private void writeStringToTextFile(File file, String content) throws IOException {
        if (Global.debugEnabled) {
            Log.d(ACalendar2IcsEngine.TAG, "Creating file " + file);
            Log.d(ACalendar2IcsEngine.TAG, "content");
        }

        FileOutputStream f1 = new FileOutputStream(file, false); //True = Append to file, false = Overwrite
        PrintStream p = new PrintStream(f1);
        p.print(content);
        p.close();
        f1.close();
    }

    /**
     * get File where attachment-content will be exported to.<br/>
     */
    private File getOutputFile() {
        final File path = getOutputDir();
        final File icsFIle = new File(path, getExportFileName());
        return icsFIle;
    }

    /**
     * get or create dir where ics file will be stored.<br/>
     */
    private File getOutputDir() {
        File path = new File(Environment.getExternalStorageDirectory(), this.getText(R.string.export_file_public_folder).toString());
        path.mkdirs();

        return path;
    }
}
