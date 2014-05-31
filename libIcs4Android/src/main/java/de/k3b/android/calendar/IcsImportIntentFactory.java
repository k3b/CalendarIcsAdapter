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
package de.k3b.android.calendar;

import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Clazz;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.List;

import de.k3b.android.compat.Compat;
import de.k3b.calendar.EventDto;

/**
 * common api for IcsImportIntentFactory2 (for android2.x) and IcsImportIntentFactory4 (for android4.x) 
 * @author k3b
 *
 */
public class IcsImportIntentFactory {
    public static final String TAG = "ICS-Import";

    private IcsImportIntentImpl4 imp4 = null;
    private IcsImportIntentImpl2 imp2 = null;

    public IcsImportIntentFactory() {
        if (Compat.isCalendarContract4Available()) {
            imp4 = new IcsImportIntentImpl4();
        } else {
            imp2 = new IcsImportIntentImpl2();
        }
    }

	public Intent createImportIntent(Context context, final EventDto eventDto, VEvent event) {
        Intent result;
        if (imp4 != null)
            result = imp4.createImportIntent(context, eventDto, event);
        else
            result = imp2.createImportIntent(context, eventDto, event);

        // #9acalendar specific alarms
        List<Integer> alarms = eventDto.getAlarmMinutesBeforeEvent();
        if (alarms != null) {
            StringBuilder param = new StringBuilder();
            for (Integer alarmValue : alarms) {
                param.append(alarmValue).append("_1;");
            }
            result.putExtra("alarms", param.toString());
            if (Global.debugEnabled) {
                Log.d(IcsImportIntentFactory.TAG, "added ACalendar alarms " + param.toString());
            }
        }

        /* #8 additional extras supported by ACalendar according to "Intent Intercept  2.0.3.apk"
            - Long calendar_id = 1
            ? Integer availabilityStatus = 0
                    // android value
                    public static final int AVAILABILITY_BUSY = 0;
                    public static final int AVAILABILITY_FREE = 1;
                    public static final int AVAILABILITY_TENTATIVE = 2;
                VEvent
                    http://www.kanzaki.com/docs/ical/freebusy
                    http://www.kanzaki.com/docs/ical/vfreebusy.html (outside of to vevent)
                    VEvent.status
                        NEEDS-ACTION;COMPLETED;IN-PROCESS;CANCELLED
                X-MICROSOFT-CDO-BUSYSTATUS:FREE|TENTATIVE|BUSY
            v Integer visibility = 0
            - Boolean editMode=true
            v alarms=30_1;                // 30 mins before event with method 1 (Alarm)
         */
        result.putExtra("visibility", getAccessLevel(event.getClassification()));

        return result;

    }

    /**
     * translates from ical4jClazz to Android-Accesslevel
     */
    private int getAccessLevel(Clazz ical4jClazz)
    {
        if (ical4jClazz == Clazz.CONFIDENTIAL) return 1;
        if (ical4jClazz == Clazz.PUBLIC) return 3;
        if (ical4jClazz == Clazz.PRIVATE) return 2;
        return 0;
    }
}
