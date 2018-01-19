package de.pheru.media.desktop.ui.application;

import de.pheru.fx.mvp.factories.StageFactory;
import de.pheru.media.desktop.ui.audiolibrary.AudioLibraryView;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

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
    private AudioLibraryView audioLibraryView;
    private Stage audioLibraryStage;

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        splitPane.getItems().add(0, playlistPlatzhalter);
        rootBox.getChildren().add(musicPlayerPlatzhalter);
    }

    @FXML
    private void openAudioLibraryDialog() {
        if (audioLibraryStage == null) {
            audioLibraryStage = stageFactory.createStage();
            audioLibraryStage.initModality(Modality.APPLICATION_MODAL);
            audioLibraryStage.setScene(new Scene(audioLibraryView.getView()));
        }
        //TODO Load
        audioLibraryStage.show();
    }
}