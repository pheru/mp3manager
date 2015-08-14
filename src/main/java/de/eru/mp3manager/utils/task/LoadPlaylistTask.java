package de.eru.mp3manager.utils.task;

import de.eru.mp3manager.data.Mp3FileData;
import de.eru.mp3manager.data.Playlist;
import de.eru.mp3manager.data.utils.Mp3Mapper;
import de.eru.mp3manager.service.FileService;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;

/**
 *
 * @author Philipp Bruckner
 */
public class LoadPlaylistTask extends Mp3ManagerTask {

    private final Playlist playlist;
    private final File playlistFile;
    private final List<Mp3FileData> masterData;

    public LoadPlaylistTask(Playlist playlist, File playlistFile, List<Mp3FileData> masterData) {
        this.playlist = playlist;
        this.playlistFile = playlistFile;
        this.masterData = masterData;
    }

    @Override
    protected void innerCall() {
        updateProgress(-1, 1);
        updateTitle("Lade Wiedergabeliste...");
        updateMessage(playlistFile.getAbsolutePath());
        List<Mp3FileData> loadedData = new ArrayList<>();
        try {
            List<String> filePaths = FileService.loadPlaylist(playlistFile);
            for (int i = 0; i < filePaths.size(); i++) {
                boolean dataAlreadyInMasterData = false;
                for (Mp3FileData data : masterData) {
                    if (data.getAbsolutePath().equals(filePaths.get(i))) {
                        loadedData.add(data);
                        dataAlreadyInMasterData = true;
                        break;
                    }
                }
                if (!dataAlreadyInMasterData) {
                    try {
                        loadedData.add(Mp3Mapper.fileToMp3FileData(new File(filePaths.get(i))));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                updateProgress(i + 1, filePaths.size());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        updateTitle("Laden der Wiedergabeliste abgeschlossen.");
        updateMessage(loadedData.size() + " Titel wurden erfolgreich geladen.");

        if (!loadedData.isEmpty()) {
            Platform.runLater(() -> {
                playlist.setFilePath(playlistFile.getParent());
                playlist.setFileName(playlistFile.getName());
                playlist.clear();
                playlist.add(loadedData);
                playlist.setCurrentTitleIndex(0);
            });
        } else {
            updateProgress(1, 1);
        }
    }

}
