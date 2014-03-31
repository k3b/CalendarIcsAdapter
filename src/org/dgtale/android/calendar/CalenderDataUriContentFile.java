package org.dgtale.android.calendar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.FileProvider;

public class CalenderDataUriContentFile {
	private Context context;

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
	public  Uri getUriForFile(final File icsFIle) {
		// let the FileProvider generate an URI for this private icsFIle
		final Uri uri = FileProvider.getUriForFile(this.context, "org.dgtale.calendar.adapter", icsFIle);
		return uri;
	}

	/**
	 * cachefile content.FileProvider specific implementention that does not need local file permissions.<br/>
	 */
	public File getOuputFile() {
		// for details see https://developer.android.com/reference/android/support/v4/content/FileProvider.html

		// concatenate the internal cache folder with the document its path and filename
		File path = new File(this.context.getCacheDir(), "ics");
		path.mkdirs();
		final File icsFIle = new File(path, "FromAndroidCalendar.ics");
		return icsFIle;
	}
}
