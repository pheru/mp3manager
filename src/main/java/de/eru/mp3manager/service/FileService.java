package de.eru.mp3manager.service;

import de.eru.mp3manager.data.Mp3FileData;
import de.eru.mp3manager.data.Playlist;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
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

    public static List<String> loadPlaylist(File playlistFile) throws IOException { //TODO Fehlerhafte Playlists verarbeiten
        List<String> playlistTitles = new ArrayList<>();
        try (Stream<String> lines = Files.lines(playlistFile.toPath())) {
            lines.filter(s -> !s.isEmpty())
                    .forEach(playlistTitles::add);
        }
        return playlistTitles;
    }

    /**
     * Speichert eine Wiedergabeliste.
     *
     * @param playlistFile Das File, in welches die Wiedergabeliste gespeichert
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
        }
    }
}
