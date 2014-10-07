package de.eru.mp3manager.gui.applicationwindow.main;

/**
 *
 * @author Philipp Bruckner
 */
public enum MainColumn {

    FILENAME("Dateiname", "fileName", 75.0),
    TITLE("Titel", "title", 75.0),
    ALBUM("Album", "album", 75.0),
    ARTIST("Interpret", "artist", 75.0),
    GENRE("Genre", "genre", 75.0),
    YEAR("Jahr", "year", 75.0),
    LAST_MODIFIED("Zuletzt bearbeitet", "lastModified", 75.0),
    SIZE("Dateigröße", "size", 75.0),
    TRACK("Titelnummer", "track", 75.0),
    DURATION("Dauer", "duration", 75.0);

    private final String name;
    private final double defaultWidth;
    private final String propertyName;

    private MainColumn(final String name, final String propertyName, final double defaultWidth) {
        this.name = name;
        this.propertyName = propertyName;
        this.defaultWidth = defaultWidth;
    }

    public String columnName() {
        return name;
    }

    public double defaultWidth() {
        return defaultWidth;
    }

    public String propertyName() {
        return propertyName;
    }
}
