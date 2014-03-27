package de.k3b.data.calendar;

import java.text.ParseException;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.Dur;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.component.*;
import net.fortuna.ical4j.model.property.*;

/**
 * Wrapper around iCal4j
 */
public class CalendarFactory {
	/**
	 * i.e. "-//Ben Fortuna//iCal4j 1.0//EN"
	 */
	private String applicationID;
	
	private Calendar calendar = null;

	/**
	 * creates a calendar
	 * @param applicationID used to identify the datasource i.e. "-//Ben Fortuna//iCal4j 1.0//EN"
	 */
	public CalendarFactory(String applicationID) {
		this.applicationID = applicationID;
	}
	
	public Calendar getCalendar() {
		if (this.calendar == null) {
			this.calendar = new Calendar();
			calendar.getProperties().add(new ProdId(applicationID));
			calendar.getProperties().add(Version.VERSION_2_0);
			calendar.getProperties().add(CalScale.GREGORIAN);
		}
		return calendar;
	}

	public VEvent addEvent(EventDto data, TimeZone timezone) {
		VEvent event = new VEvent();
		PropertyList eventProperties = event.getProperties();
		if (data.getId() != null) eventProperties.add(new Uid("acal-"+data.getCalendarId()+"-"+data.getId()));
		if (data.getDtstart() != 0) eventProperties.add(new DtStart(new Date( data.getDtstart())));
		if (data.getDtend() != 0) eventProperties.add(new DtEnd(new Date( data.getDtend())));

		if (data.getTitle() != null) eventProperties.add(new Summary(data.getTitle()));
		if (data.getDescription() != null) eventProperties.add(new Description(data.getDescription()));
		if (data.getEventLocation() != null) eventProperties.add(new Location(data.getEventLocation()));
		
		if (timezone != null) eventProperties.add(timezone);
		if (data.getDuration() != null) eventProperties.add(new Duration(new Dur( data.getDuration())));
		
		if (data.getRrule() != null) {
			try {
				eventProperties.add(new RRule(data.getRrule()));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		getCalendar().getComponents().add(event);
		return event;
	}
}
