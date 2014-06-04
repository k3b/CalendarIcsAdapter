package de.k3b.sql;

/**
 * A fake ColumnBinder that is used for testing
 * Created by k3b on 04.06.2014.
 */
public class MockColumnBinder implements ColumnBinder {
    private final int columnCount;

    private String[] colums;
    public MockColumnBinder(int columnCount) {
        this.columnCount = columnCount;
        clear();
    }
    @Override
    public void clear() {
        colums = new String[columnCount];
    }

    @Override
    public String getString(final int columnId) {
        return colums[columnId];
    }

    @Override
    public void set(final int columnId, final String value) {
        colums[columnId] = value;
    }

    @Override
    public long getLong(final int columnId) {
        return Long.parseLong(getString(columnId));
    }

    @Override
    public void set(final int columnId, final long value) {
        set(columnId, Long.toString(value));
    }
}
