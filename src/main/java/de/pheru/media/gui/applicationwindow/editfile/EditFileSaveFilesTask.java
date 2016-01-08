package de.pheru.media.gui.applicationwindow.editfile;

import de.pheru.media.data.Mp3FileData;
import de.pheru.media.task.SaveFilesTask;
import javafx.collections.ObservableList;

/**
 * Created by Philipp on 08.01.2016.
 */
public class EditFileSaveFilesTask extends SaveFilesTask {
    public EditFileSaveFilesTask(ObservableList<Mp3FileData> dataToSave, Mp3FileData changeData) {
        super(dataToSave, changeData);
    }

    //TODO
    @Override
    protected void handleRenameFailed() {

    }

    @Override
    protected boolean handleSaveFailed(String fileName) {
        return false;
    }

    @Override
    protected void handleReloadFailed() {

    }
}
