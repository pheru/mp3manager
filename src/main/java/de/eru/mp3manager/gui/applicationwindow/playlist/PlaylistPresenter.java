package de.eru.mp3manager.gui.applicationwindow.playlist;

import de.eru.mp3manager.cdi.CurrentTitleEvent;
import de.eru.mp3manager.cdi.TableData;
import de.eru.mp3manager.cdi.TableDataSource;
import de.eru.mp3manager.cdi.Updated;
import de.eru.mp3manager.cdi.XMLSettings;
import de.eru.mp3manager.data.Mp3FileData;
import de.eru.mp3manager.data.Playlist;
import de.eru.mp3manager.gui.utils.CssRowFactory;
import de.eru.mp3manager.gui.utils.DragAndDropRowFactory;
import de.eru.mp3manager.player.MusicPlayer;
import de.eru.mp3manager.service.FileService;
import de.eru.mp3manager.settings.Settings;
import de.eru.mp3manager.utils.task.TaskPool;
import de.eru.mp3manager.utils.formatter.TimeFormatter;
import de.eru.mp3manager.utils.task.LoadPlaylistTask;
import de.eru.mp3manager.utils.task.Mp3ManagerTask;
import de.eru.pherufx.mvp.InjectableList;
import de.eru.pherufx.notifications.Notification;
import de.eru.pherufx.notifications.Notifications;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Application.Parameters;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.util.Pair;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
    @TableData(source = TableDataSource.PLAYLIST_SELECTED)
    private InjectableList<Mp3FileData> selectedTitles;
    @Inject
    @TableData(source = TableDataSource.MAIN_ALL)
    private InjectableList<Mp3FileData> mainTitles;
    @Inject
    private MusicPlayer musicPlayer;

    @Inject
    private Parameters params;

    private Notification currentTitleNotification;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initTable();
        bindUI();
        if (!params.getRaw().isEmpty()) {
            Mp3ManagerTask loadPlaylistTask = new LoadPlaylistTask(playlist, new File(params.getRaw().get(0)), mainTitles);
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
        DragAndDropRowFactory<Mp3FileData> dndRowFactory = new DragAndDropRowFactory<>(table, Mp3FileData.EMPTY_PLAYLIST_DATA, (TableView<Mp3FileData> param) -> {
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
            playlist.setDirtyByCheck();
        });

        tableRowFactory = new CssRowFactory<>("played", dndRowFactory);
        table.setRowFactory(tableRowFactory);
        table.setItems(playlist.getTitles());
        selectedTitles.set(table.getSelectionModel().getSelectedItems());
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
                if (playlist.getTitles().contains(Mp3FileData.EMPTY_PLAYLIST_DATA)) {
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
                return TimeFormatter.secondsToDurationFormat(duration, true);
            }
        });
        deleteMenuItem.disableProperty().bind(playlist.fileNameProperty().isEmpty());
        saveMenuItem.disableProperty().bind(playlist.dirtyProperty().not());
        saveMenu.disableProperty().bind(Bindings.size(playlist.getTitles()).isEqualTo(0));
    }

    @FXML
    private void savePlaylist() {
        //TODO Statusbar-Meldung
        try {
            FileService.savePlaylist(new File(playlist.getAbsolutePath()), playlist.getTitles());
            playlist.setDirty(false);
        } catch (IOException e) {
            LOGGER.error("Exception saving playlist!", e);
            Alert alert = new Alert(Alert.AlertType.ERROR, "Fehler beim Speichern der Wiedergabeliste!");
            alert.showAndWait();
        }
    }

    @FXML
    private void savePlaylistAs() {
        //TODO Statusbar-Meldung
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Wiedergabeliste speichern");
        if (!settings.getPlaylistFilePath().isEmpty()) {
            fileChooser.setInitialDirectory(new File(settings.getPlaylistFilePath()));
        }

        String fileName = "Wiedergabeliste";
        int i = 2;
        while (new File(settings.getPlaylistFilePath() + "\\" + fileName + "." + Playlist.FILE_EXTENSION).exists()) {
            fileName = "Wiedergabeliste (" + i + ")";
            i++;
        }
        fileChooser.setInitialFileName(fileName + "." + Playlist.FILE_EXTENSION);

        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Wiedergabelisten", "*." + Playlist.FILE_EXTENSION));
        File playlistFile = fileChooser.showSaveDialog(table.getScene().getWindow());
        if (playlistFile != null) {
            try {
                if (FileService.savePlaylist(playlistFile, playlist.getTitles())) {
                    playlist.setFilePath(playlistFile.getParent());
                    playlist.setFileName(playlistFile.getName());
                    playlist.setDirty(false);
                    settings.setPlaylistFilePath(playlistFile.getParent());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void loadPlaylist() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Wiedergabeliste laden");
        if (!settings.getPlaylistFilePath().isEmpty()) {
            fileChooser.setInitialDirectory(new File(settings.getPlaylistFilePath()));
        }
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Wiedergabelisten", "*." + Playlist.FILE_EXTENSION));
        File playlistFile = fileChooser.showOpenDialog(table.getScene().getWindow());
        if (playlistFile != null) {
            Mp3ManagerTask loadPlaylistTask = new LoadPlaylistTask(playlist, playlistFile, mainTitles);
            loadPlaylistTask.runningProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                if (oldValue && !newValue) {
                    musicPlayer.stop();
                }
            });
            taskPool.addTask(loadPlaylistTask);
            settings.setPlaylistFilePath(playlistFile.getParent());
        }
    }

    @FXML
    private void deletePlaylist() {
        //TODO Statusbar-Meldung
        //TODO Bestätigungsdialog mit Option die Titel aus der aktuellen Wiedergabe zu entfernen
        if (FileService.deleteFile(playlist.getAbsolutePath())) {
            playlist.setFilePath("");
            playlist.setFileName("");
        }
    }

    @FXML
    private void play() {
        if (!selectedTitles.isEmpty()) {
            musicPlayer.play(table.getSelectionModel().getSelectedIndices().get(0)); // TODO Bei Mehrfachauswahl die nicht selektierten entfernen?
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

    private void currentTitleUpdated(@Observes @Updated CurrentTitleEvent event) {
        updateStyledIndex(event.getNewCurrentTitleIndex());
        Mp3FileData newCurrentTitle = event.getNewCurrentTitle();
        if (currentTitleNotification != null) {
            currentTitleNotification.hide();
        }
        currentTitleNotification = Notifications.createNotification(Notification.Type.INFO)
                .setHeader("Aktueller Titel")
                .setText(newCurrentTitle.getTitle() + "\n" + newCurrentTitle.getAlbum() + "\n" + newCurrentTitle.getArtist());
        currentTitleNotification.show();
    }

    private void updateStyledIndex(int playlistIndex) {
        tableRowFactory.getStyledIndices().clear();
        if (playlistIndex >= 0) {
            tableRowFactory.getStyledIndices().add(playlistIndex);
        }
    }
}
