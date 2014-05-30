package de.k3b.calendar;

import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.TimeZoneRegistryFactory;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by k3b on 30.05.2014.
 */
public class TimeZoneTests {
    private final TimeZoneRegistry tzregistry = TimeZoneRegistryFactory.getInstance().createRegistry();

    @Test
    public void shuldKnow_Europe_Amsterdam() {
        TimeZone tz = tzregistry.getTimeZone("Europe/Amsterdam");

        Assert.assertNotNull(tz);
    }

    @Test
    public void shuldKnow_GMT() {
        TimeZone tz = tzregistry.getTimeZone("GMT");
        Assert.assertNotNull(tz);
    }

    @Test
    public void shuldKnow_UTC() {
        TimeZone tz = tzregistry.getTimeZone("UTC");
        Assert.assertNotNull(tz);
    }
}
