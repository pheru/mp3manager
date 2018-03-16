package de.pheru.media.desktop.data;

import de.pheru.media.core.data.model.AudioFile;
import de.pheru.media.desktop.DesktopApplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AudioLibraryData {

    public static final String DIRECTORY = DesktopApplication.APPLICATION_DATA_HOME + "/audiolibrarydata";
    public static final String FILE_ENDING = "ald";

    private Map<String, Artist> artists = new HashMap<>();

    public Artist getArtist(final String artistName) {
        return artists.get(artistName);
    }

    public List<Album> getAllAlbums() {
        final List<Album> albums = new ArrayList<>();
        for (final Artist artist : artists.values()) {
            albums.addAll(artist.getAlbums().values());
        }
        return albums;
    }

    public List<AudioFile> getAllAudioFiles() {
        final List<AudioFile> audioFiles = new ArrayList<>();
        for (final Artist artist : artists.values()) {
            for (final Album album : artist.getAlbums().values()) {
                audioFiles.addAll(album.getAudioFiles());
            }
        }
        return audioFiles;
    }

    public Map<String, List<AudioFile>> getAllAudioFilesByGenre() {
        final Map<String, List<AudioFile>> audioFilesByGenre = new HashMap<>();
        for (final Artist artist : artists.values()) {
            for (final Album album : artist.getAlbums().values()) {
                for (final AudioFile audioFile : album.getAudioFiles()) {
                    if (!audioFilesByGenre.containsKey(audioFile.getGenre())) {
                        audioFilesByGenre.put(audioFile.getGenre(), new ArrayList<>());
                    }
                    audioFilesByGenre.get(audioFile.getGenre()).addAll(album.getAudioFiles());
                }
            }
        }
        return audioFilesByGenre;
    }

    public Map<String, Artist> getArtists() {
        return artists;
    }

    public void setArtists(final Map<String, Artist> artists) {
        this.artists = artists;
    }
}
