package de.pheru.media.desktop.data;

import de.pheru.media.desktop.DesktopApplication;

import java.util.ArrayList;
import java.util.List;

public class AudioLibrary {

    public static final String DIRECTORY = DesktopApplication.APPLICATION_DATA_HOME + "/audiolibraries";
    public static final String FILE_ENDING = "al";
    public static final String NEW_NAME = "Neue Musikbibliothek";
    public static final String DEFAULT_NAME = "Meine Musik";
    public static final String DEFAULT_FILENAME = toFileName(DEFAULT_NAME, FILE_ENDING);

    private String name = DEFAULT_NAME;
    private List<String> directories = new ArrayList<>();
    private long lastUpdated = 0;

    public String getFileName() {
        return toFileName(name, FILE_ENDING);
    }

    public String getDataCacheFileName() {
        return toFileName(name, AudioLibraryData.FILE_ENDING);
    }

    //TODO auf Ungültige Zeichen für Dateinamen prüfen
    private static String toFileName(final String name, final String fileEnding) {
        return name.toLowerCase().replaceAll(" ", "_") + "." + fileEnding;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public List<String> getDirectories() {
        return directories;
    }

    public void setDirectories(final List<String> directories) {
        this.directories = directories;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(final long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
