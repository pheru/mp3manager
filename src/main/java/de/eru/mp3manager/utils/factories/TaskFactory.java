package de.eru.mp3manager.utils.factories;

import de.eru.mp3manager.data.utils.Mapper;
import de.eru.mp3manager.data.Mp3FileData;
import de.eru.mp3manager.service.FileService;
import java.io.File;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

/**
 * Klasse zum erzeugen von Tasks.
 *
 * @author Philipp Bruckner
 */
public final class TaskFactory {

    private TaskFactory() {
        //Utility-Klasse
    }

    /**
     * Erzeugt einen Task zum Auslesen von Dateien aus einem Verzeichnis. <br>
     * Dabei werden aus den Dateien bereits Mp3FileData-Objekte erzeugt und den Tabellen-Daten hinzugefügt; <br>
     * diese enthalten aber noch keine MP3-spezifischen Informationen!
     *
     * @param directory Das auszulesende Verzeichnis.
     * @param tableData Die Liste für die Mp3FileData-Objekte der Tabelle.
     * @param tableDisableProperty Das BooleanProperty zum sperren/freigeben der Tabelle.
     * @return Einen Task zum Auslesen von Dateien aus einem Verzeichnis.
     */
    public static Task<Void> createReadDirectyTask(String directory, ObservableList<Mp3FileData> tableData, BooleanProperty tableDisableProperty) {
        return new Task<Void>() {

            @Override
            protected Void call() throws Exception {
                try {
                    tableDisableProperty.set(true);
                    updateTitle("Lese Verzeichnis...");
                    updateMessage(directory);
                    updateProgress(-1, 1);
                    ObservableList<File> files = FileService.collectMp3FilesFromDirectory(directory);
                    Platform.runLater(() -> {
                        for (int i = 0; i < files.size(); i++) {
                            final int j = i;
                            tableData.add(new Mp3FileData(files.get(j)));
                        }
                    });
                    tableDisableProperty.set(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
    }

    /**
     * Erzeugt einen Task zum Laden der MP3-spezifischen Informationen.
     *
     * @param tableData Die Liste mit den Mp3FileData-Objekten der Tabelle.
     * @return Einen Task zum Laden der Mp3-spezifischen Informationen.
     */
    public static Task<Void> createLoadFilesTask(ObservableList<Mp3FileData> tableData) {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {

                    for (int i = 0; i < tableData.size(); i++) {
                        updateTitle("Lade Datei " + (i + 1) + " von " + tableData.size() + "...");
                        updateMessage(tableData.get(i).getAbsolutePath());
                        if (!tableData.get(i).isLoaded()) {
                            Mapper.fileToMp3FileData(new File(tableData.get(i).getAbsolutePath()), tableData.get(i));
                            tableData.get(i).setLoaded(true);
                        }
                        updateProgress(i + 1, tableData.size());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
    }
}
