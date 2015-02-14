package de.eru.mp3manager.gui.applicationwindow.main;

import de.eru.mp3manager.utils.Comparators;
import java.util.Comparator;

/**
 *
 * @author Philipp Bruckner
 */
public enum MainColumn {

    FILENAME("Dateiname", "fileName", null, 100.0, true),
    TITLE("Titel", "title", null, 100.0, true),
    ALBUM("Album", "album", null, 100.0, true),
    ARTIST("Interpret", "artist", null, 100.0, true),
    TRACK("Titelnummer", "track", Comparators.NUMBER_COMPARATOR, 100.0, true),
    DURATION("Dauer", "formattedDuration", Comparators.TIME_COMPARATOR, 100.0, true),
    GENRE("Genre", "genre", null, 100.0, true),
    YEAR("Jahr", "year", Comparators.NUMBER_COMPARATOR, 100.0, true),
    SIZE("Dateigröße", "size", Comparators.SIZE_COMPARATOR, 100.0, true),
    BITRATE("Bitrate", "bitrate", Comparators.BITRATE_COMPARATOR, 100.0, true),
    LAST_MODIFIED("Zuletzt bearbeitet", "lastModified", Comparators.DATE_COMPARATOR, 100.0, true);

    private final String columnName;
    private final String propertyName;
    private final Comparator<String> comparator;
    private final double defaultWidth;
    private final boolean defaultVisible;

    private MainColumn(final String columnName, final String propertyName, final Comparator<String> comparator, final double defaultWidth, final boolean defaultVisible) {
        this.columnName = columnName;
        this.propertyName = propertyName;
        this.comparator = comparator;
        this.defaultWidth = defaultWidth;
        this.defaultVisible = defaultVisible;
    }

    public String getColumnName() {
        return columnName;
    }

    public String getPropertyName() {
        return propertyName;
    }
    
    public Comparator<String> getComparator(){
        return comparator;
    }

    public double getDefaultWidth() {
        return defaultWidth;
    }

    public boolean isDefaultVisible() {
        return defaultVisible;
    }
}
