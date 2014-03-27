package de.k3b.data.calendar;

import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.DateProperty;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.Duration;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.Organizer;
import net.fortuna.ical4j.model.property.RRule;
import net.fortuna.ical4j.model.property.Summary;
import net.fortuna.ical4j.model.property.Uid;

public class EventDtoIcs implements EventDto {
	private VEvent event;

	public EventDtoIcs(VEvent event) {
		this.event = event;
	}

	@Override
	public String getId() {
		Uid value = event.getUid();
		
		if (value != null) {
			return value.getValue();
		}
		return null;
	}

	@Override
	public long getDtstart() {
		return getDate(event.getStartDate());
	}

	@Override
	public long getDtend() {
		return getDate(event.getEndDate());
	}

	@Override
	public String getTitle() {
		Summary value = event.getSummary();
		
		if (value != null) {
			return value.getValue();
		}
		return null;
	}

	@Override
	public String getDescription() {
		Description value = event.getDescription();
		
		if (value != null) {
			return value.getValue();
		}
		return null;
	}

	@Override
	public String getEventLocation() {
		Location value = event.getLocation();
		
		if (value != null) {
			return value.getValue();
		}
		return null;
	}

	@Override
	public String getDuration() {
		Duration value = event.getDuration();
		
		if (value != null) {
			return value.getValue();
		}
		return null;
	}

	@Override
	public String getEventTimezone() {
//		VTimeZone value = () event.getProperty(Property.TZID);
//		
//		if (value != null) {
//			return value.getName();
//		}
		return null;
	}

	@Override
	public String getRrule() {
		RRule value = (RRule) event.getProperty(Property.RRULE);
		
		if (value != null) {
			return value.getValue();
		}
		return null;
	}

	@Override
	public String getOrganizer() {
		Organizer value = event.getOrganizer();
		
		if (value != null) {
			return value.getValue();
		}
		return null;
	}

	@Override
	public String getCalendarId() {
		Uid value = event.getUid();
		
		if (value != null) {
			return value.getValue();
		}
		return null;
	}
	
	private long getDate(DateProperty date) {
		return (date != null) ? date.getDate().getTime() : 0;
	}
}
