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
