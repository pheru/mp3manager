package de.pheru.media.desktop.data;

import de.pheru.media.core.data.model.Artwork;
import de.pheru.media.core.data.model.AudioFile;

import java.util.List;

public class Album {

    private List<AudioFile> audioFiles;
    private List<Artwork> artworks;

    public List<AudioFile> getAudioFiles() {
        return audioFiles;
    }

    public void setAudioFiles(final List<AudioFile> audioFiles) {
        this.audioFiles = audioFiles;
    }

    public List<Artwork> getArtworks() {
        return artworks;
    }

    public void setArtworks(final List<Artwork> artworks) {
        this.artworks = artworks;
    }
}
