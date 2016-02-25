package de.pheru.media.gui.taskimpl;

import de.pheru.media.data.Mp3FileData;
import de.pheru.media.exceptions.PheruMediaRuntimeException;
import de.pheru.media.task.SaveFilesTask;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * Created by Philipp on 08.01.2016.
 */
public class SaveFilesTaskImpl extends SaveFilesTask {

    private boolean firstFail = true;
    private BooleanProperty showAlertOnNextFail = new SimpleBooleanProperty(true);

    public SaveFilesTaskImpl(ObservableList<Mp3FileData> dataToSave, Mp3FileData changeData) {
        super(dataToSave, changeData);
    }

    @Override
    protected void handleRenameFailed() {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Speichern fehlgeschlagen!");
            alert.setContentText("Dateiname konnte nicht geändert werden!\n\n"
                    + "Möglicherweise enthält der Dateiname ungültige Zeichen oder eine Datei mit diesem Namen existiert bereits.");
            alert.showAndWait();
        });
    }

    @Override
    protected boolean handleSaveFailed(String fileName, boolean lastFileToSave) {
        if (firstFail || showAlertOnNextFail.get()) {
            firstFail = false;
            FutureTask<Boolean> alertTask = new FutureTask<>(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Speichern fehlgeschlagen!");
                Label contentText = new Label("Fehler beim Speichern der Datei \"" + fileName
                        + "\"!\n\nMöglicherweise ist die Datei schreibgeschützt oder in einer anderen Anwendung geöffnet.");
                CheckBox continueBox = new CheckBox("Mit verbleibenden Dateien fortfahren");
                CheckBox showAlertBox = new CheckBox("Diesen Dialog bei Fehler erneut zeigen");
                showAlertBox.selectedProperty().bindBidirectional(showAlertOnNextFail);
                showAlertBox.disableProperty().bind(continueBox.selectedProperty().not());

                VBox content = new VBox(contentText);
                if (!lastFileToSave) {
                    Pane pane = new Pane();
                    pane.setMinHeight(20);
                    pane.setPrefWidth(0);
                    content.getChildren().addAll(pane, continueBox, showAlertBox);
                }
                alert.getDialogPane().setContent(content);
                alert.showAndWait();
                return continueBox.isSelected();
            });
            Platform.runLater(alertTask);
            try {
                return alertTask.get();
            } catch (InterruptedException | ExecutionException e) {
                if (isCancelled()) {
                    return false;
                }
                throw new PheruMediaRuntimeException("Exception waiting for FutureTask!", e);
            }
        }
        return true;
    }

    @Override
    protected void handleReloadFailed(String fileName) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Neu laden der Datei " + fileName + " fehlgeschlagen!");
            alert.setContentText(fileName + " wurde korrekt gespeichert, konnte jedoch nicht neu geladen werden." +
                    "\nDie angezeigten Informationen sind gegebenenfalls nicht aktuell.");
            alert.showAndWait();
        });
    }
}
