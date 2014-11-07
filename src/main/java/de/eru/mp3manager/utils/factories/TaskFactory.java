package de.eru.mp3manager.utils.factories;

import de.eru.mp3manager.data.utils.Mp3Mapper;
import de.eru.mp3manager.data.Mp3FileData;
import de.eru.mp3manager.gui.utils.TablePlaceholder;
import de.eru.mp3manager.service.FileService;
import java.io.File;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.collections.FXCollections;
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
     * Dabei werden aus den Dateien bereits Mp3FileData-Objekte erzeugt und den
     * Tabellen-Daten hinzugefügt; <br>
     * diese enthalten aber noch keine MP3-spezifischen Informationen!
     *
     * @param directory Das auszulesende Verzeichnis.
     * @param masterData Die Liste für die Mp3FileData-Objekte.
     * @param tableDisable Das BooleanProperty zum sperren/freigeben der
     * Tabelle.
     * @return Einen Task zum Auslesen von Dateien aus einem Verzeichnis.
     */
    public static Task<Void> createReadDirectoryTask(String directory, ObservableList<Mp3FileData> masterData, TablePlaceholder tablePlaceholder, BooleanProperty tableDisable) {
        return new Task<Void>() {

            @Override
            protected Void call() throws Exception {
                try {
                    Platform.runLater(() -> {
                        tablePlaceholder.setText("Verzeichnis wird geladen.\nBitte warten...");
                        tablePlaceholder.setIndicatorVisible(true);
                        masterData.clear();
                    });
                    //Verzeichnis auslesen
                    updateTitle("Lese Verzeichnis...");
                    updateMessage(directory);
                    updateProgress(-1, 1);
                    ObservableList<File> files = FileService.collectMp3FilesFromDirectory(directory);

                    //Mp3Informationen laden und am Ende der Liste hinzufügen
                    updateProgress(-1, 1);
                    ObservableList<Mp3FileData> loadedData = FXCollections.observableArrayList();
                    for (int i = 0; i < files.size(); i++) {
                        updateTitle("Lade Datei " + (i + 1) + " von " + files.size() + "...");
                        updateMessage(files.get(i).getAbsolutePath());
                        loadedData.add(Mp3Mapper.fileToMp3FileData(new File(files.get(i).getAbsolutePath())));
                        updateProgress(i + 1, files.size());
                    }
                    updateTitle("Laden der Dateien abgeschlossen.");
                    updateMessage(files.size() + " Dateien wurden erfolgreich geladen.");

                    Platform.runLater(() -> {
                        if (loadedData.size() == 0) {
                            tablePlaceholder.setText("Das gewählte Verzeichnis enthält keine MP3-Dateien");
                            tablePlaceholder.setIndicatorVisible(false);
                            updateProgress(1, 1);
                        }
                        masterData.addAll(loadedData);
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
    }

    public static Task<Void> createSaveFilesTask(final ObservableList<Mp3FileData> dataToSave, final Mp3FileData changeData) {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    updateProgress(-1, 1);
                    for (int i = 0; i < dataToSave.size(); i++) {
                        updateTitle("Speichere Datei " + (i + 1) + " von " + dataToSave.size() + "...");
                        updateMessage(dataToSave.get(i).getAbsolutePath());
                        FileService.saveMp3File(dataToSave.get(i), changeData);
                        Platform.runLater(dataToSave.get(i)::reload);
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
