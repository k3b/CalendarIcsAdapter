CalendarIcsAdapter
=========

CalendarIcsAdapter is a small free and open source app for Android 2.1 and up
that converts between android calendar events and ics-vevent-files.
In other words you can invite and get invitations for calendar events via email 
on your android device. 

Technically CalendarIcsAdapter intercepts the VIEW intent for 
ics/vevent/text-calendar files and allows to add it to the android calendar.
It also  intercepts the VIEW intent for 
for the calendar-viewer and allows to generate ics/vevent filecontent that 
can be send via email.

Permissions
===========
android.permission.READ_CALENDAR 
	to export event data for export
android.permission.WRITE_EXTERNAL_STORAGE 
	to save event data to local file. This
	must be readable by other apps to make SendTo work. Unfortunately FileProvider 
	does not work on my android 2.2 togehter with send to bluetooth.
	
At the moment CalendarIcsAdapter does not need calendar-write permissions.

When importing the original .ics file is 
parsed and the event is added using the native calendar application. 
This way you are free to change 
the event details before saving.
 
Building
========

The project should build as-is. Be aware that you need to use ant. The libraries contained 
in this project are taken from the [ical4j project](http://ical4j.sf.net/).


TODO
====

The following features have been tested and reported working but more feedback is welcome:
* ICS files containing multiple events (tested with 15 events in the same file. Be aware that you need to go back after each event insertion). This feature was added in version 1.1

At the moment the project needs testing. A lot of testing! Especially this aspects are currently untested:
* Time Zone
* ...
