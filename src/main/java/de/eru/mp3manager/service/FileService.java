package de.eru.mp3manager.service;

import de.eru.mp3manager.data.Mp3FileData;
import de.eru.mp3manager.data.Playlist;
import de.eru.mp3manager.gui.applicationwindow.editfile.EditFilePresenter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldDataInvalidException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.KeyNotFoundException;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.id3.AbstractID3v2Tag;

/**
 * Service für den Zugriff auf Dateien und Verzeichnisse.
 *
 * @author Philipp Bruckner
 */
public final class FileService {

    private FileService() {
        //Utility-Klasse
    }

    /**
     * Speichert die geänderten MP3-Informationen ab.
     *
     * @param dataToSave Die zu überschreibende Datei.
     * @param changeData Die zu speichernden MP3-Informationen.
     */
    public static void saveFile(Mp3FileData dataToSave, Mp3FileData changeData) throws KeyNotFoundException, FieldDataInvalidException, CannotReadException, IOException, TagException, ReadOnlyFileException, InvalidAudioFrameException, CannotWriteException {
        MP3File file = (MP3File) AudioFileIO.read(new File(dataToSave.getAbsolutePath()));
        AbstractID3v2Tag tag = file.getID3v2Tag();
        setTagField(tag, FieldKey.TITLE, changeData.getTitle());
        setTagField(tag, FieldKey.ARTIST, changeData.getArtist());
        setTagField(tag, FieldKey.ALBUM, changeData.getAlbum());
        setTagField(tag, FieldKey.GENRE, changeData.getGenre());
        setTagField(tag, FieldKey.YEAR, changeData.getYear());
        setTagField(tag, FieldKey.TRACK, changeData.getTrack());
        tag.getFirstArtwork().setBinaryData(changeData.getCover());

        file.commit();

        if (!dataToSave.getFileName().equals(changeData.getFileName())) {
            file.getFile().renameTo(new File(dataToSave.getFilePath() + "\\" + changeData.getFileName()));
        }
    }

    private static void setTagField(AbstractID3v2Tag tag, FieldKey key, String value) throws KeyNotFoundException, FieldDataInvalidException {
        if (!value.equals(EditFilePresenter.DIFF_VALUES)) {
            tag.setField(key, value);
        }
    }

    /**
     * Speichert eine Wiedergabeliste.
     *
     * @param playlistFile Das File, in welche die Wiedergabeliste gespeichert werden soll.
     * @param playlist Die zu speichernde Wiedergabeliste.
     * @return true, wenn das Speichern erfolgreich war.
     * @throws java.io.IOException TODO Exception-Doc
     */
    public static boolean savePlaylist(File playlistFile, Playlist playlist) throws IOException {
        //TODO richtig implementieren
        if (playlistFile.exists()) {
            boolean success = playlistFile.delete(); //Funktioniert nicht wie erwartet (Änderungsdatum bleibt gleich)
            if (!success) {
                return false;
            }
        }
        try (FileWriter writer = new FileWriter(playlistFile)) {
            writer.append("Test\nTest2\nTest3");
        }
        return playlistFile.exists();
    }

    /**
     * Löscht eine Wiedergabeliste.
     *
     * @param playlistPath Der Pfad der Playlist-Datei.
     * @return true, wenn das Löschen erfolgreich war.
     */
    public static boolean deletePlaylist(String playlistPath) { //TODO richtig implementieren
        File playlistFile = new File(playlistPath);
        return playlistFile.delete();
    }

    /**
     * Sammelt alle MP3-Dateien aus einem Verzeichnis und dessen Unterverzeichnissen.
     *
     * @param directory Das auszulesende Verzeichnis.
     * @return Eine Liste von Files der MP3-Dateien.
     */
    public static ObservableList<File> collectMp3FilesFromDirectory(String directory) {
        ObservableList<File> fileList = FXCollections.observableArrayList();
        collect(directory, fileList);
        return fileList;
    }

    /**
     * Methode zum rekursiven Sammeln von MP3-Dateien aus einem Verzeichnis.
     *
     * @param directory Das auszulesende Verzeichnis.
     * @param fileList Die Liste von Files der MP3-Dateien.
     */
    private static void collect(String directory, ObservableList<File> fileList) {
        File dir = new File(directory);
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                collect(file.getAbsolutePath(), fileList);
            } else if (file.getName().endsWith(".mp3")) {
                fileList.add(file);
            }
        }
    }

    /**
     * TODO
     *
     * @param playlistFile
     * @return
     * @throws IOException
     */
    public static Playlist fileToPlaylist(File playlistFile) throws IOException {
        Playlist playlist = new Playlist();
        playlist.setAbsolutePath(playlistFile.getAbsolutePath());
        Files.lines(playlistFile.toPath()).forEach(System.out::println);
        return playlist;
    }
}
