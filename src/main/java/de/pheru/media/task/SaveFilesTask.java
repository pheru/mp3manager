package de.pheru.media.task;

import de.pheru.media.data.Mp3FileData;
import de.pheru.media.exceptions.Mp3FileDataException;
import de.pheru.media.exceptions.RenameFailedException;
import de.pheru.media.exceptions.SaveFailedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Task zum Speichern von MP3-Dateien.
 */
public abstract class SaveFilesTask extends PheruMediaTask {

    private static final Logger LOGGER = LogManager.getLogger(SaveFilesTask.class);

    private final List<Mp3FileData> dataToSave;
    private final Mp3FileData changeData;

    public SaveFilesTask(final List<Mp3FileData> dataToSave, final Mp3FileData changeData) {
        this.dataToSave = dataToSave;
        this.changeData = changeData;
    }

    protected abstract void handleRenameFailed();

    /**
     * @param fileName
     * @return true, wenn der Speichervorgang fortgesetzt werden soll.
     */
    protected abstract boolean handleSaveFailed(String fileName, boolean lastFileToSave);

    protected abstract void handleReloadFailed(String fileName);

    @Override
    protected void callImpl() {
        int successfullySaved = 0;
        updateProgress(-1, 1);
        for (int i = 0; i < dataToSave.size(); i++) {
            if (isCancelled()) {
                updateTitle("Speichern der Dateien abgebrochen!");
                updateMessage(successfullySaved + " von " + dataToSave.size() + " Dateien wurden erfolgreich gespeichert.");
                updateProgress(1, 1);
                setStatus(PheruMediaTaskStatus.INSUFFICIENT);
                return;
            }
            updateTitle("Speichere Datei " + (i + 1) + " von " + dataToSave.size() + "...");
            updateMessage(dataToSave.get(i).getAbsolutePath());
            try {
                dataToSave.get(i).save(changeData);
                successfullySaved++;
            } catch (RenameFailedException e) {
                LOGGER.warn("Exception renaming file!", e);
                handleRenameFailed();
            } catch (SaveFailedException e) {
                LOGGER.warn("Exception saving file!", e);
                boolean lastFileToSave = i == dataToSave.size() - 1;
                if (!handleSaveFailed(dataToSave.get(i).getFileName(), lastFileToSave)) {
                    cancel();
                }
            } catch (Mp3FileDataException e) {
                LOGGER.error("Exception reloading saved file!", e);
                handleReloadFailed(dataToSave.get(i).getFileName());
            }
            updateProgress(i + 1, dataToSave.size());
        }
        updateTitle("Speichern der Dateien abgeschlossen.");
        updateMessage(successfullySaved + " von " + dataToSave.size() + " Dateien wurden erfolgreich gespeichert.");
        if (successfullySaved == 0) {
            setStatus(PheruMediaTaskStatus.FAILED);
        } else if (successfullySaved < dataToSave.size()) {
            setStatus(PheruMediaTaskStatus.INSUFFICIENT);
        } else {
            setStatus(PheruMediaTaskStatus.SUCCESSFUL);
        }
    }
}
