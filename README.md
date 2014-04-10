# CalendarIcsAdapter ( android.calendar.ics.adapter )

**CalendarIcsAdapter** is a small free and open source app for Android 2.1 and up
that converts between android calendar events and calendar entry files (`*.ics` or `*.ical`).

If you open a calendar entry file you can choose to add this event to your android calendar.

This works with
* get an email invitation with an attached calendar entry
* download a calendar entry file from a webpage
* receive a calendar entry via bluetooth`(*)`
* open a calendar entry file in a filemanager

If you want to open a calendar entry in your android calendar you can choose
* view event with standard calendar viewer
* Send Event to ...

Selecting `Send Event to ...` allows you to send a calendar entry file via email, bluetooth`(*)` or any other installed android application that can handle it.

`(*)` there is an [android issue that blocks receiving `*.ics` files via bluetooth](https://github.com/k3b/CalendarIcsAdapter/issues/2). If you rename the fileextension to `*.ical` it will work with android bluetooth.

Technically CalendarIcsAdapter intercepts the VIEW intent for 
vevent/text-calendar files (*ics or *.ical or *.icalendar or mimetype text/calendar) 
and allows to add it to the android calendar.

It also  intercepts the VIEW intent for 
for the calendar-viewer and allows to generate ics or ical vevent filecontent that 
can be send via email.

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

The project should build as-is using `ant clean release install` or `ant clean debug install`. 

The libraries contained  in this project are taken from the [ical4j project](http://ical4j.sf.net/).

The app should be available in the android opensource appstore [f-droid](https://f-droid.org/)

If you find any issues look at the [CalendarIcsAdapter issue tracker](https://github.com/k3b/CalendarIcsAdapter/issues)
