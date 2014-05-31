package de.k3b.calendar.ics;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.property.Uid;

import java.util.Iterator;

/**
 * Created by EVE on 31.05.2014.
 */
public class IcsUtil {
    public static void setCalId(final Calendar vcalendar, final String id) {
        Uid prop = getCalIdProperty(vcalendar);
        if (prop == null) {
            prop =  new Uid();
            vcalendar.getProperties().add(prop);
        }
        prop.setValue(id);
    }

    public static String getCalId(final Calendar vcalendar) {
        Uid prop = getCalIdProperty(vcalendar);
        return (prop != null) ? prop.getValue() : null;
    }

    private static Uid getCalIdProperty(final Calendar vcalendar) {
        return (vcalendar != null) ? (Uid) vcalendar.getProperty(Property.UID) : null;
    }

    public static int includeTimeZone(final Calendar vcalendar, final VTimeZone... zones) {
        ComponentList components = vcalendar.getComponents();
        int itemsInserted = 0;
        for(VTimeZone zone : zones) {
            if ((zone != null) && (null == getTimeZone(vcalendar,getId(zone)))) {
                components.add(zone);
                itemsInserted++;
            }
        }
        return itemsInserted;
    }

    public static VTimeZone getTimeZone(final Calendar vcalendar, final String id) {
        ComponentList zones = vcalendar.getComponents(Component.VTIMEZONE);
        for (final Iterator i = zones.iterator(); i.hasNext();) {
            final VTimeZone c = (VTimeZone) i.next();
            if (id.equals(getId(c))) {
                return c;
            }
        }
        return null;
    }

    public static String getId(VTimeZone zone) {
        Property prop = (zone != null) ? zone.getTimeZoneId() : null;
        return (prop != null) ? prop.getValue() : null;
    }
}
