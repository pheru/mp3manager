package de.pheru.media.desktop.data;

import de.pheru.media.desktop.DesktopApplication;

import java.util.Map;

public class AudioLibraryData {

    public static final String DIRECTORY = DesktopApplication.APPLICATION_DATA_HOME + "/audiolibrarydata";
    public static final String FILE_ENDING = "ald";

    private Map<String, Artist> artists;

    public Artist getArtist(final String artistName) {
        return artists.get(artistName);
    }

    public Map<String, Artist> getArtists() {
        return artists;
    }

    public void setArtists(final Map<String, Artist> artists) {
        this.artists = artists;
    }
}
