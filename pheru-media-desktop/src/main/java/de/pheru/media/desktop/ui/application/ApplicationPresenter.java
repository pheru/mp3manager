package de.pheru.media.desktop.ui.application;

import de.pheru.fx.mvp.factories.StageFactory;
import de.pheru.fx.mvp.qualifiers.PrimaryStage;
import de.pheru.media.desktop.cdi.qualifiers.CurrentAudioLibrary;
import de.pheru.media.desktop.cdi.qualifiers.StartFinishedActions;
import de.pheru.media.desktop.data.AudioLibrary;
import de.pheru.media.desktop.ui.audiolibrary.AudioLibraryView;
import de.pheru.media.desktop.util.PrioritizedRunnable;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.New;
import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.SortedSet;

public class ApplicationPresenter implements Initializable {

    private static final Logger LOGGER = LogManager.getLogger(ApplicationPresenter.class);

    @FXML
    private VBox rootBox;
    @FXML
    private SplitPane splitPane;

    private final Label playlistPlatzhalter = new Label("Playlist");
    private final Label musicPlayerPlatzhalter = new Label("Musicplayer");

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
            audioLibraryStage = stageFactory.createStage(StageStyle.TRANSPARENT);
            audioLibraryStage.initModality(Modality.APPLICATION_MODAL);
        }
        final Parent audioLibraryView = audioLibraryViewInstance.get().getView();
        audioLibraryView.getStyleClass().add("dialog-shadow");
        audioLibraryStage.setScene(new Scene(audioLibraryView, Color.TRANSPARENT));
        audioLibraryStage.setOnHidden(event -> {
            if (currentAudioLibrary.get() == null) {
                LOGGER.info("No current audiolibrary selected. Exiting Application.");
                Platform.exit();
            } else {
                //TODO audiolibrarydata laden
            }
        });
        audioLibraryStage.show();
    }
}