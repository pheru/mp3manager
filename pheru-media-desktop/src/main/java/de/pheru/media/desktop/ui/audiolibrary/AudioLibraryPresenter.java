package de.pheru.media.desktop.ui.audiolibrary;

import de.pheru.media.core.io.directory.DefaultDirectorySearcher;
import de.pheru.media.core.io.directory.DirectorySearcher;
import de.pheru.media.desktop.cdi.qualifiers.CurrentAudioLibrary;
import de.pheru.media.desktop.data.AudioLibrary;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.util.StringConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import java.io.File;
import java.net.URL;
import java.util.*;

public class AudioLibraryPresenter implements Initializable {

    private static final Logger LOGGER = LogManager.getLogger(AudioLibraryPresenter.class);

    @FXML
    private ListView<AudioLibrary> audioLibrariesListView;
    @FXML
    private ListView<String> directoriesListView;
    @FXML
    private Button confirmButton;

    @Inject
    @CurrentAudioLibrary
    private ObjectProperty<AudioLibrary> currentAudioLibrary;

    private final ObservableList<AudioLibrary> audioLibraries = FXCollections.observableArrayList();
    private final Comparator<AudioLibrary> audioLibraryComparator = Comparator.comparing(AudioLibrary::getName);
    private boolean edited = false;

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        initUI();
        loadAudioLibraries();
        testdata(); //TODO testdaten entfernen
        if (currentAudioLibrary.get() != null) {
            for (AudioLibrary item : audioLibrariesListView.getItems()) {
                if (item.getName().equals(currentAudioLibrary.get().getName())) {
                    audioLibrariesListView.getSelectionModel().select(item);
                    audioLibrariesListView.scrollTo(item);
                }
            }
        }
    }

    private void testdata() {
        for (int i = 0; i < 13; i++) {
            final AudioLibrary a = new AudioLibrary();
            a.setName("Test" + i);
            audioLibraries.add(a);
            for (int j = 0; j < 13; j++) {
                a.getDirectories().add(a.getName() + "-" + j);
            }
        }
        audioLibrariesListView.setItems(audioLibraries.sorted(audioLibraryComparator));
    }

    private void loadAudioLibraries() {
        final DirectorySearcher directorySearcher = new DefaultDirectorySearcher();
        final List<File> audioLibraries = directorySearcher.searchFiles(Collections.singletonList(AudioLibrary.FILE_ENDING),
                AudioLibrary.DIRECTORY);
        //TODO fileio audiolibraries laden
    }

    private void initUI() {
        initAudioLibrariesListView();
        initBindingsAndListeners();
    }

    private void initAudioLibrariesListView() {
        audioLibrariesListView.setEditable(true);
        audioLibrariesListView.setOnEditCommit(event -> {
            final String newName = event.getNewValue().getName().trim();
            if (newName.isEmpty()) {
                audioLibrariesListView.refresh();
                event.consume();
            } else if (audioLibraryNameAlreadyExists(newName)) {
                showLibraryNameErrorAlert("Name bereits vorhanden!");
                event.consume();
            } else {
                final AudioLibrary editedLibrary = audioLibrariesListView.getItems().get(event.getIndex());
                editedLibrary.setName(newName);
                audioLibrariesListView.setItems(audioLibraries.sorted(audioLibraryComparator));
                edited = true;
                audioLibrariesListView.scrollTo(audioLibrariesListView.getSelectionModel().getSelectedIndex());
            }
        });
        audioLibrariesListView.setCellFactory(TextFieldListCell.forListView(new StringConverter<AudioLibrary>() {
            @Override
            public String toString(final AudioLibrary object) {
                return object.getName();
            }

            @Override
            public AudioLibrary fromString(final String string) {
                final AudioLibrary edited = new AudioLibrary();
                edited.setName(string);
                return edited;
            }
        }));
    }

    private boolean audioLibraryNameAlreadyExists(final String name) {
        for (final AudioLibrary audioLibrary : audioLibraries) {
            if (audioLibrary.getName().toLowerCase().equals(name.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    private void initBindingsAndListeners() {
        confirmButton.disableProperty().bind(
                audioLibrariesListView.getSelectionModel().selectedItemProperty().isNull());
        audioLibrariesListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                directoriesListView.getItems().setAll(newValue.getDirectories());
                directoriesListView.scrollTo(0);
            }
        });
    }

    @FXML
    private void createAudioLibrary() {
        final Optional<String> result = showLibraryNameInputDialog();
        result.ifPresent(input -> {
            final AudioLibrary newLibrary = new AudioLibrary();
            newLibrary.setName(input.trim());
            audioLibraries.add(newLibrary);
            audioLibrariesListView.scrollTo(newLibrary);
            audioLibrariesListView.getSelectionModel().select(newLibrary);
            edited = true;
        });
    }

    private Optional<String> showLibraryNameInputDialog() {
        final TextInputDialog dialog = new TextInputDialog(AudioLibrary.NEW_NAME);
        dialog.setTitle("Neue Musikbibliothek erstellen");
        dialog.setHeaderText(null);
        dialog.setContentText("Namen eingeben:");
        dialog.setOnCloseRequest(event -> {
            if (dialog.getResult() != null) {
                if (dialog.getResult().trim().isEmpty()) {
                    showLibraryNameErrorAlert("Name darf nicht leer sein!");
                    event.consume();
                } else if (audioLibraryNameAlreadyExists(dialog.getResult())) {
                    showLibraryNameErrorAlert("Name bereits vorhanden!");
                    event.consume();
                }
            }
        });
        return dialog.showAndWait();
    }

    private void showLibraryNameErrorAlert(final String headerText) {
        final Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(headerText);
        alert.setContentText("Bitte einen anderen Namen eingeben.");
        alert.show();
    }

    @FXML
    private void deleteAudioLibrary() {
        final AudioLibrary selected = audioLibrariesListView.getSelectionModel().getSelectedItem();
        LOGGER.info("Deleting audiolibrary " + selected.getName() + " - " + selected.getFileName() + " ...");
        //TODO audiolibrary direkt loeschen
        LOGGER.info("Deleting audiolibrary done.");
    }

    @FXML
    private void addDirectory() {
        edited = true;
    }

    @FXML
    private void removeDirectory() {
        edited = true;
    }

    @FXML
    private void confirm() {
        if (edited) {
            LOGGER.info("Audiolibraries have been edited. Saving changes...");
            //TODO audiolibraries speichern nach edit
            LOGGER.info("Saving audiolibraries done.");
        }
        hide();
        currentAudioLibrary.set(audioLibrariesListView.getSelectionModel().getSelectedItem());
    }

    @FXML
    private void cancel() {
        if (currentAudioLibrary.get() == null) {
            LOGGER.info("No current audiolibrary selected. Exiting Application.");
            Platform.exit();
        } else {
            hide();
        }
    }

    private void hide() {
        audioLibrariesListView.getScene().getWindow().hide();
    }
}