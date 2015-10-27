package de.pheru.media.task;

import de.pheru.media.data.Mp3FileData;
import de.pheru.media.exceptions.Mp3FileDataException;
import de.pheru.media.util.FileUtil;
import java.io.File;
import java.util.List;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Task zum Auslesen von Dateien aus einem Verzeichnis.
 *
 * @author Philipp Bruckner
 */
public class ReadDirectoryTask extends PheruMediaTask {

    private static final Logger LOGGER = LogManager.getLogger(ReadDirectoryTask.class);

    private final String directory;
    private final ObservableList<Mp3FileData> masterData;
    private final ObservableList<Mp3FileData> playlistTitles;

    /**
     * @param directory Das auszulesende Verzeichnis.
     * @param masterData Die Liste für die Mp3FileData-Objekte.
     * @param playlistTitles
     */
    public ReadDirectoryTask(final String directory, final ObservableList<Mp3FileData> masterData,
            final ObservableList<Mp3FileData> playlistTitles) {
        this.directory = directory;
        this.masterData = masterData;
        this.playlistTitles = playlistTitles;
    }

    //TODO Innercall überarbeiten: zu unübersichtlich und lang
    @Override
    protected void innerCall() {
        Platform.runLater(() -> {
            masterData.clear();
        });
        //Verzeichnis auslesen
        updateTitle("Lese Verzeichnis...");
        updateMessage(directory);
        updateProgress(-1, 1);
        ObservableList<File> files = FileUtil.collectMp3FilesFromDirectory(directory);

        //Mp3Informationen laden und am Ende der Liste hinzufügen
        ObservableList<Mp3FileData> loadedData = FXCollections.observableArrayList();
        ObservableList<String> failedToLoadFileNames = FXCollections.observableArrayList();
        for (int i = 0; i < files.size(); i++) {
            if (isCancelled()) {
                updateTitle("Laden der Dateien abgebrochen!");
                updateMessage(loadedData.size() + " von " + files.size() + " Dateien wurden erfolgreich geladen.");
                updateProgress(1, 1);
                setStatus(PheruMediaTaskStatus.INSUFFICIENT);
                break;
            }
            File currentFile = files.get(i);
            updateTitle("Lade Datei " + (i + 1) + " von " + files.size() + "...");
            updateMessage(currentFile.getAbsolutePath());
            boolean dataAlreadyInPlaylist = false;
            for (Mp3FileData playlistTitle : playlistTitles) {
                if (playlistTitle.getAbsolutePath().equals(currentFile.getAbsolutePath())) {
                    loadedData.add(playlistTitle);
                    dataAlreadyInPlaylist = true;
                    break;
                }
            }
            if (!dataAlreadyInPlaylist) {
                try {
                    loadedData.add(new Mp3FileData(new File(currentFile.getAbsolutePath())));
                } catch (Mp3FileDataException e) {
                    LOGGER.error("Exception loading Mp3FileData for file \"" + currentFile.getAbsolutePath() + "\"!", e);
                    failedToLoadFileNames.add(currentFile.getAbsolutePath());
                }
            }
            updateProgress(i + 1, files.size());
        }
        if (!isCancelled()) {
            updateTitle("Laden der Dateien abgeschlossen.");
            updateMessage(loadedData.size() + " von " + files.size() + " Dateien wurden erfolgreich geladen.");
            if (loadedData.isEmpty() && !files.isEmpty()) {
                setStatus(PheruMediaTaskStatus.FAILED);
                Platform.runLater(() -> {
                    showFailedAlert(directory, failedToLoadFileNames);
                });
            } else if (loadedData.size() < files.size()) {
                setStatus(PheruMediaTaskStatus.INSUFFICIENT);
                Platform.runLater(() -> {
                    showFailedAlert(directory, failedToLoadFileNames);
                });
            } else {
                setStatus(PheruMediaTaskStatus.SUCCESSFUL);
            }
        }
        Platform.runLater(() -> {
            if (loadedData.isEmpty()) {
                updateProgress(1, 1);
            } else {
                masterData.addAll(loadedData);
            }
        });
    }

    @Deprecated //TODO Keine GUI in Task
    private void showFailedAlert(String directory, List<String> failedToLoadFileNames) {
        StringBuilder fileNamesStringBuilder = new StringBuilder();
        for (String failedToLoadFileName : failedToLoadFileNames) {
            fileNamesStringBuilder.append(failedToLoadFileName);
            fileNamesStringBuilder.append("\n");
        }
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Fehler beim Lesen des Verzeichnisses \"" + directory + "\"!");
        alert.setContentText("Folgende Dateien konnten nicht gelesen werden:");
        alert.getDialogPane().setExpandableContent(new Label(fileNamesStringBuilder.toString()));
        alert.showAndWait();
    }
}
