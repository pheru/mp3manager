package de.pheru.media.task;

import de.pheru.media.data.Mp3FileData;
import de.pheru.media.exceptions.Mp3FileDataException;
import de.pheru.media.util.FileUtil;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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

    private final List<Mp3FileData> loadedData = new ArrayList<>();
    private final List<String> failedToLoadFileNames = new ArrayList<>();

    /**
     * @param directory      Das auszulesende Verzeichnis.
     * @param masterData     Die Liste für die Mp3FileData-Objekte.
     * @param playlistTitles
     */
    public ReadDirectoryTask(final String directory, final ObservableList<Mp3FileData> masterData,
            final ObservableList<Mp3FileData> playlistTitles) {
        this.directory = directory;
        this.masterData = masterData;
        this.playlistTitles = playlistTitles;
    }

    @Override
    protected void callImpl() {
        Platform.runLater(masterData::clear);

        List<File> files = readDirectory();
        loadData(files);

        if (!isCancelled()) {
            updateTitle("Laden der Dateien abgeschlossen.");
            updateMessage(loadedData.size() + " von " + files.size() + " Dateien wurden erfolgreich geladen.");
            if (loadedData.isEmpty() && !files.isEmpty()) {
                setStatus(PheruMediaTaskStatus.FAILED);
                Platform.runLater(() -> showFailedAlert(directory, failedToLoadFileNames));
            } else if (loadedData.size() < files.size()) {
                setStatus(PheruMediaTaskStatus.INSUFFICIENT);
                Platform.runLater(() -> showFailedAlert(directory, failedToLoadFileNames));
            } else {
                setStatus(PheruMediaTaskStatus.SUCCESSFUL);
            }
        }
        Platform.runLater(() -> {
            updateProgress(1, 1);
            masterData.addAll(loadedData);
        });
    }

    private List<File> readDirectory() {
        updateTitle("Lese Verzeichnis...");
        updateMessage(directory);
        updateProgress(-1, 1);
        return FileUtil.collectMp3FilesFromDirectory(directory);
    }

    private void loadData(List<File> files) {
        for (int i = 0; i < files.size(); i++) {
            if (isCancelled()) {
                updateTitle("Laden der Dateien abgebrochen!");
                updateMessage(loadedData.size() + " von " + files.size() + " Dateien wurden erfolgreich geladen.");
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
    }

    @Deprecated //TODO Keine GUI in Task
    private void showFailedAlert(String directory, List<String> failedToLoadFileNames) {
        StringBuilder fileNamesStringBuilder = new StringBuilder();
        for (String failedToLoadFileName : failedToLoadFileNames) {
            fileNamesStringBuilder.append(failedToLoadFileName);
            fileNamesStringBuilder.append("\n");
        }
//        Alert alert = new Alert(Alert.AlertType.ERROR);
//        alert.setHeaderText("Fehler beim Lesen des Verzeichnisses \"" + directory + "\"!");
//        alert.setContentText("Folgende Dateien konnten nicht gelesen werden:");
//        alert.getDialogPane().setExpandableContent(new Label(fileNamesStringBuilder.toString()));
//        alert.showAndWait();
    }
}
