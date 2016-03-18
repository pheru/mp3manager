package de.pheru.media.gui.taskimpl;

import de.pheru.media.data.Mp3FileData;
import de.pheru.media.task.ReadDirectoryTask;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;

import java.util.List;

/**
 * Created by Philipp on 20.02.2016.
 */
public class ReadDirectoryTaskImpl extends ReadDirectoryTask {

    /**
     * @param directory      Das auszulesende Verzeichnis.
     * @param masterData     Die Liste für die Mp3FileData-Objekte.
     * @param playlistTitles
     */
    public ReadDirectoryTaskImpl(String directory, ObservableList<Mp3FileData> masterData, ObservableList<Mp3FileData> playlistTitles) {
        super(directory, masterData, playlistTitles);
    }

    @Override
    protected void init(List<Mp3FileData> masterData) {
        Platform.runLater(masterData::clear);
    }

    @Override
    protected void finished(List<Mp3FileData> masterData, List<Mp3FileData> loadedData) {
        Platform.runLater(()->{
            masterData.addAll(loadedData);
        });
    }

    @Override
    protected void handleReadDirectoryInsufficient(String directory, List<String> failedToLoadFileNames) {
        showAlert(directory, failedToLoadFileNames);
    }

    @Override
    protected void handleReadDirectoryFailed(String directory, List<String> failedToLoadFileNames) {
        showAlert(directory, failedToLoadFileNames);
    }

    private void showAlert(String directory, List<String> failedToLoadFileNames) {
        StringBuilder fileNamesStringBuilder = new StringBuilder();
        for (String failedToLoadFileName : failedToLoadFileNames) {
            fileNamesStringBuilder.append(failedToLoadFileName);
            fileNamesStringBuilder.append("\n");
        }
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Fehler beim Lesen des Verzeichnisses \"" + directory + "\"!");
            alert.setContentText("Folgende Dateien konnten nicht gelesen werden:");
            alert.getDialogPane().setExpandableContent(new Label(fileNamesStringBuilder.toString()));
            alert.showAndWait();
        });
    }
}
