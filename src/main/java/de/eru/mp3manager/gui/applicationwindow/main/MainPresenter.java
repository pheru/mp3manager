package de.eru.mp3manager.gui.applicationwindow.main;

import de.eru.mp3manager.settings.Settings;
import de.eru.mp3manager.cdi.CurrentTitleEvent;
import de.eru.mp3manager.data.Mp3FileData;
import de.eru.mp3manager.data.Playlist;
import de.eru.mp3manager.cdi.TableData;
import de.eru.mp3manager.cdi.TableDataSource;
import de.eru.mp3manager.cdi.Updated;
import de.eru.mp3manager.cdi.XMLSettings;
import de.eru.mp3manager.gui.utils.CssRowFactory;
import de.eru.mp3manager.gui.utils.TablePlaceholders;
import de.eru.mp3manager.settings.ColumnSettings;
import de.eru.mp3manager.utils.TaskPool;
import de.eru.mp3manager.utils.task.TaskFactory;
import de.eru.pherufx.focus.FocusTraversal;
import de.eru.pherufx.mvp.InjectableList;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SortEvent;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

@ApplicationScoped
public class MainPresenter implements Initializable {

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
    private TableView<Mp3FileData> table;
    private CssRowFactory<Mp3FileData> tableRowFactory; //TODO Liste der Indizes würde reichen

    public static final Node DEFAULT_TABLE_PLACEHOLDER = TablePlaceholders.NO_FILTER_RESULT;

    @Inject
    private TaskPool taskPool;
    @Inject
    private Playlist playlist;
    @Inject
    @XMLSettings
    private Settings settings;

    @Inject
    @TableData(source = TableDataSource.MAIN_ALL)
    private InjectableList<Mp3FileData> masterData;
    @Inject
    @TableData(source = TableDataSource.MAIN_SELECTED)
    private InjectableList<Mp3FileData> selectedData;

    private boolean updatingColumnsOrderList = false;
    private boolean updatingColumnsOrderTable = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initTable();
        bindUI();
        FocusTraversal.createFocusTraversalGroup("mainGroup", table, TablePlaceholders.getEmptyDirectoryButton(),
                TablePlaceholders.getNoDirectoryButton(), filterTextField);
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
//        playlist.currentTitleIndexProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
//            updateStyledIndex(newValue.intValue());
//        });
        if (!settings.getMusicDirectory().isEmpty()) {
            table.setPlaceholder(DEFAULT_TABLE_PLACEHOLDER);
        } else {
            table.setPlaceholder(TablePlaceholders.NO_DIRECTORY);
        }
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        table.setItems(setUpTableFilter());
        selectedData.set(table.getSelectionModel().getSelectedItems());
        initColumns();
        settings.getAllMainColumnSettings().addListener((ListChangeListener.Change<? extends ColumnSettings> change) -> {
            if (!updatingColumnsOrderList) {
                updateColumnsOrderTable();
            }
        });
        table.getColumns().addListener((ListChangeListener.Change<? extends TableColumn<Mp3FileData, ?>> change) -> {
            if (!updatingColumnsOrderTable) {
                updateColumnsOrderList();
            }
        });
        updateColumnsOrderTable(); // Initiale Anpassung der Reihenfolge an die Settings
        table.setOnSort((SortEvent<TableView<Mp3FileData>> event) -> {
            updateStyledIndex(playlist.getCurrentTitleIndex());
        });
        TablePlaceholders.getEmptyDirectoryButton().setText("Verzeichnis wechseln");
        TablePlaceholders.getEmptyDirectoryButton().setOnAction((ActionEvent event) -> {
            changeDirectory();
        });
        TablePlaceholders.getNoDirectoryButton().setText("Verzeichnis wählen");
        TablePlaceholders.getNoDirectoryButton().setOnAction((ActionEvent event) -> {
            changeDirectory();
        });
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

