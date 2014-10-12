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
     * @param masterData Die Liste für die Mp3FileData-Objekte.
     * @param tableDisableProperty Das BooleanProperty zum sperren/freigeben der Tabelle.
     * @return Einen Task zum Auslesen von Dateien aus einem Verzeichnis.
     */
    public static Task<Void> createReadDirectoryTask(String directory, ObservableList<Mp3FileData> masterData, BooleanProperty tableDisableProperty) {
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
                            masterData.add(new Mp3FileData(files.get(j)));
                        }
                    });
                    updateTitle("Lesen von " + directory + " abgeschlossen.");
                    updateMessage(files.size() + " Dateien wurden erfolgreich gelesen.");
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
     * @param data Die Liste mit den Mp3FileData-Objekten.
     * @return Einen Task zum Laden der Mp3-spezifischen Informationen.
     */
    public static Task<Void> createLoadFilesTask(ObservableList<Mp3FileData> data) {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    updateProgress(-1, 1);
                    for (int i = 0; i < data.size(); i++) {
                        updateTitle("Lade Datei " + (i + 1) + " von " + data.size() + "...");
                        updateMessage(data.get(i).getAbsolutePath());
                        if (!data.get(i).isLoaded()) {
                            Mapper.fileToMp3FileData(new File(data.get(i).getAbsolutePath()), data.get(i));
                            data.get(i).setLoaded(true);
                        }
                        updateProgress(i + 1, data.size());
                    }
                    updateTitle("Laden der Dateien abgeschlossen.");
                    updateMessage(data.size() + " Dateien wurden erfolgreich geladen.");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
    }

    /**
     * TODO
     */
    public static Task<Void> createSaveFilesTask(ObservableList<Mp3FileData> dataToSave, Mp3FileData changeData) {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    updateProgress(-1, 1);
                    for (int i = 0; i < dataToSave.size(); i++) {
                        updateTitle("Speichere Datei " + (i + 1) + " von " + dataToSave.size() + "...");
                        updateMessage(dataToSave.get(i).getAbsolutePath());
                        FileService.saveFile(dataToSave.get(i), changeData);
                        dataToSave.get(i).reload();
                        updateProgress(i + 1, dataToSave.size());
                    }
                    updateTitle("Speichern der Dateien abgeschlossen.");
                    updateMessage(dataToSave.size() + " Dateien wurden erfolgreich gespeichert.");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
    }
}
