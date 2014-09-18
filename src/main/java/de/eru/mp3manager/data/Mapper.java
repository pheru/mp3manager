package de.eru.mp3manager.data;

import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;
import de.eru.mp3manager.utils.ByteFormatter;
import de.eru.mp3manager.utils.TimeFormatter;
import java.io.File;
import java.io.IOException;

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
     * @throws UnsupportedTagException TODO JavaDoc für Exception
     * @throws InvalidDataException TODO JavaDoc für Exception
     */
    public static Mp3FileData fileToMp3FileData(File file) throws IOException, UnsupportedTagException, InvalidDataException {
        Mp3FileData mp3FileData = new Mp3FileData();
        mp3FileData.setFileName(file.getName());
        mp3FileData.setFilePath(file.getParent());
        mp3FileData.setSize(ByteFormatter.bytesToMB(file.length()));
        fileToMp3FileData(file, mp3FileData);
        return mp3FileData;
    }

    /**
     * Mappt ein File in ein bereits bestehendes Mp3FileData-Objekt.
     *
     * @param file Die Datei, welche in ein Mp3FileData gemappt werden soll.
     * @param mp3FileData Das bestehende Mp3FileData-Objekt.
     * @throws IOException TODO JavaDoc für Exception
     * @throws UnsupportedTagException TODO JavaDoc für Exception
     * @throws InvalidDataException TODO JavaDoc für Exception
     */
    public static void fileToMp3FileData(File file, Mp3FileData mp3FileData) throws IOException, UnsupportedTagException, InvalidDataException {
        Mp3File mp3File = new Mp3File(file.getAbsolutePath());
        mp3FileData.setLastModified(TimeFormatter.millisecondsToDateFormat(mp3File.getLastModified()));
        mp3FileData.setDuration(mp3File.getLengthInSeconds());
        if (mp3File.hasId3v2Tag()) {
            ID3v2 id3v2Tag = mp3File.getId3v2Tag();
            mp3FileData.setTitle(id3v2Tag.getTitle());
            mp3FileData.setArtist(id3v2Tag.getArtist());
            mp3FileData.setAlbum(id3v2Tag.getAlbum());
            mp3FileData.setGenre(id3v2Tag.getGenreDescription());
            mp3FileData.setYear(id3v2Tag.getYear());
            mp3FileData.setTrack(id3v2Tag.getTrack());
            mp3FileData.setCover(id3v2Tag.getAlbumImage());
        }
    }
}
