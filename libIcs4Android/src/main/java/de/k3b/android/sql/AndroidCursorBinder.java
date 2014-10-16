package de.k3b.android.sql;

import android.database.Cursor;

import org.apache.commons.lang.NullArgumentException;

import de.k3b.sql.ColumnBinder;

/**
 * Created by k3b on 04.06.2014.
 */
public class AndroidCursorBinder implements ColumnBinder {
    private final Cursor databaseCursor;

    public AndroidCursorBinder(final Cursor databaseCursor) {
        if (databaseCursor == null) throw new NullArgumentException(Cursor.class.getName());
        this.databaseCursor = databaseCursor;
    }

    @Override
    public void clear() {
    }

    @Override
    public String getString(int columnId) {
        return databaseCursor.getString(columnId);
    }

    @Override
    public void set(int columnId, String value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getLong(int columnId) {
        return databaseCursor.getLong(columnId);
    }

    @Override
    public void set(int columnId, long value) {
        throw new UnsupportedOperationException();
    }
}
