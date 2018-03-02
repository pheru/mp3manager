package de.pheru.media.desktop.ui.application;

import de.pheru.fx.mvp.factories.StageFactory;
import de.pheru.fx.mvp.qualifiers.PrimaryStage;
import de.pheru.media.desktop.cdi.qualifiers.CurrentAudioLibrary;
import de.pheru.media.desktop.cdi.qualifiers.StartFinishedActions;
import de.pheru.media.desktop.data.AudioLibrary;
import de.pheru.media.desktop.ui.audiolibrary.AudioLibraryView;
import de.pheru.media.desktop.util.PrioritizedRunnable;
import javafx.beans.property.ObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.New;
import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.SortedSet;

public class ApplicationPresenter implements Initializable {

    @FXML
    private VBox rootBox;
    @FXML
    private SplitPane splitPane;

    private Label playlistPlatzhalter = new Label("Playlist");
    private Label musicPlayerPlatzhalter = new Label("Musicplayer");

    @Inject
    private StageFactory stageFactory;

    @Inject
    @New
    private Instance<AudioLibraryView> audioLibraryViewInstance;
    private Stage audioLibraryStage;
    @Inject
    @CurrentAudioLibrary
    private ObjectProperty<AudioLibrary> currentAudioLibrary;
    @Inject
    @PrimaryStage
    private Stage primaryStage;
    @Inject
    @StartFinishedActions
    private SortedSet<PrioritizedRunnable> startFinishedActions;

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        splitPane.getItems().add(0, playlistPlatzhalter);
        rootBox.getChildren().add(musicPlayerPlatzhalter);

        if (currentAudioLibrary.get() == null) {
            startFinishedActions.add(new PrioritizedRunnable(
                    this::openAudioLibraryDialog, PrioritizedRunnable.Priority.LOW));
        }
    }

    @FXML
    private void openAudioLibraryDialog() {
        if (audioLibraryStage == null) {
            audioLibraryStage = stageFactory.createStage(StageStyle.UNDECORATED);
            audioLibraryStage.initModality(Modality.APPLICATION_MODAL);
        }
        audioLibraryStage.setScene(new Scene(audioLibraryViewInstance.get().getView()));
        audioLibraryStage.show();
    }
}