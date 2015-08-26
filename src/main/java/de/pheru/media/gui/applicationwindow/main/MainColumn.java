package de.pheru.media.gui.applicationwindow.main;

import de.pheru.media.util.Comparators;
import java.util.Comparator;

/**
 *
 * @author Philipp Bruckner
 */
public enum MainColumn {

    FILENAME("Dateiname", "fileName", null, 100.0, true, false),
    TITLE("Titel", "title", null, 100.0, true, false),
    ALBUM("Album", "album", null, 100.0, true, false),
    ARTIST("Interpret", "artist", null, 100.0, true, false),
    TRACK("Titelnummer", "track", Comparators.NUMBER_COMPARATOR, 100.0, true, true),
    DURATION("Dauer", "formattedDuration", Comparators.TIME_COMPARATOR, 100.0, true, true),
    GENRE("Genre", "genre", null, 100.0, true, false),
    YEAR("Jahr", "year", Comparators.NUMBER_COMPARATOR, 100.0, true, true),
    SIZE("Dateigröße", "size", Comparators.SIZE_COMPARATOR, 100.0, true, true),
    BITRATE("Bitrate", "bitrate", Comparators.BITRATE_COMPARATOR, 100.0, true, true),
    LAST_MODIFIED("Zuletzt bearbeitet", "lastModified", Comparators.DATE_COMPARATOR, 100.0, true, true);

    private final String columnName;
    private final String propertyName;
    private final Comparator<String> comparator;
    private final double defaultWidth;
    private final boolean defaultVisible;
    private final boolean alignRight;

    private MainColumn(final String columnName, final String propertyName, final Comparator<String> comparator,
            final double defaultWidth, final boolean defaultVisible, final boolean alignRight) {
        this.columnName = columnName;
        this.propertyName = propertyName;
        this.comparator = comparator;
        this.defaultWidth = defaultWidth;
        this.defaultVisible = defaultVisible;
        this.alignRight = alignRight;
    }

    public static MainColumn getMainColumnByColumnName(String columnName) {
        for (MainColumn mc : MainColumn.values()) {
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

    public Comparator<String> getComparator() {
        return comparator;
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
}
