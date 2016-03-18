package de.pheru.media.gui.applicationwindow.playlist;

import de.pheru.fx.controls.notification.Notification;
import de.pheru.fx.mvp.PrimaryStage;
import de.pheru.media.cdi.qualifiers.TableData;
import de.pheru.media.cdi.qualifiers.XMLSettings;
import de.pheru.media.data.Mp3FileData;
import de.pheru.media.data.Playlist;
import de.pheru.media.gui.taskimpl.LoadPlaylistTaskImpl;
import de.pheru.media.gui.util.CssRowFactory;
import de.pheru.media.gui.util.DragAndDropRowFactory;
import de.pheru.media.gui.player.MusicPlayer;
import de.pheru.media.settings.Settings;
import de.pheru.media.task.PheruMediaTask;
import de.pheru.media.task.TaskPool;
import de.pheru.media.util.TimeUtil;
import javafx.application.Application.Parameters;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@ApplicationScoped
public class PlaylistPresenter implements Initializable {

    private static final Logger LOGGER = LogManager.getLogger(PlaylistPresenter.class);

    @FXML
    private TableView<Mp3FileData> table;
    @FXML
    private Label playlistNameLabel;
    @FXML
    private Label totalDurationLabel;
    @FXML
    private Label titlesSize;
    @FXML
    private MenuItem deleteMenuItem;
    @FXML
    private Menu saveMenu;
    @FXML
    private MenuItem saveMenuItem;

    private CssRowFactory<Mp3FileData> tableRowFactory;

    @Inject
    @XMLSettings
    private Settings settings;
    @Inject
    private Playlist playlist;
    @Inject
    private TaskPool taskPool;
    @Inject
    @TableData(TableData.Source.MAIN)
    private ObservableList<Mp3FileData> mainTitles;
    @Inject
    private MusicPlayer musicPlayer;

    @Inject
    private Parameters params;
    @Inject
    @PrimaryStage
    private Stage primaryStage;

