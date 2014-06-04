package de.k3b.sql;

import org.apache.commons.lang.NullArgumentException;

import java.util.ArrayList;
import java.util.List;

import de.k3b.calendar.EventDto;
import de.k3b.calendar.EventFilter;

/**
 * Database abstraction layer for persisting an event as a database-row via a columnBinder interface.
 *
 * Created by k3b on 04.06.2014.
 */
public class EventRowBinder implements EventDto {
    private static final int col_ID = 0;
    private static final int col_DtStart = 1;
    private static final int col_DtEnd = 2;
    private static final int col_Title = 3;
    private static final int col_Description = 4;
    private static final int col_EventLocation = 5;
    private static final int col_EventTimezone = 6;
    private static final int col_Duration = 7;
    private static final int col_RRule = 8;
    private static final int col_RDate = 9;
    private static final int col_Organizer = 10;
    private static final int col_CalendarId = 11;
    private static final int col_Alarm = 12;
    private static final int col_ExtDates = 13;
    public static final int COLUMN_COUNT = 14;

    private final ColumnBinder columnBinder;

    public EventRowBinder(final ColumnBinder columnBinder) {
        if (columnBinder == null) throw new NullArgumentException(ColumnBinder.class.getName());
        this.columnBinder = columnBinder;
    }

    @Override
    public String getId() {return this.columnBinder.getString(col_ID);}

    /* (non-Javadoc)
         * @see de.k3b.calendar.adapter.EventData#getDtStart()
         */
    @Override
    public long getDtStart() {return this.columnBinder.getLong(col_DtStart);}
    /* (non-Javadoc)
     * @see de.k3b.calendar.adapter.EventData#getDtEnd()
     */
    @Override
    public long getDtEnd() {return this.columnBinder.getLong(col_DtEnd);}
    /* (non-Javadoc)
     * @see de.k3b.calendar.adapter.EventData#getTitle()
     */
    @Override
    public String getTitle() {return this.columnBinder.getString(col_Title);}
    /* (non-Javadoc)
     * @see de.k3b.calendar.adapter.EventData#getDescription()
     */
    @Override
    public String getDescription() {return this.columnBinder.getString(col_Description);}
    /* (non-Javadoc)
     * @see de.k3b.calendar.adapter.EventData#getEventLocation()
     */
    @Override
    public String getEventLocation() {return this.columnBinder.getString(col_EventLocation);}
    /* (non-Javadoc)
     * @see de.k3b.calendar.adapter.EventData#getEventTimezone()
     */
    @Override
    public String getEventTimezone() {return this.columnBinder.getString(col_EventTimezone);}
    /* (non-Javadoc)
     * @see de.k3b.calendar.adapter.EventData#getDuration()
     */
    @Override
    public String getDuration() {return this.columnBinder.getString(col_Duration);}
    /* (non-Javadoc)
     * @see de.k3b.calendar.adapter.EventData#getRRule()
     */
    @Override
    public String getRRule() {return this.columnBinder.getString(col_RRule);}

    @Override
    public String getRDate() {return this.columnBinder.getString(col_RDate);}

    @Override
    public String getOrganizer() {return this.columnBinder.getString(col_Organizer);}

    @Override
    public String getCalendarId() {return this.columnBinder.getString(col_CalendarId);}

    /** #9 the alarm(s) should trigger x menutes before the event.
     * null means no alarms. This Method does not load the alarms */
    @Override
    public List<Integer> getAlarmMinutesBeforeEvent() {return (this.columnBinder.getLong(col_Alarm) != 0) ? new ArrayList<Integer>():null;}

    /** #11 formatted as komma seperated list of iso-utc-dates. Example: '20090103T093000Z,20110101T093000Z' */
    @Override
    public String getExtDates() {
        return this.columnBinder.getString(col_ExtDates);
    }

    public void bind(final EventDto src, final EventFilter filter) {
//      set(col_ID,  src.getId(), filter.getId());
        set(col_CalendarId,  src.getCalendarId(), filter.getCalendarId());
        set(col_DtStart,  src.getDtStart(), true);
        set(col_DtEnd,  src.getDtEnd(), true);
        set(col_Title,  src.getTitle(), true);
        set(col_Description,  src.getDescription(), true);
        set(col_EventLocation,  src.getEventLocation(), filter.getEventLocation());
        set(col_Organizer,  src.getOrganizer(), filter.getOrganizer());

        set(col_Duration,  src.getDuration(), true);
        set(col_EventTimezone,  src.getEventTimezone(), filter.getEventTimezone());

        boolean copyRecurrence = filter.getRecurrenceType() != EventFilter.RecurrenceType.ThisEvent;
        set(col_RRule,  src.getRRule(), copyRecurrence);
        set(col_RDate,  src.getRDate(), copyRecurrence);
        set(col_ExtDates,  src.getExtDates(), copyRecurrence);
        set(col_Alarm, (src.getAlarmMinutesBeforeEvent() != null) ? 1 : 0, filter.getAlarms());
    }

    private void set(final int colId, final long value, final boolean enabled) {
        if (enabled) {
            columnBinder.set(colId, value);
        }
    }

    private void set(final int colId, final String value, final boolean enabled) {
        if (enabled) {
            columnBinder.set(colId, value);
        }
    }
}
