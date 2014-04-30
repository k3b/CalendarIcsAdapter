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

/**
 * common api for IcsImportIntentFactory2 (for android2.x) and IcsImportIntentFactory4 (for android4.x) 
 * @author k3b
 *
 */
public interface IcsImportIntentFactory {

	Intent createImportIntent(Context context, VEvent event);

}
