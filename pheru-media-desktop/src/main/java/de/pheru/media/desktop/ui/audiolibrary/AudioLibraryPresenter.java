package de.pheru.media.desktop.ui.audiolibrary;

import de.pheru.media.core.io.directory.DefaultDirectorySearcher;
import de.pheru.media.core.io.directory.DirectorySearcher;
import de.pheru.media.desktop.cdi.qualifiers.CurrentAudioLibrary;
import de.pheru.media.desktop.data.AudioLibrary;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
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
    private VBox root;
    @FXML
    private Label currentAudioLibraryLabel;
    @FXML
    private ListView<AudioLibrary> audioLibrariesListView;
    @FXML
    private Button removeDirectoryButton;
    @FXML
    private ListView<String> directoriesListView;
    @FXML
    private Button deleteAudioLibraryButton;
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
        selectCurrentAudioLibrary();
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

    private void showLibraryNameErrorAlert(final String headerText) {
        final Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(headerText);
        alert.setContentText("Bitte einen anderen Namen eingeben.");
        alert.show();
    }

    private void initBindingsAndListeners() {
        currentAudioLibraryLabel.textProperty().bind(new StringBinding() {
            {
                bind(audioLibrariesListView.getSelectionModel().selectedItemProperty());
            }

            @Override
            protected String computeValue() {
                final AudioLibrary selected = audioLibrariesListView.getSelectionModel().getSelectedItem();
                if (selected == null) {
                    return "---";
                }
                return selected.getName();
            }
        });
        deleteAudioLibraryButton.disableProperty().bind(
                audioLibrariesListView.getSelectionModel().selectedItemProperty().isNull());
        removeDirectoryButton.disableProperty().bind(
                directoriesListView.getSelectionModel().selectedItemProperty().isNull());
        confirmButton.disableProperty().bind(
                audioLibrariesListView.getSelectionModel().selectedItemProperty().isNull());
        audioLibrariesListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                directoriesListView.getItems().setAll(newValue.getDirectories());
                directoriesListView.scrollTo(0);
            }
        });
    }

    private void loadAudioLibraries() {
        final DirectorySearcher directorySearcher = new DefaultDirectorySearcher();
        final List<File> audioLibraries = directorySearcher.searchFiles(Collections.singletonList(AudioLibrary.FILE_ENDING),
                AudioLibrary.DIRECTORY);
        //TODO fileio audiolibraries laden
    }

    private void selectCurrentAudioLibrary() {
        if (currentAudioLibrary.get() != null) {
            for (final AudioLibrary item : audioLibrariesListView.getItems()) {
                if (item.getName().equals(currentAudioLibrary.get().getName())) {
                    audioLibrariesListView.getSelectionModel().select(item);
                    audioLibrariesListView.scrollTo(item);
                }
            }
        }
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

    @FXML
    private void deleteAudioLibrary() {
        final AudioLibrary selected = audioLibrariesListView.getSelectionModel().getSelectedItem();
        LOGGER.info("Deleting audiolibrary " + selected.getName() + " - " + selected.getFileName() + " ...");
        //TODO audiolibrary direkt loeschen
        LOGGER.info("Deleting audiolibrary done.");
    }

    @FXML
    private void addDirectory() {
        final DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Verzeichnis zu Musikbibliothek hinzufügen");
        final File directory = directoryChooser.showDialog(root.getScene().getWindow());
        final AudioLibrary selectedLibrary = audioLibrariesListView.getSelectionModel().getSelectedItem();
        selectedLibrary.getDirectories().add(directory.getAbsolutePath());
        directoriesListView.getItems().setAll(selectedLibrary.getDirectories());
        edited = true;
    }

    @FXML
    private void removeDirectory() {
        final AudioLibrary selectedLibrary = audioLibrariesListView.getSelectionModel().getSelectedItem();
        final String selectedDirectory = directoriesListView.getSelectionModel().getSelectedItem();
        selectedLibrary.getDirectories().remove(selectedDirectory);
        directoriesListView.getItems().setAll(selectedLibrary.getDirectories());
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
        hide();
    }

    private void hide() {
        root.getScene().getWindow().hide();
    }
}