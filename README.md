todo k3b

>	import via eventData-Interface vcal->eventData->androidIntent
	  commit all + backup + git-rebranch (siehe ff-download)
	  todo test ob noch ok
	  eigene klasse  

	j2se-Junit-Integration test
		EventDtoSimple
		ics->EventDto->ics
	sendto parameter subject body
		EventDto2txt
	new readme with workflow pc-calendar<->email<->android-calendar


	howto
		enable/disable intents at runtime



CalendarIcsAdapter
=========

CalendarIcsAdapter is a small free and open source app for Android 2.1 and up that intercepts the VIEW intent for text/calendar files and allows to add it to the calendar. 
At the moment it does not need any calendar-related permissions because it acts as a bridge. The original .ics file is parsed and the event is 
added using the native calendar application. This way you are free to change the event details before saving.

Building
========

The project should build as-is. Be aware that you need to use ant. The libraries contained in this project are taken from the [ical4j project](http://ical4j.sf.net/).


TODO
====

The following features have been tested and reported working but more feedback is welcome:
* ICS files containing multiple events (tested with 15 events in the same file. Be aware that you need to go back after each event insertion). This feature was added in version 1.1

At the moment the project needs testing. A lot of testing! Especially this aspects are currently untested:
* Time Zone
* Full-day events 
* ...
