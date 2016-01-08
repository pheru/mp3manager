package de.pheru.media.task;

import de.pheru.media.data.Mp3FileData;
import de.pheru.media.exceptions.Mp3FileDataException;
import de.pheru.media.exceptions.RenameFailedException;
import de.pheru.media.exceptions.SaveFailedException;
import javafx.collections.ObservableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Task zum Speichern von MP3-Dateien.
 *
 * @author Philipp Bruckner
 */
public abstract class SaveFilesTask extends PheruMediaTask {

    private static final Logger LOGGER = LogManager.getLogger(SaveFilesTask.class);

    private final ObservableList<Mp3FileData> dataToSave; //TODO Observable nötig?
    private final Mp3FileData changeData;

    public SaveFilesTask(final ObservableList<Mp3FileData> dataToSave, final Mp3FileData changeData) {
        this.dataToSave = dataToSave;
        this.changeData = changeData;
    }

    protected abstract void handleRenameFailed();

    protected abstract boolean handleSaveFailed(String fileName); //TODO filename nötig?

    protected abstract void handleReloadFailed();

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
                LOGGER.info("Exception renaming file!", e);
                handleRenameFailed();
            } catch (SaveFailedException e) {
                LOGGER.info("Exception saving file!", e);
                if (!handleSaveFailed(dataToSave.get(i).getFileName())) {
                    cancel();
                }
            } catch (Mp3FileDataException e) {
                LOGGER.error("Exception reloading saved file!", e);
                handleReloadFailed(); //TODO evtl. filename als Parameter
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

//    protected abstract void handleRenameFailed();
    //TODO Keine GUI in Task
//        Platform.runLater(() -> {
//            Alert alert = new Alert(Alert.AlertType.ERROR);
//            alert.setHeaderText("Speichern fehlgeschlagen!");
//            alert.setContentText("Dateiname konnte nicht geändert werden!\n\n"
//                    + "Möglicherweise enthält der Dateiname ungültige Zeichen oder eine Datei mit diesem Namen existiert bereits.");
//            alert.showAndWait();
//        });
//    }

//    protected abstract boolean handleSaveFailed(String fileName); //TODO filename nötig?
//        //TODO keine GUI in Task
//        if (firstFail || showAlertOnNextFail.get()) {
//            firstFail = false;
//            FutureTask<Void> alertTask = new FutureTask<>(() -> {
////                Alert alert = new Alert(Alert.AlertType.ERROR);
////                alert.setHeaderText("Speichern fehlgeschlagen!");
////                Label contentText = new Label("Fehler beim Speichern der Datei \"" + fileName
////                        + "\"!\n\nMöglicherweise ist die Datei schreibgeschützt oder in einer anderen Anwendung geöffnet.");
////
////                CheckBox continueBox = new CheckBox("Mit verbleibenden Dateien fortfahren");
////                continueBox.selectedProperty().bindBidirectional(continueOnFail);
////                CheckBox showAlertBox = new CheckBox("Diesen Dialog bei Fehler erneut zeigen");
////                showAlertBox.selectedProperty().bindBidirectional(showAlertOnNextFail);
////                showAlertBox.disableProperty().bind(continueBox.selectedProperty().not());
////
////                VBox content = new VBox(contentText);
////                if (dataToSave.size() > 1) {
////                    Pane pane = new Pane();
////                    pane.setMinHeight(20);
////                    pane.setPrefWidth(0);
////                    content.getChildren().addAll(pane, continueBox, showAlertBox);
////                }
////
////                alert.getDialogPane().setContent(content);
////                alert.showAndWait();
//            }, null);
//
////            Platform.runLater(alertTask);
//
////            try {
////                alertTask.get();
////            } catch (InterruptedException | ExecutionException e) {
////                if (isCancelled()) {
////                    return;
////                }
////                throw new PheruMediaRuntimeException("Exception waiting for FutureTask!", e);
////            }
//        }
//    }

}
