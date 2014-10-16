/*
 * Copyright (C) 2014- k3b
 *
 * This file is part of android.calendar.ics.adapter.
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
package de.k3b.calendar;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.Date;

import de.k3b.calendar.ics.EventDto2IcsFactory;
import de.k3b.calendar.ics.IcsAsEventDto;
import de.k3b.sql.EventRowBinder;
import de.k3b.sql.MockColumnBinder;
import de.k3b.util.DateTimeUtil;

/**
 * Tests the conversion from EventDto to iCal4j-ics-event and back to EventDto.
 * Created by k3b on 29.05.2014.
 */
public class DtoRowDtoTests {
    /** controls, wich data elements will be exported. */
    private final EventFilter filter = new EventFilterDto(EventFilterDto.ALL)
            .setAlarms(false).setId(false);

    @Test
    public void shouldConvertCompleteEventRow() throws IOException, ParserException {
		EventDtoSimple src = TestDataUtils.createTestEventDto();

        EventRowBinder rowBinder = new EventRowBinder(new MockColumnBinder(EventRowBinder.COLUMN_COUNT));
        rowBinder.bind(src, filter);
        EventDtoSimple result = new EventDtoSimple(rowBinder,filter);
		
        Assert.assertEquals(TestDataUtils.getIcs(filter, src), TestDataUtils.getIcs(filter, result));
    }
}