    private void initColumns() {
        for (MainColumn column : MainColumn.values()) {
            TableColumn<Mp3FileData, String> tableColumn = new TableColumn<>(column.getColumnName());
            if (column.isAlignRight()) {
                tableColumn.setCellFactory((TableColumn<Mp3FileData, String> param) -> {
                    TableCell<Mp3FileData, String> cell = new TableCell<Mp3FileData, String>() {

                        @Override
                        protected void updateItem(String item, boolean empty) {
                            super.updateItem(item, empty);

                            if (empty || item == null) {
                                setText(null);
                                setGraphic(null);
                            } else {
                                setText(item);
                            }
                        }

                    };
                    cell.setAlignment(Pos.CENTER_RIGHT);
                    return cell;
                });
            }
            tableColumn.prefWidthProperty().bind(settings.getMainColumnSettings(column).widthProperty());
            settings.getMainColumnSettings(column).widthProperty().bind(tableColumn.widthProperty());
            tableColumn.visibleProperty().bindBidirectional(settings.getMainColumnSettings(column).visibleProperty());
            tableColumn.setCellValueFactory(new PropertyValueFactory(column.getPropertyName()));
            if (column.getComparator() != null) {
                tableColumn.setComparator(column.getComparator());
            }
            table.getColumns().add(tableColumn);
        }
    }

    private void updateColumnsOrderTable() {
        updatingColumnsOrderTable = true;
        List<TableColumn> columns = new ArrayList<>(table.getColumns());
        table.getColumns().clear();
        for (ColumnSettings cs : settings.getAllMainColumnSettings()) {
            table.getColumns().add(getColumnByName(columns, cs.getColumn().getColumnName()));
        }
        updatingColumnsOrderTable = false;
    }

    private TableColumn getColumnByName(List<TableColumn> list, String name) {
        for (TableColumn c : list) {
            if (c.getText().equals(name)) {
                return c;
            }
        }
        return null;
    }

    private void updateColumnsOrderList() {
        updatingColumnsOrderList = true;
        List<ColumnSettings> newOrder = new ArrayList<>();
        for (TableColumn<Mp3FileData, ?> column : table.getColumns()) {
            newOrder.add(settings.getMainColumnSettings(MainColumn.getMainColumnByColumnName(column.getText())));
        }
        settings.getAllMainColumnSettings().clear();
        for (ColumnSettings cs : newOrder) {
            settings.getAllMainColumnSettings().add(cs);
        }
        updatingColumnsOrderList = false;
    }

    /**
     * Bindet die UI-Elemente untereinander.
     */
    private void bindUI() {
        TablePlaceholders.noFilterResultFilterProperty().bind(filterTextField.textProperty());
        clearFilterButton.visibleProperty().bind(filterTextField.textProperty().isEmpty().not());
//        taskCancelButton.disableProperty().bind(taskPool.cancellingProperty().or(taskPool.runningProperty().not()));
        taskCancelButton.disableProperty().bind(taskPool.statusProperty().isNotEqualTo(TaskPool.Status.RUNNING));
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
        directoryLabel.textProperty().bind(settings.musicDirectoryProperty());

    }

    private void currentTitleUpdated(@Observes @Updated CurrentTitleEvent event) {
        updateStyledIndex(event.getNewCurrentTitleIndex());
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
            settings.setMusicDirectory(directory.getAbsolutePath());
            readDirectory();
        }
    }

    /**
     * Liest alle MP3-Dateien aus dem übergebenen Verzeichnis, überträgt diese
     * in Mp3FileData-Objekte und fügt sie der Liste von Daten hinzu.
     */
    public void readDirectory() {
        String directory = settings.getMusicDirectory();
        if (directory != null && !directory.isEmpty()) {
            Task<Void> readDirectoryTask = TaskFactory.createReadDirectoryTask(directory, masterData, table.placeholderProperty(), playlist.getTitles());
            readDirectoryTask.runningProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                if (oldValue && !newValue) {
                    updateStyledIndex(playlist.getCurrentTitleIndex());
                }
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
     *
     */
    @FXML
    private void play() {
        if(!selectedData.isEmpty()){
            //TODO implementieren
        }
    }

    /**
     * Kontexmenü-Methode.<br/>
     * Fügt die ausgewählten Titel der Wiedergabeliste hinzu.
     */
    @FXML
    private void addToPlaylist() {
        playlist.add(selectedData);
    }

    @FXML
    private void clearFilter() {
        filterTextField.setText("");
    }

}
