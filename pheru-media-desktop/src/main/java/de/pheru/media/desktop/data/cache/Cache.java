package de.pheru.media.desktop.data.cache;

import java.util.Map;

public class Cache {

    private Map<String, ArtistCache> artistCaches;

    public ArtistCache getArtistCache(final String artistName) {
        return artistCaches.get(artistName);
    }

    public Map<String, ArtistCache> getArtistCaches() {
        return artistCaches;
    }

    public void setArtistCaches(final Map<String, ArtistCache> artistCaches) {
        this.artistCaches = artistCaches;
    }
}
