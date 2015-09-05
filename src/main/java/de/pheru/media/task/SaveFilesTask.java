package de.pheru.media.task;

import de.pheru.media.data.Mp3FileData;
import de.pheru.media.exceptions.PheruMediaRuntimeException;
import de.pheru.media.exceptions.RenameFailedException;
import de.pheru.media.exceptions.SaveFailedException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Task zum Speichern von MP3-Dateien.<br/>
 * Pro Task kann der Dateiname nur für eine einzelne Datei gespeichert werden.
 * Sonstige Tag-Informationen können für mehrere Dateien gleichzeitig
 * gespeichert werden.
 *
 * @author Philipp Bruckner
 */
public class SaveFilesTask extends PheruMediaTask {

    private static final Logger LOGGER = LogManager.getLogger(SaveFilesTask.class);

    private final ObservableList<Mp3FileData> dataToSave;
    private final Mp3FileData changeData;

    private boolean firstFail = true;
    private final BooleanProperty continueOnFail = new SimpleBooleanProperty(true);
    private final BooleanProperty showAlertOnNextFail = new SimpleBooleanProperty(true);

    public SaveFilesTask(final ObservableList<Mp3FileData> dataToSave, final Mp3FileData changeData) {
        this.dataToSave = dataToSave;
        this.changeData = changeData;
    }

    @Override
    protected void innerCall() {
        int successfullySaved = 0;
        updateProgress(-1, 1);
        for (int i = 0; i < dataToSave.size(); i++) {
            if (isCancelled()) {
                updateTitle("Speichern der Dateien abgebrochen!");
                updateMessage(successfullySaved + " von " + dataToSave.size() + " Dateien wurden erfolgreich gespeichert.");
                updateProgress(1, 1);
                setStatus(Status.INSUFFICIENT);
                return;
            }
            updateTitle("Speichere Datei " + (i + 1) + " von " + dataToSave.size() + "...");
            updateMessage(dataToSave.get(i).getAbsolutePath());
            try {
                dataToSave.get(i).save(changeData);
                successfullySaved++;
            } catch (RenameFailedException e) {
                handleRenameFailed(e);
            } catch (SaveFailedException e) {
                handleSaveFailed(e, dataToSave.get(i).getFileName());
                if (!continueOnFail.get()) {
                    cancel();
                }
            }
            updateProgress(i + 1, dataToSave.size());
        }
        updateTitle("Speichern der Dateien abgeschlossen.");
        updateMessage(successfullySaved + " von " + dataToSave.size() + " Dateien wurden erfolgreich gespeichert.");
        if (successfullySaved == 0) {
            setStatus(Status.FAILED);
        } else if (successfullySaved < dataToSave.size()) {
            setStatus(Status.INSUFFICIENT);
        } else {
            setStatus(Status.SUCCESSFUL);
        }
    }

    private void handleRenameFailed(RenameFailedException renameFailedException) {
        LOGGER.info("Exception renaming file!", renameFailedException);
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Speichern fehlgeschlagen!");
        alert.setContentText("Dateiname konnte nicht geändert werden!\n\n"
                + "Möglicherweise enthält der Dateiname ungültige Zeichen oder eine Datei mit diesem Namen existiert bereits.");
        Platform.runLater(alert::showAndWait);
    }

    private void handleSaveFailed(SaveFailedException saveFailedException, String fileName) {
        LOGGER.info("Exception saving file!", saveFailedException);
        if (firstFail || showAlertOnNextFail.get()) {
            firstFail = false;
            FutureTask<Void> alertTask = new FutureTask<>(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Speichern fehlgeschlagen!");
                Label contentText = new Label("Fehler beim Speichern der Datei \"" + fileName
                        + "\"!\n\nMöglicherweise ist die Datei schreibgeschützt oder in einer anderen Anwendung geöffnet.");

                CheckBox continueBox = new CheckBox("Mit verbleibenden Dateien fortfahren");
                continueBox.selectedProperty().bindBidirectional(continueOnFail);
                CheckBox showAlertBox = new CheckBox("Diesen Dialog bei Fehler erneut zeigen");
                showAlertBox.selectedProperty().bindBidirectional(showAlertOnNextFail);
                showAlertBox.disableProperty().bind(continueBox.selectedProperty().not());

                VBox content = new VBox(contentText);
                if (dataToSave.size() > 1) {
                    Pane pane = new Pane();
                    pane.setMinHeight(20);
                    pane.setPrefWidth(0);
                    content.getChildren().addAll(pane, continueBox, showAlertBox);
                }

                alert.getDialogPane().setContent(content);
                alert.showAndWait();
            }, null);

            Platform.runLater(alertTask);

            try {
                alertTask.get();
            } catch (InterruptedException | ExecutionException e) {
                if(isCancelled()){
                    return;
                }
                throw new PheruMediaRuntimeException("Exception waiting for FutureTask!", e);
            }
        }
    }

}
