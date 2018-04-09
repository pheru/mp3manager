package de.pheru.media.core.data.loader;

import de.pheru.media.core.data.model.Artwork;
import de.pheru.media.core.data.model.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.id3.AbstractID3v2Tag;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class Mp3FileLoader implements AudioFileLoader {

    @Override
    public List<String> getSupportedExtensions() {
        return Collections.singletonList(".mp3");
    }

    @Override
    public AudioFile load(final File file, final ArtworkCreator artworkCreator) throws AudioFileLoaderException {
        final AudioFile audioFile = new AudioFile();

        audioFile.setFileName(file.getName());
        audioFile.setFilePath(file.getParent());
        audioFile.setSize(file.length());

        loadMp3Data(audioFile, file, artworkCreator);

        return audioFile;
    }

    private void loadMp3Data(final AudioFile audioFile, final File file, final ArtworkCreator artworkCreator) throws AudioFileLoaderException {
        final MP3File mp3File = readMp3File(file);

        final AudioHeader audioHeader = mp3File.getAudioHeader();
        audioFile.setDuration(audioHeader.getTrackLength());
        audioFile.setBitrate((short) intFromStringValue(audioHeader.getBitRate()));

        final AbstractID3v2Tag tag = readID3v2Tag(mp3File);
        audioFile.setTitle(tag.getFirst(FieldKey.TITLE));
        audioFile.setAlbum(tag.getFirst(FieldKey.ALBUM));
        audioFile.setArtist(tag.getFirst(FieldKey.ARTIST));
        audioFile.setGenre(tag.getFirst(FieldKey.GENRE));
        audioFile.setTrack((short) intFromStringValue(tag.getFirst(FieldKey.TRACK)));
        audioFile.setYear((short) intFromStringValue(tag.getFirst(FieldKey.YEAR)));
        if (tag.getFirstArtwork() != null) {
            final org.jaudiotagger.tag.images.Artwork jaudiotaggerArtwork = tag.getFirstArtwork();
            try {
                final Artwork artwork = artworkCreator.createArtwork(jaudiotaggerArtwork.getBinaryData());
                audioFile.setArtwork(artwork);
            } catch (final IOException e) {
                throw new AudioFileLoaderException("Could not load artwork from file \"" + file.getAbsolutePath() + "\"!");
            }
        }
    }

    private MP3File readMp3File(final File file) throws AudioFileLoaderException {
        try {
            return (MP3File) AudioFileIO.read(file);
        } catch (final CannotReadException | IOException | TagException | ReadOnlyFileException | InvalidAudioFrameException e) {
            throw new AudioFileLoaderException("Failed to read mp3-data from file \"" + file.getAbsolutePath() + "\"!", e);
        }
    }

    private AbstractID3v2Tag readID3v2Tag(final MP3File mp3File) throws AudioFileLoaderException {
        if (!mp3File.hasID3v2Tag()) {
            throw new AudioFileLoaderException("File \"" + mp3File.getFile().getAbsolutePath() + "\" does not have an ID3v2Tag!");
        }
        return mp3File.getID3v2Tag();
    }

    private int intFromStringValue(final String value) {
        if (value == null || value.isEmpty()) {
            return -1;
        } else {
            // Alle "no-digits" entfernen
            final String replace = value.replaceAll("\\D", "");
            if (replace.isEmpty()) {
                return -1;
            } else {
                return Integer.valueOf(replace);
            }
        }
    }

}
