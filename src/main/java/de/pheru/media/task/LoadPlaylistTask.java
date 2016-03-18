package de.pheru.media.task;

import de.pheru.media.data.Mp3FileData;
import de.pheru.media.data.Playlist;
import de.pheru.media.exceptions.Mp3FileDataException;
import de.pheru.media.util.FileUtil;
import javafx.application.Platform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Philipp Bruckner
 */
public abstract class LoadPlaylistTask extends PheruMediaTask {

    private static final Logger LOGGER = LogManager.getLogger(LoadPlaylistTask.class);

    private final File playlistFileToLoad;
    private final List<Mp3FileData> masterData;

    public LoadPlaylistTask(File playlistFileToLoad, List<Mp3FileData> masterData) {
        this.playlistFileToLoad = playlistFileToLoad;
        this.masterData = masterData;
    }

    protected abstract void handleReadPlaylistFailed(String playlistPath);

    protected abstract void handleLoadPlaylistInsufficient(String playlistPath, List<String> failedToLoadFilePaths);

    protected abstract void handleLoadPlaylistFailed(String playlistPath, List<String> failedToLoadFilePaths);

    protected abstract void updatePlaylist(List<Mp3FileData> loadedData, File loadedPlaylistFile);

    @Override
    protected void callImpl() {
        updateProgress(-1, 1);
        updateTitle("Lade Wiedergabeliste " + playlistFileToLoad.getName() + "...");
        updateMessage("Lese Datei...");
        List<String> failedToLoadFilePaths = new ArrayList<>();
        List<String> filePaths;
        try {
            filePaths = FileUtil.readLinesFromFile(playlistFileToLoad, true);
        } catch (IOException e) {
            LOGGER.error("Exception loading playlist!", e);
            setStatus(PheruMediaTaskStatus.FAILED);
            updateTitle("Laden der Wiedergabeliste fehlgeschlagen!");
            updateProgress(1, 1);
            handleReadPlaylistFailed(playlistFileToLoad.getAbsolutePath());
            return;
        }
        List<Mp3FileData> loadedData = loadDataFromFilepaths(filePaths, failedToLoadFilePaths);

        updateTitle("Laden der Wiedergabeliste abgeschlossen.");
        updateMessage(loadedData.size() + " Titel wurden erfolgreich geladen.");

        if (loadedData.isEmpty() && !filePaths.isEmpty()) {
            setStatus(PheruMediaTaskStatus.FAILED);
            handleLoadPlaylistInsufficient(playlistFileToLoad.getAbsolutePath(), failedToLoadFilePaths);
        } else if (loadedData.size() < filePaths.size()) {
            setStatus(PheruMediaTaskStatus.INSUFFICIENT);
            handleLoadPlaylistFailed(playlistFileToLoad.getAbsolutePath(), failedToLoadFilePaths);
        } else {
            setStatus(PheruMediaTaskStatus.SUCCESSFUL);
        }

        updatePlaylist(loadedData, playlistFileToLoad);
    }

    private List<Mp3FileData> loadDataFromFilepaths(List<String> filePaths, List<String> failedToLoadFilePaths) {
        List<Mp3FileData> loadedData = new ArrayList<>();
        for (int i = 0; i < filePaths.size(); i++) {
            if (isCancelled()) {
                updateTitle("Laden der Wiedergabeliste abgebrochen!");
                updateMessage(loadedData.size() + " von " + filePaths.size() + " Dateien wurden erfolgreich geladen.");
                updateProgress(1, 1);
                setStatus(PheruMediaTaskStatus.INSUFFICIENT);
                break;
            }
            updateMessage("Lade Titel " + (i + 1) + " von " + filePaths.size() + "...");
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
                    loadedData.add(new Mp3FileData(new File(filePaths.get(i))));
                } catch (Mp3FileDataException e) {
                    LOGGER.error("Exception loading Mp3FileData!", e);
                    failedToLoadFilePaths.add(filePaths.get(i));
                }
            }
            updateProgress(i + 1, filePaths.size());
        }
        return loadedData;
    }

}
