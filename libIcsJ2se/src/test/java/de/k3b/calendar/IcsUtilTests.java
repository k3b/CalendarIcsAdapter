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

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.TimeZoneRegistryFactory;
import net.fortuna.ical4j.model.component.VTimeZone;

import org.junit.Assert;
import org.junit.Test;

import de.k3b.calendar.ics.IcsUtil;

/**
 * Created by k3b on 30.05.2014.
 */
public class IcsUtilTests {
    private final TimeZoneRegistry tzregistry = TimeZoneRegistryFactory.getInstance().createRegistry();

    @Test
    public void shouldKnow_Europe_Amsterdam() {
        TimeZone tz = tzregistry.getTimeZone("Europe/Amsterdam");

        Assert.assertNotNull(tz);
    }

    @Test
    public void shouldKnow_GMT() {
        TimeZone tz = tzregistry.getTimeZone("GMT");
        Assert.assertNotNull(tz);
    }

    @Test
    public void shouldKnow_UTC() {
        TimeZone tz = tzregistry.getTimeZone("UTC");
        Assert.assertNotNull(tz);
    }

    @Test
    public void shouldHandleCalendarId() {
        Calendar vcalendar = new Calendar();
        IcsUtil.setCalId(vcalendar, "33");
        Assert.assertEquals("id", "33", IcsUtil.getCalId(vcalendar));
    }


    @Test
    public void shouldNotIncludeDuplicateCalendarId() {
        Calendar vcalendar = new Calendar();
        IcsUtil.setCalId(vcalendar, "32");
        IcsUtil.setCalId(vcalendar, "33");
        Assert.assertEquals("#properties", 1, vcalendar.getProperties(Property.UID).size());
    }

    @Test
    public void shouldFindTimeZone() {
        Calendar vcalendar = new Calendar();
        IcsUtil.includeTimeZone(vcalendar, tzregistry.getTimeZone("UTC").getVTimeZone(), tzregistry.getTimeZone("Europe/Amsterdam").getVTimeZone());

        VTimeZone found = IcsUtil.getTimeZone(vcalendar, "Europe/Amsterdam");
        Assert.assertEquals("Europe/Amsterdam", IcsUtil.getId(found));
    }

    @Test
    public void shouldNotIncludeDuplicateTimeZone() {
        Calendar vcalendar = new Calendar();
        int numberInserted = IcsUtil.includeTimeZone(vcalendar,
                tzregistry.getTimeZone("UTC").getVTimeZone(),
                tzregistry.getTimeZone("UTC").getVTimeZone());

        Assert.assertEquals(1, numberInserted);
    }

}
