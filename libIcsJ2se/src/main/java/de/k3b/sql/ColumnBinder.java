package de.k3b.sql;

/**
 * Facade to hide sql-column specific operations.
 *
 * Created by k3b on 04.06.2014.
 */
public interface ColumnBinder {
    void clear();

    String getString(int columnId);

    void set(int columnId, String value);

    long getLong(int columnId);

    void set(int columnId, long value);
}
