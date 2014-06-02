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

import java.io.Closeable;

import android.database.Cursor;
import android.net.Uri;
import de.k3b.calendar.EventDto;
import de.k3b.calendar.EventFilter;

/**
 * Let an android specific calendarEvent cursor appear as a EventDto.
 * common api for ACalendarCursorAsEventDto2 (for android2.x) and ACalendarCursorAsEventDto4 (for android4.x)
 *
 * @author k3b
 *
 */
public interface ACalendarCursorAsEventDto extends EventDto, Closeable {

	Cursor queryByContentURI(Uri contentUri);

    /** #9 creates a copy of the data and downlownloads dependent subdata
     * @param filter*/
    EventDto loadFull(final EventFilter filter);
}
