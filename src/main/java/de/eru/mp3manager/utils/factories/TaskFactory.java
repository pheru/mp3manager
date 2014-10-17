package de.eru.mp3manager.utils.factories;

import de.eru.mp3manager.data.utils.Mapper;
import de.eru.mp3manager.data.Mp3FileData;
import de.eru.mp3manager.service.FileService;
import java.io.File;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.scene.text.TextAlignment;

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
    public static Task<Void> createReadDirectoryTask(String directory, ObservableList<Mp3FileData> masterData, StringProperty tablePlaceholderText, BooleanProperty tableDisable) {
        return new Task<Void>() {

            @Override
            protected Void call() throws Exception {
                try {
                    Platform.runLater(() -> {
                        tablePlaceholderText.set("Verzeichnis wird geladen.\nBitte warten...");
                        masterData.clear();
                        tableDisable.set(true);
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
                        loadedData.add(Mapper.fileToMp3FileData(new File(files.get(i).getAbsolutePath())));
                        updateProgress(i + 1, files.size());
                    }
                    updateTitle("Laden der Dateien abgeschlossen.");
                    updateMessage(files.size() + " Dateien wurden erfolgreich geladen.");
                    
                    Platform.runLater(() -> {
                        if(loadedData.size() == 0){
                            tablePlaceholderText.set("Das gewählte Verzeichnis enthält keine MP3-Dateien");
                            updateProgress(1, 1);
                        }
                        masterData.addAll(loadedData);
                        tableDisable.set(false);
                    });
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
