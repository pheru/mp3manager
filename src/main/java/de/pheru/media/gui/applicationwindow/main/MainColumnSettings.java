package de.pheru.media.gui.applicationwindow.main;

import de.pheru.fx.util.properties.ObservablePropertyKey;

import java.lang.reflect.Field;

public final class MainColumnSettings { // TODO enum?

    public static final MainColumnSettings FILENAME = new MainColumnSettings("Filename", true, 0, 100);
    public static final MainColumnSettings TITLE = new MainColumnSettings("Title", true, 1, 100);
    public static final MainColumnSettings ALBUM = new MainColumnSettings("Album", true, 2, 100);
    public static final MainColumnSettings ARTIST = new MainColumnSettings("Artist", true, 3, 100);
    public static final MainColumnSettings TRACK = new MainColumnSettings("Track", true, 4, 100);
    public static final MainColumnSettings DURATION = new MainColumnSettings("Duration", true, 5, 100);
    public static final MainColumnSettings GENRE = new MainColumnSettings("Genre", true, 6, 100);
    public static final MainColumnSettings YEAR = new MainColumnSettings("Year", true, 7, 100);
    public static final MainColumnSettings SIZE = new MainColumnSettings("Size", true, 8, 100);
    public static final MainColumnSettings BITRATE = new MainColumnSettings("Bitrate", true, 9, 100);
    public static final MainColumnSettings LASTMODIFIED = new MainColumnSettings("Lastmodified", true, 10, 100);

    private final ObservablePropertyKey<Boolean> visible;
    private final ObservablePropertyKey<Integer> index; // todo bei enum die ordinalnumber verweden
    private final ObservablePropertyKey<Double> width;

    private MainColumnSettings(final String columnName, final boolean visible, final int index, final double width) {
        this.visible = new ObservablePropertyKey<>("maincolumn" + columnName + "Visible", visible);
        this.index = new ObservablePropertyKey<>("maincolumn" + columnName + "Index", index);
        this.width = new ObservablePropertyKey<>("maincolumn" + columnName + "Width", width);
    }

    public static MainColumnSettings getByName(final String columnName) {
        for (Field field : MainColumnSettings.class.getFields()) {
            if (field.getName().equals(columnName.toUpperCase())) {
                try {
                    return (MainColumnSettings) field.get(null);
                } catch (final IllegalAccessException e) {
                    throw new IllegalArgumentException("Could not access settings for columName " + columnName, e);
                }
            }
        }
        throw new IllegalArgumentException("No settings found for columnName " + columnName);
    }

    public ObservablePropertyKey<Boolean> getVisible() {
        return visible;
    }

    public ObservablePropertyKey<Integer> getIndex() {
        return index;
    }

    public ObservablePropertyKey<Double> getWidth() {
        return width;
    }

}