    private Notification currentTitleNotification;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initTable();
        bindUI();
        playlist.currentTitleIndexProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            updateStyledIndex(newValue.intValue());
            Mp3FileData newCurrentTitle = playlist.getCurrentTitle();
            if (!primaryStage.isFocused() && newCurrentTitle != null) {
                showCurrentTitleNotification(newCurrentTitle);
            }
        });
        if (!params.getRaw().isEmpty()) {
            PheruMediaTask loadPlaylistTask = new LoadPlaylistTaskImpl(playlist, new File(params.getRaw().get(0)), mainTitles);
            loadPlaylistTask.runningProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                if (oldValue && !newValue) {
                    musicPlayer.playPause();
                }
            });
            taskPool.addTask(loadPlaylistTask);
        }
    }

    /**
     * Initialisiert die Tabelle.
     */
    private void initTable() {
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        DragAndDropRowFactory<Mp3FileData> dndRowFactory = new DragAndDropRowFactory<>(table, Mp3FileData.EMPTY_DATA, (TableView<Mp3FileData> param) -> {
            TableRow<Mp3FileData> row = new TableRow<>();
            row.setOnMouseClicked((MouseEvent event) -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    play();
                }
            });
            return row;
        });
        dndRowFactory.setOnDropCompleted((DragAndDropRowFactory.DropCompletedEvent event) -> {
            Integer newCurrentIndex = playlist.getCurrentTitleIndex();
            for (Pair<Integer, Integer> p : event.getMovedIndices()) {
                if (p.getKey().equals(playlist.getCurrentTitleIndex())) {
                    newCurrentIndex = p.getValue();
                    break;
                } else if (p.getKey() < playlist.getCurrentTitleIndex() && event.getTargetIndex() > playlist.getCurrentTitleIndex()) {
                    newCurrentIndex--;
                } else if (p.getKey() > playlist.getCurrentTitleIndex() && event.getTargetIndex() <= playlist.getCurrentTitleIndex()) {
                    newCurrentIndex++;
                }
            }
            playlist.setCurrentTitleIndex(newCurrentIndex);
            playlist.updateDirtyFlag();
        });

        tableRowFactory = new CssRowFactory<>("played", dndRowFactory);
        table.setRowFactory(tableRowFactory);
        table.setItems(playlist.getTitles());
    }

    /**
     * Bindet die UI-Elemente untereinander.
     */
    private void bindUI() {
        playlistNameLabel.textProperty().bind(new StringBinding() {
            {
                bind(playlist.fileNameProperty(), playlist.dirtyProperty());
            }

            @Override
            protected String computeValue() {
                if (playlist.getFileName().isEmpty()) {
                    return "<Neue Wiedergabeliste>";
                } else {
                    String fileName = playlist.getFileName().replace("." + Playlist.FILE_EXTENSION, "");
                    if (playlist.isDirty()) {
                        fileName += "*";
                    }
                    return fileName;
                }
            }
        });
        titlesSize.textProperty().bind(new StringBinding() {
            {
                bind(playlist.getTitles());
            }

            @Override
            protected String computeValue() {
                if (playlist.getTitles().contains(Mp3FileData.EMPTY_DATA)) {
                    return String.valueOf(playlist.getTitles().size() - 1);
                }
                return String.valueOf(playlist.getTitles().size());
            }
        });
        totalDurationLabel.textProperty().bind(new StringBinding() {
            {
                bind(playlist.getTitles());
            }

            @Override
            protected String computeValue() {
                Double duration = 0.0;
                for (Mp3FileData mp3FileData : playlist.getTitles()) {
                    duration += mp3FileData.getDuration();
                }
                return TimeUtil.secondsToDurationFormat(duration, true);
            }
        });
        deleteMenuItem.disableProperty().bind(playlist.fileNameProperty().isEmpty());
        saveMenuItem.disableProperty().bind(playlist.dirtyProperty().not());
        saveMenu.disableProperty().bind(Bindings.size(playlist.getTitles()).isEqualTo(0));
    }

    @FXML
    private void savePlaylist() {
        try {
            if (playlist.save()) {
                playlist.setDirty(false);
            }
        } catch (IOException e) {
            LOGGER.error("Exception saving playlist!", e);
            Alert alert = new Alert(Alert.AlertType.ERROR, "Fehler beim Speichern der Wiedergabeliste!");
            alert.showAndWait();
        }
    }

    @FXML
    private void savePlaylistAs() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Wiedergabeliste speichern");
        if (!settings.getPlaylistsDirectory().isEmpty()) {
            fileChooser.setInitialDirectory(new File(settings.getPlaylistsDirectory()));
        }

        String fileName = "Wiedergabeliste";
        int i = 2;
        while (new File(settings.getPlaylistsDirectory() + "\\" + fileName + "." + Playlist.FILE_EXTENSION).exists()) {
            fileName = "Wiedergabeliste (" + i + ")";
            i++;
        }
        fileChooser.setInitialFileName(fileName + "." + Playlist.FILE_EXTENSION);

        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Wiedergabelisten", "*." + Playlist.FILE_EXTENSION));
        File playlistFile = fileChooser.showSaveDialog(table.getScene().getWindow());
        if (playlistFile != null) {
            try {
                if (playlist.save(playlistFile)) {
                    playlist.setFilePath(playlistFile.getParent());
                    playlist.setFileName(playlistFile.getName());
                    playlist.setDirty(false);
                    settings.setPlaylistsDirectory(playlistFile.getParent());
                }
            } catch (IOException e) {
                LOGGER.error("Exception saving playlist!", e);
                Alert alert = new Alert(Alert.AlertType.ERROR, "Fehler beim Speichern der Wiedergabeliste!");
                alert.showAndWait();
            }
        }
    }

    @FXML
    private void loadPlaylist() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Wiedergabeliste laden");
        if (!settings.getPlaylistsDirectory().isEmpty()) {
            fileChooser.setInitialDirectory(new File(settings.getPlaylistsDirectory()));
        }
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Wiedergabelisten", "*." + Playlist.FILE_EXTENSION));
        File playlistFile = fileChooser.showOpenDialog(table.getScene().getWindow());
        if (playlistFile != null) {
            PheruMediaTask loadPlaylistTask = new LoadPlaylistTaskImpl(playlist, playlistFile, mainTitles);
            loadPlaylistTask.runningProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                if (oldValue && !newValue) {
                    musicPlayer.stop();
                    settings.setPlaylistsDirectory(playlistFile.getParent());
                }
            });
            taskPool.addTask(loadPlaylistTask);
        }
    }

    @FXML
    private void deletePlaylist() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        CheckBox clearPlaylistBox = new CheckBox("Titel aus aktueller Wiedergabe entfernen");
        alert.getDialogPane().setContent(clearPlaylistBox);
        alert.setHeaderText("Wiedergabeliste löschen?");
        ButtonType result = alert.showAndWait().get();
        if (result == ButtonType.CANCEL) {
            return;
        }
        if (new File(playlist.getAbsolutePath()).delete()) {
            playlist.setFilePath("");
            playlist.setFileName("");
            if (clearPlaylistBox.isSelected()) {
                table.getSelectionModel().clearSelection();
                playlist.clear();
            }
        }
    }

    @FXML
    private void play() {
        ObservableList<Mp3FileData> selectedItems = table.getSelectionModel().getSelectedItems();
        if (table.getSelectionModel().getSelectedItems().size() == 1) {
            musicPlayer.play(table.getSelectionModel().getSelectedIndices().get(0));
        } else if (selectedItems.size() > 1) {
            ObservableList<Mp3FileData> selectedItemsCopy = FXCollections.observableArrayList(selectedItems);
            playlist.clear();
            playlist.add(selectedItemsCopy);
            musicPlayer.play(0);
        }
    }

    @FXML
    private void remove() {
        if (table.getSelectionModel().getSelectedIndices().contains(playlist.getCurrentTitleIndex())) {
            musicPlayer.stop();
        }
        playlist.remove(table.getSelectionModel().getSelectedIndices());
    }

    @FXML
    private void tableKeyReleased(KeyEvent event) {
        switch (event.getCode()) {
            case ENTER:
                play();
                break;
            case DELETE:
                remove();
                break;
        }
    }

    private void showCurrentTitleNotification(Mp3FileData newCurrentTitle) {
        if (currentTitleNotification != null) {
            currentTitleNotification.hide(false);
        }
        VBox vbox = new VBox(new Label(newCurrentTitle.getTitle()),
                new Label(newCurrentTitle.getAlbum()), new Label(newCurrentTitle.getArtist()));
        vbox.setAlignment(Pos.CENTER_LEFT);

        Image image = new Image(new ByteArrayInputStream(newCurrentTitle.getArtworkData().getBinaryData()));
        ImageView artworkImage = new ImageView(image);
        artworkImage.setFitHeight(75);
        artworkImage.setFitWidth(75);

        HBox content = new HBox(artworkImage, vbox);
        content.setSpacing(5);

        currentTitleNotification = new Notification(content);
        currentTitleNotification.setOnMouseClicked((MouseEvent event) -> {
            primaryStage.setIconified(false);
            primaryStage.toFront();
            currentTitleNotification.hide(false);
        });
        currentTitleNotification.show();
    }

    private void updateStyledIndex(int playlistIndex) {
        tableRowFactory.getStyledIndices().clear();
        if (playlistIndex >= 0) {
            tableRowFactory.getStyledIndices().add(playlistIndex);
        }
    }
}
