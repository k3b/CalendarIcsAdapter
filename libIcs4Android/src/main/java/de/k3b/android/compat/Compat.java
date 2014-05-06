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
package de.k3b.android.compat;

import junit.framework.Assert;
import android.util.Log;
import de.k3b.android.calendar.ACalendar2IcsEngine;
import de.k3b.android.calendar.Global;

/**
 * Compatibility support library
 * @author k3b
 *
 */
public class Compat {

	/**
	 * @return true if current runtime supports buildin class CalendarContract
	 */
	public static boolean isCalendarContract4Available() {
		if (Global.USE_MOCK_CALENDAR) {
			Assert.assertEquals(true, isClassAvailable("de.k3b.android.compat.CalendarContract"));
			Assert.assertEquals(false, isClassAvailable("hello.World"));
		}
		return isClassAvailable("android.provider.CalendarContract"); // production implementation
	}
	
	/**
	 * @return true if current runtime support fullClassName
	 */
	private static boolean isClassAvailable(String fullClassName) {
		boolean result = false;
		try {
			if (null != Class.forName(fullClassName)) {
				result = true;
			}
		} catch (ClassNotFoundException e) {
			// ignore
		}
		if (Global.debugEnabled) {
			Log.d(ACalendar2IcsEngine.TAG, "Compat.isClassAvailable('"
				+ fullClassName
				+ "') : " + result);
		}
		return result;

	}
}
