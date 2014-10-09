package de.eru.mp3manager.gui.applicationwindow.main;

/**
 *
 * @author Philipp Bruckner
 */
public enum MainColumn {

    FILENAME("Dateiname", "fileName", 75.0, true),
    TITLE("Titel", "title", 75.0, true),
    ALBUM("Album", "album", 75.0, true),
    ARTIST("Interpret", "artist", 75.0, true),
    GENRE("Genre", "genre", 75.0, true),
    YEAR("Jahr", "year", 75.0, true),
    LAST_MODIFIED("Zuletzt bearbeitet", "lastModified", 75.0, true),
    SIZE("Dateigröße", "size", 75.0, true),
    TRACK("Titelnummer", "track", 75.0, true),
    DURATION("Dauer", "duration", 75.0, true);

    private final String columnName;
    private final double defaultWidth;
    private final boolean defaultVisible;
    private final String propertyName;

    private MainColumn(final String columnName, final String propertyName, final double defaultWidth, final boolean defaultVisible) {
        this.columnName = columnName;
        this.propertyName = propertyName;
        this.defaultWidth = defaultWidth;
        this.defaultVisible = defaultVisible;
    }

    public String getColumnName() {
        return columnName;
    }

    public double getDefaultWidth() {
        return defaultWidth;
    }
    public boolean isDefaultVisible() {
        return defaultVisible;
    }

    public String getPropertyName() {
        return propertyName;
    }
}
