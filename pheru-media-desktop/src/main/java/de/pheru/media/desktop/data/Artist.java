package de.pheru.media.desktop.data;

import java.util.Map;

public class Artist {

    private Map<String, Album> albums;

    public Album getAlbum(final String albumName){
        return albums.get(albumName);
    }

    public Map<String, Album> getAlbums() {
        return albums;
    }

    public void setAlbums(final Map<String, Album> albums) {
        this.albums = albums;
    }
}
