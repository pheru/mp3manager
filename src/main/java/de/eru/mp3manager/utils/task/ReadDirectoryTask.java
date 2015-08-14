package de.eru.mp3manager.utils.task;

import de.eru.mp3manager.data.Mp3FileData;
import de.eru.mp3manager.data.utils.Mp3Mapper;
import de.eru.mp3manager.gui.applicationwindow.main.MainPresenter;
import de.eru.mp3manager.gui.utils.TablePlaceholders;
import de.eru.mp3manager.service.FileService;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;

/**
 * Task zum Auslesen von Dateien aus einem Verzeichnis.
 *
 * @author Philipp Bruckner
 */
public class ReadDirectoryTask extends Mp3ManagerTask {

    private final String directory;
    private final ObservableList<Mp3FileData> masterData;
    private final ObjectProperty<Node> placeholderProperty; //GUI-Objekt in Logik?
    private final List<Mp3FileData> playlistTitles;

    /**
     * @param directory Das auszulesende Verzeichnis.
     * @param masterData Die Liste für die Mp3FileData-Objekte.
     * @param placeholderProperty
     * @param playlistTitles
     */
    public ReadDirectoryTask(final String directory, final ObservableList<Mp3FileData> masterData,
            final ObjectProperty<Node> placeholderProperty, final List<Mp3FileData> playlistTitles) {
        this.directory = directory;
        this.masterData = masterData;
        this.placeholderProperty = placeholderProperty;
        this.playlistTitles = playlistTitles;
    }

    @Override
    protected void innerCall() {
        Platform.runLater(() -> {
            placeholderProperty.set(TablePlaceholders.READING_DIRECTORY);
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
            if (isCancelled()) {
                updateTitle("Laden der Dateien abgebrochen!");
                updateMessage(loadedData.size() + " von " + files.size() + " Dateien wurden erfolgreich geladen.");
                updateProgress(1, 1);
                setStatus(Status.INSUFFICIENT);
                break;
            }
            updateTitle("Lade Datei " + (i + 1) + " von " + files.size() + "...");
            updateMessage(files.get(i).getAbsolutePath());
            boolean dataAlreadyInPlaylist = false;
            for (Mp3FileData title : playlistTitles) {
                if (title.getAbsolutePath().equals(files.get(i).getAbsolutePath())) {
                    loadedData.add(title);
                    dataAlreadyInPlaylist = true;
                    break;
                }
            }
            if (!dataAlreadyInPlaylist) {
                try {
                    loadedData.add(Mp3Mapper.fileToMp3FileData(new File(files.get(i).getAbsolutePath())));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            updateProgress(i + 1, files.size());
        }
        if (!isCancelled()) {
            updateTitle("Laden der Dateien abgeschlossen.");
            updateMessage(loadedData.size() + " von " + files.size() + " Dateien wurden erfolgreich geladen.");
            setStatus(Status.SUCCESSFUL);
        }

        Platform.runLater(() -> {
            if (!loadedData.isEmpty()) {
                masterData.addAll(loadedData);
                placeholderProperty.set(MainPresenter.DEFAULT_TABLE_PLACEHOLDER);
            } else {
                placeholderProperty.set(TablePlaceholders.EMPTY_DIRECTORY);
                updateProgress(1, 1);
            }
        });
    }

}
