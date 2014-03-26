package de.k3b.data.calendar;

public class EventDtoSimple implements EventDTO {
	public long getId() {
		return Id;
	}

	public EventDtoSimple setId(long id) {
		Id = id;
		return this;
	}

	public long getDtstart() {
		return Dtstart;
	}

	public EventDtoSimple setDtstart(long dtstart) {
		Dtstart = dtstart;
		return this;
	}

	public long getDtend() {
		return Dtend;
	}

	public EventDtoSimple setDtend(long dtend) {
		Dtend = dtend;
		return this;
	}

	public String getTitle() {
		return Title;
	}

	public EventDtoSimple setTitle(String title) {
		Title = title;
		return this;
	}

	public String getDescription() {
		return Description;
	}

	public EventDtoSimple setDescription(String description) {
		Description = description;
		return this;
	}

	public String getEventLocation() {
		return EventLocation;
	}

	public EventDtoSimple setEventLocation(String eventLocation) {
		EventLocation = eventLocation;
		return this;
	}

	public String getEventTimezone() {
		return EventTimezone;
	}

	public EventDtoSimple setEventTimezone(String eventTimezone) {
		EventTimezone = eventTimezone;
		return this;
	}

	public String getDuration() {
		return Duration;
	}

	public EventDtoSimple setDuration(String duration) {
		Duration = duration;
		return this;
	}

	public String getRrule() {
		return Rrule;
	}

	public EventDtoSimple setRrule(String rrule) {
		Rrule = rrule;
		return this;
	}

	public String getOrganizer() {
		return Organizer;
	}

	public EventDtoSimple setOrganizer(String organizer) {
		Organizer = organizer;
		return this;
	}

	public int getCalendarId() {
		return CalendarId;
	}

	public EventDtoSimple setCalendarId(int calendarId) {
		CalendarId = calendarId;
		return this;
	}

	long Id;
	
	long Dtstart;

	long  Dtend;

	String Title;

	String Description;

	String EventLocation;

	String EventTimezone;

	String Duration;

	String Rrule;

	String Organizer;

	int CalendarId;
}
