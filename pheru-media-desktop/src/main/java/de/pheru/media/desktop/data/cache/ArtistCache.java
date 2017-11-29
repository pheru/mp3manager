package de.pheru.media.desktop.data.cache;

import java.util.Map;

public class ArtistCache {

    private Map<String, AlbumCache> albumCaches;

    public AlbumCache getAlbumCache(final String albumName){
        return albumCaches.get(albumName);
    }

    public Map<String, AlbumCache> getAlbumCaches() {
        return albumCaches;
    }

    public void setAlbumCaches(final Map<String, AlbumCache> albumCaches) {
        this.albumCaches = albumCaches;
    }
}
