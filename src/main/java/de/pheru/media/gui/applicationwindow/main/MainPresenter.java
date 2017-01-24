package de.pheru.media.gui.applicationwindow.main;

import de.pheru.fx.mvp.ObservableListWrapper;
import de.pheru.fx.util.properties.ObservableProperties;
import de.pheru.media.cdi.qualifiers.TableData;
import de.pheru.media.data.Mp3FileData;
import de.pheru.media.data.Playlist;
import de.pheru.media.gui.Settings;
import de.pheru.media.gui.applicationwindow.main.table.MainTable;
import de.pheru.media.gui.nodes.EmptyDirectoryPlaceholder;
import de.pheru.media.gui.nodes.NoDirectoryPlaceholder;
import de.pheru.media.gui.nodes.NoFilterResultPlaceholder;
import de.pheru.media.gui.nodes.ReadingDirectoryPlaceholder;
import de.pheru.media.gui.taskimpl.ReadDirectoryTaskImpl;
import de.pheru.media.gui.util.CssRowFactory;
import de.pheru.media.task.PheruMediaTask;
import de.pheru.media.task.TaskPool;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SortEvent;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

@ApplicationScoped
public class MainPresenter implements Initializable {

    private static final Logger LOGGER = LogManager.getLogger(MainPresenter.class);

    @FXML
    private VBox root;
    @FXML
    private Label directoryLabel;
    @FXML
    private Button clearFilterButton;
    @FXML
    private TextField filterTextField;
    @FXML
    private Button taskCancelButton;
    @FXML
    private Label statusL1;
    @FXML
    private Label statusL2;
    @FXML
    private Label statusR1;
    @FXML
    private Label statusR2;
    @FXML
    private ProgressIndicator taskProgress;
    @FXML
    private MainTable table;
    private CssRowFactory<Mp3FileData> tableRowFactory;

    @FXML
    private ReadingDirectoryPlaceholder readingDirectoryPlaceholder;
    @FXML
    private NoFilterResultPlaceholder noFilterResultPlaceholder;
    @FXML
    private EmptyDirectoryPlaceholder emptyDirectoryPlaceholder;
    @FXML
    private NoDirectoryPlaceholder noDirectoryPlaceholder;

    @Inject
    private TaskPool taskPool;
    @Inject
    private Playlist playlist;
    @Inject
    private ObservableProperties settings;

    @Inject
    @TableData(TableData.Source.MAIN)
    private ObservableList<Mp3FileData> masterData;
    @Inject
    @TableData(TableData.Source.MAIN_SELECTED)
    private ObservableListWrapper<Mp3FileData> selectedDataWrapper;

