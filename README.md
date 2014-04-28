# CalendarIcsAdapter ( android.calendar.ics.adapter )

**CalendarIcsAdapter** is a small free and open source app for Android 2.1 and up
that converts between 
android calendar events and 
[calendar entry files](https://en.wikipedia.org/wiki/ICalendar) (`*.ics` or `*.ical`).

Use a filemanager (i.e. [[org.openintents.filemanager|https://f-droid.org/wiki/page/org.openintents.filemanager]]), emailclient (i.e. [[k9 mail|https://f-droid.org/wiki/page/com.fsck.k9]]) or bluetooth to import or export
android calendar entries files or attachments.

The android.calendar.ics.adapter has no gui of its own. Instead it hooks into the android system.
* If you open an event from the android calendar you can choose between `view event` and `send event to ...`.
  * Selecting `Send Event to ...` allows you to send a calendar entry file to any installed android application that can handle file attachments. I.e.
    * android emailclient
    * bluetooth 
* If you open an event from file/attachment you get a prepopulated "add to calender-form". This works with
	* get an email invitation with an attached calendar entry
	* download a calendar entry file from a webpage
	* receive a calendar entry via bluetooth`(*)`
	* open a calendar entry file in a filemanager.

`(*)` there is an [android issue that blocks receiving `*.ics` files via bluetooth](https://github.com/k3b/CalendarIcsAdapter/issues/2). 
If you rename the fileextension from `*.ics` to `*.ical` it will work with android bluetooth.

## Permissions

* android.permission.READ_CALENDAR 
  *	to export event data for export
* android.permission.WRITE_EXTERNAL_STORAGE 
  *	to save event data to local file. This
  *	must be readable by other apps to make SendTo work. Unfortunately FileProvider 
  *	does not work on my android 2.2 togehter with send to bluetooth.
	
At the moment CalendarIcsAdapter does not need calendar-write permissions.

When importing the original .ics file is 
parsed and the event is added using the native calendar application. 
This way you are free to change 
the event details before saving.
 
## Building

There are 3 branches in the git repository at https://github.com/k3b/CalendarIcsAdapter.git
* **master** ics-import only that requires no additional permisions based on [[org.dgtale.icsimport]]
* **development** current development version for eclipse that include local lib/*.jars and is not suitable for fdroid-builds
* **release-fdroid** official fdroid build version with gradle-build and no local lib/*.jars.
  * official fdroid releases are tagged with v*.*.* (for example v1.5.4)

The project should build as-is using 
* ant
  * `ant clean release install` or 
  * `ant clean debug install` or 
* using Gradle-1.11 and android-buildtoos-19.0.3
  * `gradle clean installRelease`
  * `gradle clean installDebug`

The libraries contained  in this project are taken from the [ical4j project](http://ical4j.sf.net/).
A binary version of the app is [available](https://f-droid.org/wiki/page/de.k3b.android.calendar.ics.adapter) in the android opensource appstore [f-droid](https://f-droid.org/)

If you find any issues look at the [CalendarIcsAdapter issue tracker](https://github.com/k3b/CalendarIcsAdapter/issues)
