package de.eru.mp3manager.service;

import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.NotSupportedException;
import com.mpatric.mp3agic.UnsupportedTagException;
import de.eru.mp3manager.data.Mp3FileData;
import de.eru.mp3manager.data.Playlist;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

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
    public static void saveFile(Mp3FileData dataToSave, Mp3FileData changeData) throws IOException, UnsupportedTagException, InvalidDataException, NotSupportedException {
        Mp3File mp3File = new Mp3File(dataToSave.getAbsolutePath());
        if (mp3File.hasId3v2Tag()) {
            ID3v2 tag = mp3File.getId3v2Tag();
            tag.setArtist(changeData.getArtist());
            tag.setAlbum(changeData.getAlbum());
            tag.setGenreDescription(changeData.getGenre());
            tag.setYear(changeData.getYear());
            tag.setTrack(changeData.getTrack());
//            tag.setAlbumImage(changeData.getCover(), null); //TODO Cover
        }
        if (dataToSave.getAbsolutePath().equals(changeData.getAbsolutePath())) {
            //TODO Bei gleichem Namen muss eine "Zwischendatei" erzeugt werden
        }
        mp3File.save(changeData.getAbsolutePath());
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
