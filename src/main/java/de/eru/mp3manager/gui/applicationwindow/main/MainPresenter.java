package de.eru.mp3manager.gui.applicationwindow.main;

import de.eru.mp3manager.Settings;
import de.eru.mp3manager.data.Mp3FileData;
import de.eru.mp3manager.data.Playlist;
import de.eru.mp3manager.cdi.InjectableList;
import de.eru.mp3manager.cdi.SelectedTableData;
import de.eru.mp3manager.utils.TaskPool;
import de.eru.mp3manager.utils.factories.TaskFactory;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.DirectoryChooser;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class MainPresenter implements Initializable {

    private static final String PLACEHOLDER_TEXT_NO_DIRECTORY_CHOSEN = "Es wurde kein Verzeichnis ausgewählt";

    @FXML
    private VBox root;
    @FXML
    private Label directoryLabel;
    @FXML
    private Button clearFilterButton;
    @FXML
    private TextField filterTextField;
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
    private final StringProperty tablePlaceholderText = new SimpleStringProperty();

    @Inject
    private TaskPool taskPool;
    @Inject
    private Playlist playlist;
    @Inject
    private Settings settings;

    @Inject
    @SelectedTableData(source = SelectedTableData.Source.MAIN)
    private InjectableList<Mp3FileData> selectedData;

    private final ObservableList<String> columnsOrder = FXCollections.observableArrayList();
    private final ObservableList<Mp3FileData> masterData = FXCollections.observableArrayList();

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
        Label placeholder = new Label(PLACEHOLDER_TEXT_NO_DIRECTORY_CHOSEN);
        placeholder.setTextAlignment(TextAlignment.CENTER);
        placeholder.textProperty().bindBidirectional(tablePlaceholderText);
        table.setPlaceholder(placeholder);
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        table.setItems(setUpTableFilter());
        selectedData.set(table.getSelectionModel().getSelectedItems());
        initColumns();
        columnsOrder.addListener((ListChangeListener.Change<? extends String> change) -> {
            if (!updatingColumnsOrderList) {
                updateColumnsOrderTable();
            }
        });
        table.getColumns().addListener((ListChangeListener.Change<? extends TableColumn<Mp3FileData, ?>> change) -> {
            if (!updatingColumnsOrderTable) {
                updateColumnsOrderList();
            }
        });
        Bindings.bindContentBidirectional(columnsOrder, settings.getMainColumnsOrder());
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
                if (mp3FileData.getAlbum() != null && mp3FileData.getAlbum().toLowerCase().contains(filter)) {
                    return true;
                } else if (mp3FileData.getArtist() != null && mp3FileData.getArtist().toLowerCase().contains(filter)) {
                    return true;
                } else if (mp3FileData.getTitle() != null && mp3FileData.getTitle().toLowerCase().contains(filter)) {
                    return true;
                } else if (mp3FileData.getGenre() != null && mp3FileData.getGenre().toLowerCase().contains(filter)) {
                    return true;
                } else if (mp3FileData.getFileName() != null && mp3FileData.getFileName().toLowerCase().contains(filter)) {
                    return true;
                }
                return false;
            });
        });
        SortedList<Mp3FileData> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(table.comparatorProperty());
        return sortedData;
    }

    private void initColumns() {
        for (MainColumn column : MainColumn.values()) {
            TableColumn<Mp3FileData, String> tableColumn = new TableColumn<>(column.getColumnName());
            tableColumn.prefWidthProperty().bind(settings.mainColumnWidthProperties().get(column.getColumnName()));
            settings.mainColumnWidthProperties().get(column.getColumnName()).bind(tableColumn.widthProperty());
            tableColumn.visibleProperty().bindBidirectional(settings.mainColumnVisibleProperties().get(column.getColumnName()));
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
        for (String columnName : columnsOrder) {
            table.getColumns().add(getColumnByName(columns, columnName));
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
        columnsOrder.clear();
        for (TableColumn<Mp3FileData, ?> column : table.getColumns()) {
            columnsOrder.add(column.getText());
        }
        updatingColumnsOrderList = false;
    }

    /**
     * Bindet die UI-Elemente untereinander.
     */
    private void bindUI() {
        clearFilterButton.visibleProperty().bind(filterTextField.textProperty().isEmpty().not());
        StringProperty sp1 = new SimpleStringProperty();
        sp1.bind(Bindings.size(table.getSelectionModel().getSelectedItems()).asString());
        StringProperty sp2 = new SimpleStringProperty();
        sp2.bind(Bindings.size(table.getItems()).asString());
        statusR1.textProperty().bind(sp1.concat(" von ").concat(sp2).concat(" Dateien ausgewählt"));
        statusR2.textProperty().bind(Bindings.size(masterData).asString().concat(" Dateien insgesamt"));
        statusL1.textProperty().bind(taskPool.titleProperty());
        statusL2.textProperty().bind(taskPool.messageProperty());
        taskProgress.progressProperty().bind(taskPool.progressProperty());
        directoryLabel.textProperty().bind(settings.musicDirectoryProperty());

    }

    @FXML
    public void changeDirectory() {
        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setTitle("Musik-Vereichnis auswählen");
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
            taskPool.addTask(TaskFactory.createReadDirectoryTask(directory, masterData, tablePlaceholderText, table.disableProperty()));
        }
    }

    /**
     * Kontextmenü-Methode.<br/>
     * Spielt die ausgewählten Titel ab.
     *
     */
    @FXML
    private void play() {
    }

    /**
     * Kontexmenü-Methode.<br/>
     * Fügt die ausgewählten Titel der Wiedergabeliste hinzu.
     */
    @FXML
    private void addToPlaylist() {
        playlist.getTitles().addAll(selectedData);
    }

    @FXML
    private void clearFilter() {
        filterTextField.setText("");
    }

}
