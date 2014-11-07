package de.eru.mp3manager.service;

import de.eru.mp3manager.data.Mp3FileData;
import de.eru.mp3manager.data.Playlist;
import de.eru.mp3manager.data.utils.Mp3Mapper;
import de.eru.mp3manager.gui.applicationwindow.editfile.EditFilePresenter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldDataInvalidException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.KeyNotFoundException;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.datatype.Artwork;
import org.jaudiotagger.tag.id3.AbstractID3v2Tag;
import org.jaudiotagger.tag.id3.valuepair.ImageFormats;
import org.jaudiotagger.tag.reference.PictureTypes;

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
    public static void saveMp3File(Mp3FileData dataToSave, Mp3FileData changeData) throws KeyNotFoundException, FieldDataInvalidException, CannotReadException, IOException, TagException, ReadOnlyFileException, InvalidAudioFrameException, CannotWriteException {
        MP3File file = (MP3File) AudioFileIO.read(new File(dataToSave.getAbsolutePath()));
        AbstractID3v2Tag tag = file.getID3v2Tag();
        setTagField(tag, FieldKey.TITLE, changeData.getTitle());
        setTagField(tag, FieldKey.ARTIST, changeData.getArtist());
        setTagField(tag, FieldKey.ALBUM, changeData.getAlbum());
        setTagField(tag, FieldKey.GENRE, changeData.getGenre());
        setTagField(tag, FieldKey.YEAR, changeData.getYear());
        setTagField(tag, FieldKey.TRACK, changeData.getTrack());

        if (changeData.getCover() != null && changeData.getCover().length > 0) {
            Artwork newArtwork = new Artwork();
            newArtwork.setBinaryData(changeData.getCover());
            newArtwork.setMimeType(ImageFormats.getMimeTypeForBinarySignature(changeData.getCover()));
            newArtwork.setDescription("");
            newArtwork.setPictureType(PictureTypes.DEFAULT_ID); //DEFAULT_ID == 3 == Cover (Front)
            tag.deleteArtworkField();
            tag.setField(newArtwork);
        }
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

    public static List<String> loadPlaylist(File playlistFile) throws IOException {
        List<String> playlistTitles = new ArrayList<>();
        Files.lines(playlistFile.toPath())
                .filter(s -> !s.isEmpty())
                .forEach(playlistTitles::add);
        return playlistTitles;
    }

    /**
     * Speichert eine Wiedergabeliste.
     *
     * @param playlistFile Das File, in welche die Wiedergabeliste gespeichert
     * werden soll.
     * @return true, wenn das Speichern erfolgreich war.
     */
    public static boolean savePlaylist(File playlistFile, List<Mp3FileData> playlistTitles) throws IOException {
        if (playlistFile.exists()) {
            if (!playlistFile.delete()) {//Funktioniert nicht wie erwartet (Änderungsdatum bleibt gleich)
                return false;
            }
        }
        try (FileWriter writer = new FileWriter(playlistFile)) {
            for (int i = 0; i < playlistTitles.size(); i++) {
                writer.append(playlistTitles.get(i).getAbsolutePath());
                if (i < playlistTitles.size() - 1) {
                    writer.append(Playlist.FILE_SPLIT);
                }
            }
        }
        return playlistFile.exists();
    }

    public static boolean deleteFile(String filePath) {
        File file = new File(filePath);
        return file.delete();
    }

    /**
     * Sammelt alle MP3-Dateien aus einem Verzeichnis und dessen
     * Unterverzeichnissen.
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
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    collect(file.getAbsolutePath(), fileList);
                } else if (file.getName().endsWith(".mp3")) {
                    fileList.add(file);
                }
            }
        } else {
            //TODO Verzeichnis existiert nicht
        }
    }
}
