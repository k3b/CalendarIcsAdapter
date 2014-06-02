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
package de.k3b.util;

import org.junit.*;

import java.text.ParseException;
import java.util.Date;

/**
 * Created by k3b on 20.05.2014.
 */
public class DateTimeUtilTests {
    private final Date xmasNoonDate = DateTimeUtil.createDate(2000, 12, 24, 12,0,0);
    private final String xmasNoonString = "20001224T120000Z";

    @Test
    public void shouldFormat() {
        String result = DateTimeUtil.toIsoDate(xmasNoonDate);
        Assert.assertEquals(xmasNoonString, result);
    }


    @Test
    public void shouldParse() throws ParseException {
        Date result = DateTimeUtil.parseIsoDate(xmasNoonString);
        Assert.assertEquals(xmasNoonString, DateTimeUtil.toIsoDate(result));
    }
}
