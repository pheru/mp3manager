package de.pheru.media.desktop.data;

import java.util.Map;

public class AudioLibraryData {

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
