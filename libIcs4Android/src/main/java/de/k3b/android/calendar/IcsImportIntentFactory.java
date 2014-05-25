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
import android.content.Context;
import android.content.Intent;

import de.k3b.android.compat.Compat;
import de.k3b.calendar.EventDto;
import de.k3b.calendar.IcsAsEventDto;

/**
 * common api for IcsImportIntentFactory2 (for android2.x) and IcsImportIntentFactory4 (for android4.x) 
 * @author k3b
 *
 */
public class IcsImportIntentFactory {
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

        return result;

    }

}
