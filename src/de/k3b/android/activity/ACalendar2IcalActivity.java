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

import de.k3b.android.calendar.ics.adapter.R;

/**
 * this is exactly the the same as ACalendar2IcsActivity except 
 * that is uses a different filename-extension. <br/>
 */
public class ACalendar2IcalActivity extends ACalendar2IcsActivity {

	/**
	 * This will differ between ics and ical
	 */
	protected CharSequence getChooserCaption() {
		return this.getText(R.string.export_chooser_caption_ical);
	}

	/**
	 * This will differ between ics and ical
	 */
	protected String getExportFileName() {
		return this.getText(R.string.export_filename_ical).toString();
	}
	
}
