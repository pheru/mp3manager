package de.eru.mp3manager.gui.applicationwindow.playlist;

import de.eru.mp3manager.cdi.CurrentTitleEvent;
import de.eru.mp3manager.cdi.SelectedTableData;
import de.eru.mp3manager.cdi.TableData;
import de.eru.mp3manager.cdi.TableDataSource;
import de.eru.mp3manager.cdi.Updated;
import de.eru.mp3manager.data.Mp3FileData;
import de.eru.mp3manager.data.Playlist;
import de.eru.mp3manager.gui.utils.CssRowFactory;
import de.eru.mp3manager.gui.utils.DragAndDropRowFactory;
import de.eru.mp3manager.service.FileService;
import de.eru.mp3manager.utils.TaskPool;
import de.eru.mp3manager.utils.factories.TaskFactory;
import de.eru.mp3manager.utils.formatter.TimeFormatter;
import de.eru.pherufx.mvp.InjectableList;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

@ApplicationScoped
public class PlaylistPresenter implements Initializable {

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
    private Playlist playlist;
    @Inject
    private TaskPool taskPool;
    @Inject
    @SelectedTableData(source = TableDataSource.PLAYLIST)
    private InjectableList<Mp3FileData> selectedTitles;
    @Inject
    @TableData(source = TableDataSource.MAIN)
    private InjectableList<Mp3FileData> mainTitles;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initTable();
        bindUI();
    }

    /**
     * Initialisiert die Tabelle.
     */
    private void initTable() {
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        DragAndDropRowFactory dndRowFactory = new DragAndDropRowFactory(table);
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
                bind(playlist.absolutePathProperty(), playlist.dirtyProperty());
            }

            @Override
            protected String computeValue() {
                if (playlist.getFileName().isEmpty()) {
                    return "<Neue Wiedergabeliste>";
                } else {
                    String fileName = playlist.getFileName().replace(Playlist.FILE_EXTENSION, "");
                    if (playlist.isDirty()) {
                        fileName += "*";
                    }
                    return fileName;
                }
            }
        });
        titlesSize.textProperty().bind(Bindings.size(playlist.getTitles()).asString());
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
        System.out.println("TODO!");//TODO implementieren
    }
    
    @FXML
    private void savePlaylistAs() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Wiedergabeliste speichern");
//        fileChooser.setInitialDirectory(new File("D:\\"));
        fileChooser.setInitialFileName("Wiedergabeliste" + Playlist.FILE_EXTENSION);
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Wiedergabelisten", "*" + Playlist.FILE_EXTENSION));
        File playlistFile = fileChooser.showSaveDialog(table.getScene().getWindow());
        if (playlistFile != null) {
            try {
                FileService.savePlaylist(playlistFile, playlist.getTitles());
                playlist.setFilePath(playlistFile.getParent());
                playlist.setFileName(playlistFile.getName());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void loadPlaylist() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Wiedergabeliste laden");
//        fileChooser.setInitialDirectory(new File("D:\\"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Wiedergabelisten", "*" + Playlist.FILE_EXTENSION));
        File playlistFile = fileChooser.showOpenDialog(table.getScene().getWindow());
        if (playlistFile != null) {
            taskPool.addTask(TaskFactory.createLoadPlaylistTask(playlist, playlistFile, mainTitles));
        }
    }

    @FXML
    private void deletePlaylist() {
        boolean deletePlaylist = FileService.deleteFile(playlist.getAbsolutePath());
    }

    @FXML
    private void play() {
        //TODO implementieren
    }

    @FXML
    private void remove() {
        playlist.remove(table.getSelectionModel().getSelectedIndices());
    }

    @FXML
    private void tableKeyReleased(KeyEvent event) {
        switch (event.getCode()) {
            case DELETE:
                remove();
                break;
        }
    }

    private void currentTitleUpdated(@Observes @Updated CurrentTitleEvent event) {
        updateStyledIndex(event.getNewCurrentTitleIndex());
    }

    private void updateStyledIndex(int playlistIndex) {
        tableRowFactory.getStyledIndices().clear();
        if (playlistIndex >= 0) {
            tableRowFactory.getStyledIndices().add(playlistIndex);
        }
    }
}
