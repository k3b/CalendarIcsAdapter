package de.k3b.sql;

/**
 * Created by EVE on 04.06.2014.
 */
public interface Binder {
    void clear();

    String getString(int columnId);

    void set(int columnId, String value);

    long getLong(int columnId);

    void set(int columnId, long value);
}
