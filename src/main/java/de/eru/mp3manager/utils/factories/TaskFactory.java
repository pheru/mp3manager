package de.eru.mp3manager.utils.factories;

import de.eru.mp3manager.data.utils.Mp3Mapper;
import de.eru.mp3manager.data.Mp3FileData;
import de.eru.mp3manager.data.Playlist;
import de.eru.mp3manager.gui.utils.TablePlaceholder;
import de.eru.mp3manager.service.FileService;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
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
    public static Task<Void> createReadDirectoryTask(String directory, ObservableList<Mp3FileData> masterData, TablePlaceholder tablePlaceholder,
            BooleanProperty tableDisable, List<Mp3FileData> playlistTitles) {
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
                        boolean dataAlreadyLoaded = false;
                        for (Mp3FileData title : playlistTitles) {
                            if (title.getAbsolutePath().equals(files.get(i).getAbsolutePath())) {
                                loadedData.add(title);
                                dataAlreadyLoaded = true;
                                break;
                            }
                        }
                        if (!dataAlreadyLoaded) {
                            loadedData.add(Mp3Mapper.fileToMp3FileData(new File(files.get(i).getAbsolutePath())));
                        }
                        updateProgress(i + 1, files.size());
                    }
                    updateTitle("Laden der Dateien abgeschlossen.");
                    updateMessage(loadedData.size() + " Dateien wurden erfolgreich geladen.");

                    Platform.runLater(() -> {
                        if (!loadedData.isEmpty()) {
                            masterData.addAll(loadedData);
                        } else {
                            tablePlaceholder.setText("Das gewählte Verzeichnis enthält keine MP3-Dateien!");
                            tablePlaceholder.setIndicatorVisible(false);
                            updateProgress(1, 1);
                        }
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

    public static Task<Void> createLoadPlaylistTask(Playlist playlist, File playlistFile, List<Mp3FileData> masterData) {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    updateProgress(-1, 1);
                    updateTitle("Lade Wiedergabeliste...");
                    updateMessage(playlistFile.getAbsolutePath());
                    List<String> filePaths = FileService.loadPlaylist(playlistFile);
                    List<Mp3FileData> loadedData = new ArrayList<>();
                    for (int i = 0; i < filePaths.size(); i++) {
                        boolean dataAlreadyLoaded = false;
                        for (Mp3FileData data : masterData) {
                            if (data.getAbsolutePath().equals(filePaths.get(i))) {
                                loadedData.add(data);
                                dataAlreadyLoaded = true;
                                break;
                            }
                        }
                        if (!dataAlreadyLoaded) {
                            loadedData.add(Mp3Mapper.fileToMp3FileData(new File(filePaths.get(i))));
                        }
                        updateProgress(i + 1, filePaths.size());
                    }
                    updateTitle("Laden der Wiedergabeliste abgeschlossen.");
                    updateMessage(loadedData.size() + " Titel wurden erfolgreich geladen.");

                    if (!loadedData.isEmpty()) {
                        Platform.runLater(() -> {
                            playlist.getTitles().clear();
                            playlist.setFilePath(playlistFile.getParent());
                            playlist.setFileName(playlistFile.getName());
                            playlist.getTitles().addAll(loadedData);
                        });
                    } else {
                        updateProgress(1, 1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
    }
}
