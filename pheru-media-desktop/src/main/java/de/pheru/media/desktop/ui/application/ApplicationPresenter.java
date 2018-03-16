package de.pheru.media.desktop.ui.application;

import de.pheru.fx.mvp.factories.StageFactory;
import de.pheru.fx.mvp.qualifiers.PrimaryStage;
import de.pheru.fx.util.properties.ObservableProperties;
import de.pheru.media.core.data.loader.Mp3FileLoader;
import de.pheru.media.desktop.cdi.qualifiers.CurrentAudioLibrary;
import de.pheru.media.desktop.cdi.qualifiers.Settings;
import de.pheru.media.desktop.cdi.qualifiers.StartFinishedActions;
import de.pheru.media.desktop.data.AudioLibrary;
import de.pheru.media.desktop.tasks.DirectoryReaderTask;
import de.pheru.media.desktop.ui.audiolibrary.AudioLibraryView;
import de.pheru.media.desktop.util.PrioritizedRunnable;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
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
    private Label currentAudioLibraryLabel;
    @FXML
    private SplitPane splitPane;

    private final Label playlistPlatzhalter = new Label("Playlist");
    private final Label musicPlayerPlatzhalter = new Label("Musicplayer");

    private Stage audioLibraryStage;

    @Inject
    @Settings
    private ObservableProperties settings;
    @Inject
    private StageFactory stageFactory;
    @Inject
    @New
    private Instance<AudioLibraryView> audioLibraryViewInstance;
    @Inject
    @CurrentAudioLibrary
    private ObjectProperty<AudioLibrary> currentAudioLibrary;
    @Inject
    @PrimaryStage
    private Stage primaryStage;
    @Inject
    @StartFinishedActions
    private SortedSet<PrioritizedRunnable> startFinishedActions;

    @Inject
    @New
    private Instance<DirectoryReaderTask> directoryReaderTaskInstance;

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        splitPane.getItems().add(0, playlistPlatzhalter);
        rootBox.getChildren().add(musicPlayerPlatzhalter);
        currentAudioLibraryLabel.textProperty().bind(new StringBinding() {
            {bind(currentAudioLibrary);}

            @Override
            protected String computeValue() {
                if (currentAudioLibrary.get() == null) {
                    return "<kein Musikbibliothek festgelegt>";
                }
                return currentAudioLibrary.get().getName();
            }
        });
        //TODO Testlauf //TODO WIP
        currentAudioLibrary.addListener(new ChangeListener<AudioLibrary>() {
            @Override
            public void changed(final ObservableValue<? extends AudioLibrary> observable, final AudioLibrary oldValue, final AudioLibrary newValue) {
                updateAudioLibraryData();
            }
        });

        if (currentAudioLibrary.get() == null) {
            startFinishedActions.add(new PrioritizedRunnable(
                    this::openAudioLibraryDialog, PrioritizedRunnable.Priority.LOW));
        }
    }

    //TODO WIP
    private void updateAudioLibraryData(){
        final DirectoryReaderTask directoryReaderTask = directoryReaderTaskInstance.get();
        final Thread thread = new Thread(directoryReaderTask);
        thread.setDaemon(true);
        thread.start();
        directoryReaderTask.setOnSucceeded(event -> {
            final DirectoryReaderTask.Result result = directoryReaderTask.getValue();
            System.out.println();
        });
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
        audioLibraryStage.show();
    }
}