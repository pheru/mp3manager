package de.pheru.media.desktop.ui.audiolibrary;

import de.pheru.fx.mvp.factories.DialogFactory;
import de.pheru.media.core.io.directory.DefaultDirectorySearcher;
import de.pheru.media.core.io.directory.DirectorySearcher;
import de.pheru.media.core.io.file.FileIO;
import de.pheru.media.desktop.cdi.qualifiers.AudioLibraryIO;
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
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.function.Consumer;

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
    private DialogFactory dialogFactory;
    @Inject
    @CurrentAudioLibrary
    private ObjectProperty<AudioLibrary> currentAudioLibrary;
    @Inject
    @AudioLibraryIO
    private FileIO audioLibraryIO;

    private final ObservableList<AudioLibrary> audioLibraries = FXCollections.observableArrayList();
    private final Comparator<AudioLibrary> audioLibraryComparator = Comparator.comparing(AudioLibrary::getName);
    private boolean edited = false;

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        initUI();
        loadAudioLibraries();
        selectCurrentAudioLibrary();
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
                showLibraryNameInfoAlert("Name bereits vorhanden!");
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
        audioLibrariesListView.setItems(audioLibraries);
    }

    private boolean audioLibraryNameAlreadyExists(final String name) {
        for (final AudioLibrary audioLibrary : audioLibraries) {
            if (audioLibrary.getName().toLowerCase().equals(name.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    private void showLibraryNameInfoAlert(final String headerText) {
        final Alert alert = dialogFactory.createAlert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(headerText);
        alert.setContentText("Bitte einen anderen Namen eingeben.");
        alert.show();
    }

    private void initBindingsAndListeners() {
        currentAudioLibraryLabel.textProperty().bind(new StringBinding() {
            { bind(audioLibrariesListView.getSelectionModel().selectedItemProperty()); }

            @Override
            protected String computeValue() {
                final AudioLibrary selected = audioLibrariesListView.getSelectionModel().getSelectedItem();
                if (selected == null) {
                    return "<nicht festgelegt>";
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
            if (newValue == null) {
                directoriesListView.getItems().clear();
            } else {
                directoriesListView.getItems().setAll(newValue.getDirectories());
                directoriesListView.scrollTo(0);
            }
        });
    }

    private void loadAudioLibraries() {
        LOGGER.info("Loading audio libraries ...");
        final DirectorySearcher directorySearcher = new DefaultDirectorySearcher();
        final List<File> audioLibraryFiles = directorySearcher.searchFiles(
                Collections.singletonList(AudioLibrary.FILE_ENDING),
                AudioLibrary.DIRECTORY);
        audioLibraries.clear();
        for (final File audioLibraryFile : audioLibraryFiles) {
            try {
                audioLibraries.add(audioLibraryIO.read(audioLibraryFile, AudioLibrary.class));
            } catch (final IOException e) {
                LOGGER.error("Exception loading audiolibrary-file " + audioLibraryFile.getAbsolutePath(), e);
                final Alert alert = dialogFactory.createAlert(Alert.AlertType.ERROR);
                alert.setHeaderText(null);
                alert.setContentText("Fehler beim Laden der Datei: " + audioLibraryFile.getAbsolutePath());
                alert.showAndWait();
            }
        }
        LOGGER.info(audioLibraries.size() + " audio libraries successfully loaded.");
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
        final TextInputDialog dialog = dialogFactory.createTextInputDialog(AudioLibrary.NEW_NAME);
        dialog.setTitle("Neue Musikbibliothek erstellen");
        dialog.setHeaderText(null);
        dialog.setContentText("Namen eingeben:");
        dialog.setOnCloseRequest(event -> {
            if (dialog.getResult() != null) {
                if (dialog.getResult().trim().isEmpty()) {
                    showLibraryNameInfoAlert("Name darf nicht leer sein!");
                    event.consume();
                } else if (audioLibraryNameAlreadyExists(dialog.getResult())) {
                    showLibraryNameInfoAlert("Name bereits vorhanden!");
                    event.consume();
                }
            }
        });
        return dialog.showAndWait();
    }

    @FXML
    private void deleteAudioLibrary() {
        final AudioLibrary selected = audioLibrariesListView.getSelectionModel().getSelectedItem();
        showAudioLibraryDeleteConfirmAlert(selected, buttonType -> {
            if (buttonType == ButtonType.OK) {
                LOGGER.info("Deleting audiolibrary " + selected.getName() + " - " + selected.getFileName() + " ...");
                final File selectedFile = new File(AudioLibrary.DIRECTORY + "/" + selected.getFileName());
                if (selectedFile.delete()) {
                    LOGGER.info("Deleting audiolibrary done.");
                    audioLibraries.remove(selected);
                    audioLibrariesListView.getSelectionModel().clearSelection();
                } else {
                    LOGGER.error("Could not delete file " + selectedFile.getAbsolutePath());
                    final Alert alert = dialogFactory.createAlert(Alert.AlertType.ERROR);
                    alert.setHeaderText(null);
                    alert.setContentText("Fehler beim Löschen der Musikbibliothek \"" + selected.getName() + "\"!");
                    alert.show();
                }
            }
        });
    }

    private void showAudioLibraryDeleteConfirmAlert(final AudioLibrary audioLibrary, final Consumer<ButtonType> consumer) {
        final Alert confirmAlert = dialogFactory.createAlert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setHeaderText("Musikbibliothek löschen");
        confirmAlert.setContentText("Sind Sie sicher, dass Sie \"" + audioLibrary.getName() + "\" löschen möchten?" +
                "\nDieser Vorgang kann nicht rückgängig gemacht werden!");
        confirmAlert.getDialogPane().setMinWidth(400);
        final Optional<ButtonType> result = confirmAlert.showAndWait();
        result.ifPresent(consumer);
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
            LOGGER.info("Audiolibraries have been edited. Saving changes ...");
            for (final AudioLibrary audioLibrary : audioLibraries) {
                LOGGER.info("Saving " + audioLibrary.getFileName() + " ...");
                try {
                    audioLibraryIO.write(new File(AudioLibrary.DIRECTORY + "/" + audioLibrary.getFileName()), AudioLibrary.class, audioLibrary);
                } catch (final IOException e) {
                    LOGGER.error("Exception saving audiolibrary " + audioLibrary.getFileName(), e);
                    final Alert alert = dialogFactory.createAlert(Alert.AlertType.ERROR);
                    alert.setHeaderText(null);
                    alert.setContentText("Fehler beim Speichern der Musikbibliothek: " + audioLibrary.getName());
                    alert.showAndWait();
                }

            }
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