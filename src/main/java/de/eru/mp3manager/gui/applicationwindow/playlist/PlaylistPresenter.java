package de.eru.mp3manager.gui.applicationwindow.playlist;

import de.eru.mp3manager.cdi.CurrentTitleEvent;
import de.eru.mp3manager.cdi.SelectedTableData;
import de.eru.mp3manager.cdi.TableData;
import de.eru.mp3manager.cdi.TableDataSource;
import de.eru.mp3manager.cdi.Updated;
import de.eru.mp3manager.cdi.XMLSettings;
import de.eru.mp3manager.data.Mp3FileData;
import de.eru.mp3manager.data.Playlist;
import de.eru.mp3manager.gui.utils.CssRowFactory;
import de.eru.mp3manager.gui.utils.DragAndDropRowFactory;
import de.eru.mp3manager.gui.utils.DropCompletedEvent;
import de.eru.mp3manager.service.FileService;
import de.eru.mp3manager.settings.Settings;
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
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.util.Pair;
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
    @XMLSettings
    private Settings settings;
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
        DragAndDropRowFactory dndRowFactory = new DragAndDropRowFactory(table, Mp3FileData.EMPTY_PLAYLIST_DATA);// {
        dndRowFactory.setOnDropCompleted(new EventHandler<DropCompletedEvent>() {

            @Override
            public void handle(DropCompletedEvent event) {
                Integer newCurrentIndex = playlist.getCurrentTitleIndex();
                for (Pair<Integer, Integer> p : event.getMovedIndices()) {
                    System.out.println("C:" + newCurrentIndex + " | " + p.getKey() + " -> " + p.getValue());
                    if (p.getKey().equals(newCurrentIndex)) {
                        System.out.println("==");
                        newCurrentIndex = p.getValue();
                        break;
                    } else if (p.getKey() < newCurrentIndex && p.getValue() >= newCurrentIndex) {
                        System.out.println("--");
                        newCurrentIndex--;
                    } else if (p.getKey() > newCurrentIndex && p.getValue() <= newCurrentIndex) {
                        System.out.println("++");
                        newCurrentIndex++;
                    }
                }
                playlist.setCurrentTitleIndex(newCurrentIndex);
            }
        });
//            @Override
//            protected void onDropCompleted(DropCompletedEvent event) { 
//            }
//        };
//        {
//
//            @Override
//            public void onEveryRowDropCompleted(int oldIndex, int targetIndex) {
//                System.out.println("Current: " + playlist.getCurrentTitleIndex() + " - Old: " + oldIndex + " - target: " + targetIndex);
//                if (oldIndex == playlist.getCurrentTitleIndex()) {
//                    System.out.println("1");
//                    playlist.setCurrentTitleIndex(targetIndex); //Event wird automatisch gefeuert
//                } else if (oldIndex > playlist.getCurrentTitleIndex() && targetIndex <= playlist.getCurrentTitleIndex()) {
//                    System.out.println("2");
//                    playlist.setCurrentTitleIndex(playlist.getCurrentTitleIndex() + 1); //Event wird automatisch gefeuert
//                } else if (oldIndex < playlist.getCurrentTitleIndex() && targetIndex >= playlist.getCurrentTitleIndex()) {
//                    System.out.println("3");
//                    playlist.setCurrentTitleIndex(playlist.getCurrentTitleIndex() - 1); //Event wird automatisch gefeuert
//                } else {
//                    System.out.println("else");
//                }
//            }
//
//        };
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
                    String fileName = playlist.getFileName().replace("." + Playlist.FILE_EXTENSION, "");
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
        try {
            FileService.savePlaylist(new File(playlist.getAbsolutePath()), playlist.getTitles());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void savePlaylistAs() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Wiedergabeliste speichern");
        fileChooser.setInitialDirectory(new File(settings.getPlaylistFilePath()));

        String fileName = "Wiedergabeliste";
        int i = 2;
        while (new File(settings.getPlaylistFilePath() + fileName + "." + Playlist.FILE_EXTENSION).exists()) {
            fileName = "Wiedergabeliste (" + i + ")";
        }
        fileChooser.setInitialFileName(fileName + "." + Playlist.FILE_EXTENSION);

        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Wiedergabelisten", "*." + Playlist.FILE_EXTENSION));
        File playlistFile = fileChooser.showSaveDialog(table.getScene().getWindow());
        if (playlistFile != null) {
            try {
                FileService.savePlaylist(playlistFile, playlist.getTitles());
                playlist.setFilePath(playlistFile.getParent());
                playlist.setFileName(playlistFile.getName());
                settings.setPlaylistFilePath(fileChooser.getInitialDirectory().getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void loadPlaylist() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Wiedergabeliste laden");
        fileChooser.setInitialDirectory(new File(settings.getPlaylistFilePath()));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Wiedergabelisten", "*." + Playlist.FILE_EXTENSION));
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
