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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.FileProvider;

/**
 * Android specific class to store result file via 
 * cachefile plus content.FileProvider that does note require android 
 * android.permission.WRITE_EXTERNAL_STORAGE
 */
public class CalenderDataUriContentFile {
	protected Context context;

	public CalenderDataUriContentFile(Context context) {
		this.context = context;
	}
	
	public  void writeStringToTextFile(File file, String content) throws IOException{
	    FileOutputStream f1 = new FileOutputStream(file,false); //True = Append to file, false = Overwrite
	    PrintStream p = new PrintStream(f1);
	    p.print(content);
	    p.close();
	    f1.close();
	}
	
	/**
	 * cachefile content.FileProvider specific implementention that does not need local file permissions.<br/>
	 */
	public  Uri getUriForFile(final File icsFile) {
		// let the FileProvider generate an URI for this private icsFIle
		final Uri uri = FileProvider.getUriForFile(this.context, "org.dgtale.calendar.adapter", icsFile);
		return uri;
	}

	/**
	 * cachefile content.FileProvider specific implementention that does not need local file permissions.<br/>
	 */
	public File getOuputFile() {
		final File path = getOutputDir();
		final File icsFIle = new File(path, "FromAndroidCalendar.ics");
		return icsFIle;
	}

	/**
	 * get or create dir where ics file will be stored.<br/>
	 */
	protected File getOutputDir() {
		// for details see https://developer.android.com/reference/android/support/v4/content/FileProvider.html

		// concatenate the internal cache folder with the document its path and filename
		File path = new File(this.context.getCacheDir(), "ics");
		path.mkdirs();
		return path;
	}
}
