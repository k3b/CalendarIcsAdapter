package de.k3b.android.sql;

import android.content.ContentValues;

import de.k3b.sql.Binder;

/**
 * Created by EVE on 04.06.2014.
 */
public class AndroidContentValuesBinder implements Binder{
    private final ContentValues values;
    private final String[] columNames;

    public AndroidContentValuesBinder(ContentValues values, String... columNames) {
        this.values = values;
        this.columNames = columNames;
    }

    @Override
    public void clear() {
        values.clear();
    }

    @Override
    public String getString(final int columnId) {
        return values.getAsString(columNames[columnId]);
    }

    @Override
    public void set(final int columnId, final String value) {
        values.put(columNames[columnId], value);
    }

    @Override
    public long getLong(final int columnId) {
        return values.getAsLong(columNames[columnId]);
    }

    @Override
    public void set(final int columnId, final long value) {
        values.put(columNames[columnId], value);
    }
}
