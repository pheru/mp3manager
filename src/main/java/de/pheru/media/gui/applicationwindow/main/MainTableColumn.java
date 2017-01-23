package de.pheru.media.gui.applicationwindow.main;

@Deprecated //TODO entfernen
public enum MainTableColumn {

    FILENAME("Dateiname", "fileName", 100.0, true, false, String.class),
    TITLE("Titel", "title", 100.0, true, false, String.class),
    ALBUM("Album", "album", 100.0, true, false, String.class),
    ARTIST("Interpret", "artist", 100.0, true, false, String.class),
    TRACK("Titelnummer", "track", 100.0, true, true, Integer.class),
    DURATION("Dauer", "formattedDuration", 100.0, true, true, Integer.class),
    GENRE("Genre", "genre", 100.0, true, false, String.class),
    YEAR("Jahr", "year", 100.0, true, true, Integer.class),
    SIZE("Dateigröße", "size", 100.0, true, true, Long.class),
    BITRATE("Bitrate", "bitrate", 100.0, true, true, Integer.class),
    LAST_MODIFIED("Zuletzt bearbeitet", "lastModified", 100.0, true, true, Long.class);

    private final String columnName;
    private final String propertyName;
    private final double defaultWidth;
    private final boolean defaultVisible;
    private final boolean alignRight;
    private final Class<?> clazz;

    MainTableColumn(final String columnName, final String propertyName,
            final double defaultWidth, final boolean defaultVisible, final boolean alignRight, Class<?> clazz) {
        this.columnName = columnName;
        this.propertyName = propertyName;
        this.defaultWidth = defaultWidth;
        this.defaultVisible = defaultVisible;
        this.alignRight = alignRight;
        this.clazz = clazz;
    }

    public static MainTableColumn getMainColumnByColumnName(String columnName) {
        for (MainTableColumn mc : MainTableColumn.values()) {
            if (mc.getColumnName().equals(columnName)) {
                return mc;
            }
        }
        return null;
    }

    public String getColumnName() {
        return columnName;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public double getDefaultWidth() {
        return defaultWidth;
    }

    public boolean isDefaultVisible() {
        return defaultVisible;
    }

    public boolean isAlignRight() {
        return alignRight;
    }

    public Class<?> getClazz() {
        return clazz;
    }
}