    private boolean updatingColumnsOrderList = false;
    private boolean updatingColumnsOrderTable = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initTable();
        bindUI();
    }

    /**
     * Initialisiert die Tabelle.
     */
    private void initTable() {
        tableRowFactory = new CssRowFactory<>("played", (TableView<Mp3FileData> param) -> {
            TableRow<Mp3FileData> row = new TableRow<>();
            row.setOnMouseClicked((MouseEvent event) -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    play();
                }
            });
            return row;
        });
        table.setRowFactory(tableRowFactory);
        if (settings.stringProperty(Settings.MUSIC_DIRECTORY).get().isEmpty()) {
            table.setPlaceholder(noDirectoryPlaceholder);
        } else {
            table.setPlaceholder(noFilterResultPlaceholder);
        }
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        table.setItems(setUpTableFilter());
        selectedDataWrapper.setList(table.getSelectionModel().getSelectedItems());
        table.applySettings(null); //TODO
        table.setOnSort((SortEvent<TableView<Mp3FileData>> event) -> updateStyledIndex(playlist.getCurrentTitleIndex()));
    }

    /**
     * Setzt Alles für den Tabellen-Filter auf und gibt eine Liste zurück,
     * welche von der Tabelle dargestellt werden kann.
     *
     * @return Die sortierte und gefilterte Liste.
     */
    private SortedList<Mp3FileData> setUpTableFilter() {
        FilteredList<Mp3FileData> filteredData = masterData.filtered((Mp3FileData t) -> true);
        filterTextField.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            table.getSelectionModel().clearSelection();
            filteredData.setPredicate((Mp3FileData mp3FileData) -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String filter = newValue.toLowerCase();
                return mp3FileData.getArtist().toLowerCase().contains(filter)
                        || mp3FileData.getAlbum().toLowerCase().contains(filter)
                        || mp3FileData.getTitle().toLowerCase().contains(filter)
                        || mp3FileData.getGenre().toLowerCase().contains(filter)
                        || mp3FileData.getFileName().toLowerCase().contains(filter);
            });
            updateStyledIndex(playlist.getCurrentTitleIndex());
        });
        filterTextField.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            Platform.runLater(() -> {
                if (newValue) {
                    filterTextField.selectAll();
                }
            });
        });
        SortedList<Mp3FileData> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(table.comparatorProperty());
        return sortedData;
    }


    private TableColumn<Mp3FileData, ?> getColumnByName(List<TableColumn<Mp3FileData, ?>> list, String name) {
        for (TableColumn<Mp3FileData, ?> c : list) {
            if (c.getText().equals(name)) {
                return c;
            }
        }
        return null;
    }

    /**
     * Bindet die UI-Elemente.
     */
    private void bindUI() {
        noFilterResultPlaceholder.filterProperty().bind(filterTextField.textProperty());
        clearFilterButton.visibleProperty().bind(filterTextField.textProperty().isEmpty().not());
        taskCancelButton.disableProperty().bind(taskPool.statusProperty().isNotEqualTo(PheruMediaTask.PheruMediaTaskStatus.RUNNING));
        taskProgress.styleProperty().bind(new StringBinding() {
            {
                bind(taskPool.statusProperty());
            }

            @Override
            protected String computeValue() {
                return "-fx-progress-color: " + taskPool.statusProperty().get().getColor() + ";";
            }
        });
        StringProperty sizeSelected = new SimpleStringProperty();
        sizeSelected.bind(Bindings.size(table.getSelectionModel().getSelectedItems()).asString());
        StringProperty sizeAll = new SimpleStringProperty();
        sizeAll.bind(Bindings.size(table.getItems()).asString());
        statusR1.textProperty().bind(sizeSelected.concat(" von ").concat(sizeAll).concat(" Dateien ausgewählt"));
        statusR2.textProperty().bind(Bindings.size(masterData).asString().concat(" Dateien insgesamt"));
        statusL1.textProperty().bind(taskPool.titleProperty());
        statusL2.textProperty().bind(taskPool.messageProperty());
        taskProgress.progressProperty().bind(taskPool.progressProperty());
        directoryLabel.textProperty().bind(settings.stringProperty(Settings.MUSIC_DIRECTORY));

        playlist.currentTitleIndexProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            updateStyledIndex(newValue.intValue());
        });
    }

    private void updateStyledIndex(int playlistIndex) {
        tableRowFactory.getStyledIndices().clear();
        if (playlistIndex >= 0) {
            tableRowFactory.getStyledIndices().add(table.getItems().indexOf(playlist.getTitles().get(playlistIndex)));
        }
    }

    @FXML
    public void changeDirectory() {
        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setTitle("Musik-Verzeichnis auswählen");
        File directory = dirChooser.showDialog(root.getScene().getWindow());
        if (directory != null) {
            settings.stringProperty(Settings.MUSIC_DIRECTORY).set(directory.getAbsolutePath());
            readDirectory();
        }
    }

    /**
     * Liest alle MP3-Dateien aus dem übergebenen Verzeichnis, überträgt diese
     * in Mp3FileData-Objekte und fügt sie der Liste von Daten hinzu.
     */
    public void readDirectory() {
        String directory = settings.stringProperty(Settings.MUSIC_DIRECTORY).get();
        if (directory != null && !directory.isEmpty()) {
            PheruMediaTask readDirectoryTask = new ReadDirectoryTaskImpl(directory, masterData, playlist.getTitles());
            readDirectoryTask.setOnRunning((WorkerStateEvent event) -> {
                table.setPlaceholder(readingDirectoryPlaceholder);
            });
            readDirectoryTask.setOnFinished((PheruMediaTask.PheruMediaTaskEvent event) -> {
                Platform.runLater(() -> {
                    updateStyledIndex(playlist.getCurrentTitleIndex());
                    if (masterData.isEmpty()) {
                        table.setPlaceholder(emptyDirectoryPlaceholder);
                    } else {
                        table.setPlaceholder(noFilterResultPlaceholder);
                    }
                });
            });
            taskPool.addTask(readDirectoryTask);
        }
    }

    @FXML
    private void cancelTask() {
        taskPool.cancelCurrentTask();
    }

    /**
     * Kontextmenü-Methode.<br/>
     * Spielt die ausgewählten Titel ab.
     */
    @FXML
    private void play() {
        if (!selectedDataWrapper.getList().isEmpty()) {
            //Kontextmenü-Play: implementieren
        }
    }

    /**
     * Kontexmenü-Methode.<br/>
     * Fügt die ausgewählten Titel der Wiedergabeliste hinzu.
     */
    @FXML
    private void addToPlaylist() {
        playlist.add(selectedDataWrapper.getList());
    }

    @FXML
    private void clearFilter() {
        filterTextField.setText("");
    }
}
