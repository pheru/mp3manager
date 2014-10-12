package de.eru.mp3manager.data.utils;

import de.eru.mp3manager.data.Mp3FileData;
import de.eru.mp3manager.utils.formatter.ByteFormatter;
import de.eru.mp3manager.utils.formatter.TimeFormatter;
import java.io.File;
import java.io.IOException;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.id3.AbstractID3v2Tag;

/**
 * Utility-Klasse zum Mappen von Files in Mp3FileData und Playlists.
 *
 * @author Philipp Bruckner
 */
public final class Mapper {

    private Mapper() {
        //Utility-Klasse
    }

    /**
     * Mappt ein File in ein Mp3FileData-Objekt.
     *
     * @param file Die Datei, welche in in ein Mp3FileData gemappt werden soll
     * @return Ein Mp3FileData.
     * @throws IOException TODO JavaDoc für Exception
     */
    public static Mp3FileData fileToMp3FileData(File file) throws IOException {
        Mp3FileData mp3FileData = new Mp3FileData(file);
        try {
            fileToMp3FileData(file, mp3FileData);
        } catch (CannotReadException | TagException | ReadOnlyFileException | InvalidAudioFrameException ex) {
            ex.printStackTrace();
        }
        return mp3FileData;
    }

    /**
     * Mappt ein File in ein bereits bestehendes Mp3FileData-Objekt.
     *
     * @param file Die Datei, welche in ein Mp3FileData gemappt werden soll.
     * @param mp3FileData Das bestehende Mp3FileData-Objekt.
     * @throws org.jaudiotagger.audio.exceptions.CannotReadException
     * @throws IOException TODO JavaDoc für Exception
     * @throws org.jaudiotagger.tag.TagException
     * @throws org.jaudiotagger.audio.exceptions.ReadOnlyFileException
     * @throws org.jaudiotagger.audio.exceptions.InvalidAudioFrameException
     */
    public static void fileToMp3FileData(File file, Mp3FileData mp3FileData) throws CannotReadException, IOException, TagException, ReadOnlyFileException, InvalidAudioFrameException {
        MP3File mp3File = (MP3File) AudioFileIO.read(file);
        AudioHeader audioHeader = mp3File.getAudioHeader();

        mp3FileData.setDuration(audioHeader.getTrackLength());
        if (mp3File.hasID3v2Tag()) {
            AbstractID3v2Tag tag = mp3File.getID3v2Tag();
            mp3FileData.setTitle(tag.getFirst(FieldKey.TITLE));
            mp3FileData.setArtist(tag.getFirst(FieldKey.ARTIST));
            mp3FileData.setAlbum(tag.getFirst(FieldKey.ALBUM));
            mp3FileData.setGenre(tag.getFirst(FieldKey.GENRE));
            mp3FileData.setYear(tag.getFirst(FieldKey.YEAR));
            mp3FileData.setTrack(tag.getFirst(FieldKey.TRACK));
            if (tag.getFirstArtwork() != null) {
                mp3FileData.setCover(tag.getFirstArtwork().getBinaryData());
            }
        }
    }
}
