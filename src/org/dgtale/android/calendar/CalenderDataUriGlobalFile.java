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
import java.io.IOException;
import java.security.acl.Permission;

import android.Manifest.permission;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.util.Log;

/**
 * Android specific class to store result file via 
 * global file that requirees android 
 * android.permission.WRITE_EXTERNAL_STORAGE
 */
public class CalenderDataUriGlobalFile extends CalenderDataUriContentFile {
	public CalenderDataUriGlobalFile(Context context) {
		super(context);
	}
	/**
	 * global readable File.<br/>
	 */
	public  Uri getUriForFile(final File icsFile) {
		final Uri uri = Uri.fromFile(icsFile);
		return uri;
	}

	/**
	 * get or create dir where ics file will be stored.<br/>
	 */
	protected File getOutputDir() {
		File path = new File(Environment.getExternalStorageDirectory(),"tmp");
		path.mkdirs();

		return path;
	}
	
	/**
	 * @return true if  ExternalStorage is mounted and app has permission WRITE_EXTERNAL_STORAGE.
	 */
	public static boolean isAvailable(Context context) {
		String state = Environment.getExternalStorageState();
				
        if (Environment.MEDIA_MOUNTED.equals(state)) {
    		PackageManager manager = context.getPackageManager();
    		int hasPermission = manager.checkPermission ("android.permission.WRITE_EXTERNAL_STORAGE", context.getApplicationContext().getPackageName());
    		if (hasPermission == manager.PERMISSION_GRANTED) {
    		    return true;
    		} // else no permission

        } // else not mounted
        return false;
	}
}

