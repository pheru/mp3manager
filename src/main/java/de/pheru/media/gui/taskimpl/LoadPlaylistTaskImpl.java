package de.pheru.media.gui.taskimpl;

import de.pheru.media.data.Mp3FileData;
import de.pheru.media.data.Playlist;
import de.pheru.media.task.LoadPlaylistTask;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;

import java.io.File;
import java.util.List;

/**
 * Created by Philipp on 20.02.2016.
 */
public class LoadPlaylistTaskImpl extends LoadPlaylistTask {

    private final Playlist playlist;

    public LoadPlaylistTaskImpl(Playlist playlist, File playlistFileToLoad, List<Mp3FileData> masterData) {
        super(playlistFileToLoad, masterData);
        this.playlist = playlist;
    }

    @Override
    protected void handleReadPlaylistFailed(String playlistPath) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText("Die Wiedergabeliste \"" + playlistPath + "\" konnte nicht gelesen werden!");
        Platform.runLater(alert::showAndWait);
    }

    @Override
    protected void handleLoadPlaylistInsufficient(String playlistPath, List<String> failedToLoadFilePaths) {
        showLoadAlert(playlistPath, failedToLoadFilePaths);
    }

    @Override
    protected void handleLoadPlaylistFailed(String playlistPath, List<String> failedToLoadFilePaths) {
        showLoadAlert(playlistPath, failedToLoadFilePaths);
    }

    @Override
    protected void updatePlaylist(List<Mp3FileData> loadedData, File loadedPlaylistFile) {
        Platform.runLater(() -> {
            playlist.setFilePath(loadedPlaylistFile.getParent());
            playlist.setFileName(loadedPlaylistFile.getName());
            playlist.clear();
            playlist.add(loadedData);
            playlist.setCurrentTitleIndex(0);
        });
    }

    private void showLoadAlert(String playlistPath, List<String> failedToLoadFilePaths) {
        StringBuilder fileNamesStringBuilder = new StringBuilder();
        for (String failedToLoadFileName : failedToLoadFilePaths) {
            fileNamesStringBuilder.append(failedToLoadFileName);
            fileNamesStringBuilder.append("\n");
        }
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Fehler beim Laden der Wiedergabliste \"" + playlistPath + "\"!");
        alert.setContentText("Folgende Titel konnten nicht gelesen werden:");
        alert.getDialogPane().setExpandableContent(new Label(fileNamesStringBuilder.toString()));
        Platform.runLater(alert::showAndWait);
    }
}
