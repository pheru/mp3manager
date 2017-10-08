package de.pheru.media.gui.applicationwindow.main;

import de.pheru.fx.util.properties.ObservablePropertyKey;

public enum MainColumnSettings {

    FILENAME("Filename", true, 100),
    TITLE("Title", true, 100),
    ALBUM("Album", true, 100),
    ARTIST("Artist", true, 100),
    TRACK("Track", true, 100),
    DURATION("Duration", true, 100),
    GENRE("Genre", true, 100),
    YEAR("Year", true, 100),
    SIZE("Size", true, 100),
    BITRATE("Bitrate", true, 100),
    LASTMODIFIED("Lastmodified", true, 100);

    private final String columnName;
    private final ObservablePropertyKey<Boolean> visiblePropertyKey;
    private final ObservablePropertyKey<Integer> indexPropertyKey;
    private final ObservablePropertyKey<Double> widthPropertyKey;

    MainColumnSettings(final String columnName, final boolean defaultVisible, final double defaultWidth) {
        this.columnName = columnName;
        this.visiblePropertyKey = new ObservablePropertyKey<>("maincolumn" + columnName + "Visible", defaultVisible);
        this.indexPropertyKey = new ObservablePropertyKey<>("maincolumn" + columnName + "Index", ordinal());
        this.widthPropertyKey = new ObservablePropertyKey<>("maincolumn" + columnName + "Width", defaultWidth);
    }

    public static MainColumnSettings getByColumnName(final String columnName) {
        for (final MainColumnSettings mainColumnSettings : MainColumnSettings.values()) {
            if (mainColumnSettings.columnName.toLowerCase().equals(columnName.toLowerCase())) {
                return mainColumnSettings;
            }
        }
        throw new IllegalArgumentException("No settings found for columnName " + columnName);
    }

    public ObservablePropertyKey<Boolean> getVisiblePropertyKey() {
        return visiblePropertyKey;
    }

    public ObservablePropertyKey<Integer> getIndexPropertyKey() {
        return indexPropertyKey;
    }

    public ObservablePropertyKey<Double> getWidthPropertyKey() {
        return widthPropertyKey;
    }

}
