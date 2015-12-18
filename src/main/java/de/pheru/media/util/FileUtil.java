package de.pheru.media.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Utility-Klasse für den Zugriff auf Dateien und Verzeichnisse.
 *
 * @author Philipp Bruckner
 */
public final class FileUtil {

    private FileUtil() {
        //Utility-Klasse
    }

    public static List<String> readLinesFromFile(File file, boolean skipEmptyLines) throws IOException {
        List<String> lines = new ArrayList<>();
        try (Stream<String> stream = Files.lines(file.toPath())) {
            stream.filter(s -> !skipEmptyLines || !s.isEmpty())
                    .forEach(lines::add);
        }
        return lines;
    }

    /**
     * Sammelt alle MP3-Dateien aus einem Verzeichnis und dessen
     * Unterverzeichnissen.
     *
     * @param directory Das auszulesende Verzeichnis.
     * @return Eine Liste von Files der MP3-Dateien.
     */
    public static List<File> collectMp3FilesFromDirectory(String directory) {
        List<File> fileList = new ArrayList<>();
        collect(directory, fileList);
        return fileList;
    }

    /**
     * Methode zum rekursiven Sammeln von MP3-Dateien aus einem Verzeichnis.
     *
     * @param directory Das auszulesende Verzeichnis.
     * @param fileList  Die Liste von Files der MP3-Dateien.
     */
    private static void collect(String directory, List<File> fileList) {
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
