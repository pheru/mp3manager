package de.eru.mp3manager.gui.applicationwindow.editfile;

import de.eru.mp3manager.data.Mp3FileData;
import de.eru.mp3manager.cdi.TableData;
import de.eru.mp3manager.cdi.TableDataSource;
import de.eru.mp3manager.data.ArtworkData;
import de.eru.mp3manager.utils.Comparators;
import de.eru.mp3manager.utils.TaskPool;
import de.eru.mp3manager.utils.formatter.ByteFormatter;
import de.eru.mp3manager.utils.factories.TaskFactory;
import de.eru.pherufx.mvp.InjectableList;
import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Window;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.jaudiotagger.tag.id3.valuepair.ImageFormats;

@ApplicationScoped
public class EditFilePresenter implements Initializable {

    public static final String DIFF_VALUES = "<Verschiedene Werte>";
    public static final String NOT_CHANGABLE = "<Bei Mehrfachauswahl nicht editierbar>";
    private static final Image MULTIPLE_COVERS_IMAGE = new Image("img/noImage.png");

    @FXML
    private GridPane root;
    @FXML
    private CheckBox synchronizeTitleBox;
    @FXML
    private TextField fileNameField;
    @FXML
    private ComboBox<String> titleField;
    @FXML
    private CheckBox sortTitleBox;
    @FXML
    private ComboBox<String> albumField;
    @FXML
    private CheckBox sortAlbumBox;
    @FXML
    private ComboBox<String> artistField;
    @FXML
    private CheckBox sortArtistBox;
    @FXML
    private ComboBox<String> yearField;
    @FXML
    private ComboBox<String> trackField;
    @FXML
    private ComboBox<String> genreField;
    @FXML
    private Pane coverPane;
    @FXML
    private ImageView coverView;
    @FXML
    private Label coverInfo;

    private final ChangeListener<Boolean> sortListener = (ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
        updateFields(); //Es wird ein komplettes Update durchgeführt, um auch die ursprüngliche Reihenfolge wiederherzustellen
    };

    private final ObservableList<ComboBox<String>> fields = FXCollections.observableArrayList();

    @Inject
    @TableData(source = TableDataSource.MAIN_SELECTED)
    private InjectableList<Mp3FileData> selectedData;
    @Inject
    private TaskPool taskPool;

    private final Mp3FileData changeData = new Mp3FileData();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addAllFieldsToList();
        bindUI();
        setUpListeners();
    }

    /**
     * Fügt alle ComboBoxen der Liste mit Feldern hinzu.
     */
    private void addAllFieldsToList() {
        fields.add(artistField);
        fields.add(albumField);
        fields.add(titleField);
        fields.add(genreField);
        fields.add(trackField);
        fields.add(yearField);
    }

    /**
     * Bindet die UI-Elemente untereinander.
     */
    private void bindUI() {
        coverView.fitHeightProperty().bind(coverView.fitWidthProperty());
        coverView.fitWidthProperty().bind(new DoubleBinding() {
            {
                bind(coverPane.widthProperty(), coverPane.heightProperty());
            }

            @Override
            protected double computeValue() {
                double height = coverPane.heightProperty().get();
                double width = coverPane.widthProperty().get();
                if (width > height) {
                    return height;
                } else {
                    return width;
                }
            }
        });
        changeData.fileNameProperty().bind(fileNameField.textProperty().concat(".mp3"));
        changeData.titleProperty().bind(titleField.valueProperty());
        changeData.albumProperty().bind(albumField.valueProperty());
        changeData.artistProperty().bind(artistField.valueProperty());
        changeData.genreProperty().bind(genreField.valueProperty());
        changeData.yearProperty().bind(yearField.valueProperty());
        changeData.trackProperty().bind(trackField.valueProperty());
    }

    /**
     * Erzeugt die verschiedenen Listener.
     */
    private void setUpListeners() {
        selectedData.addListener((ListChangeListener<Mp3FileData>) (ListChangeListener.Change<? extends Mp3FileData> change) -> {
            updateFields();
        });
        synchronizeTitleBox.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            setUpTitleSynchronization();
        });
        //TODO Ansatz für Issue #7
//        sortArtistBox.addEventFilter(KeyEvent.KEY_PRESSED, (KeyEvent event) -> {
//            if (event.getCode() == KeyCode.TAB) {
//                fileNameField.requestFocus();
//                event.consume();
//            }
//        });
        fileNameField.focusedProperty().addListener(createSelectAllFocusListener(fileNameField));
        titleField.focusedProperty().addListener(createSelectAllFocusListener(titleField.getEditor()));
        albumField.focusedProperty().addListener(createSelectAllFocusListener(albumField.getEditor()));
        artistField.focusedProperty().addListener(createSelectAllFocusListener(artistField.getEditor()));
        genreField.focusedProperty().addListener(createSelectAllFocusListener(genreField.getEditor()));
        yearField.focusedProperty().addListener(createSelectAllFocusListener(yearField.getEditor()));
        trackField.focusedProperty().addListener(createSelectAllFocusListener(trackField.getEditor()));
        sortTitleBox.selectedProperty().addListener(sortListener);
        sortAlbumBox.selectedProperty().addListener(sortListener);
        sortArtistBox.selectedProperty().addListener(sortListener);
    }

    private ChangeListener<Boolean> createSelectAllFocusListener(TextField target) {
        return (ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            Platform.runLater(() -> {
                if (newValue) {
                    target.selectAll();
                }
            });
        };
    }

    /**
     * Aktualisiert die Felder mit den Werten der ausgewählten
     * Mp3FileData-Objekte.
     */
    private void updateFields() {
        clearFieldItems();
        if (selectedData.size() == 1) {
            setUpTitleSynchronization();
            synchronizeTitleBox.setDisable(false);
            fillFieldsWithSingleData();
        } else if (selectedData.size() > 1) {
            fileNameField.textProperty().unbind();
            fileNameField.setDisable(true);
            synchronizeTitleBox.setDisable(true);
            fillFieldsWithMultipleData();
        }
    }

    /**
     * Leert die Listen der ComboBoxen.
     */
    private void clearFieldItems() {
        for (ComboBox<String> field : fields) {
            field.getItems().clear();
        }
    }

    /**
     * Befüllt die Felder mit den Werten eines einzelnen Mp3FileData-Objektes.
     */
    private void fillFieldsWithSingleData() {
        Mp3FileData singleData = selectedData.get(0);
        if (!fileNameField.textProperty().isBound()) {
            fileNameField.setText(singleData.getFileName().replace(".mp3", ""));
        }
        fillField(titleField, singleData.getTitle());
        fillField(artistField, singleData.getArtist());
        fillField(albumField, singleData.getAlbum());
        fillField(genreField, singleData.getGenre());
        fillField(trackField, singleData.getTrack());
        fillField(yearField, singleData.getYear());
        setCover(singleData.getArtworkData());
        changeData.setArtworkData(singleData.getArtworkData());
    }

    /**
     *
     * Befüllt das Feld mit dem übergebenen Wert. <br>
     * Ist der Wert darüber hinaus kein leerer String, so wird er auch der Liste
     * der ComboBox hinzugefügt.
     *
     * @param field Das betreffende Feld.
     * @param value Der Wert.
     */
    private void fillField(ComboBox<String> field, String value) {
        if (value != null && !value.isEmpty()) {
            field.getItems().add(value);
        }
        field.setValue(value);
    }

    /**
     * Befüllt die Felder mit den Werten mehrerer Mp3FileData-Objekte. <br>
     * Dabei werden alle Werte den Listen der ComboBoxen hinzugefügt. <br>
     * Es werden nur Werte hinzugefügt, welche noch nicht in der Liste vorhanden
     * sind. <br>
     * Ist am Ende nur ein Wert in der Liste, so wird dieser im Feld angezeigt.
     * Sind es mehrere, so wird im Feld der Wert der Konstante DIFF_VALUES
     * angezeigt.
     */
    private void fillFieldsWithMultipleData() {
        fillFieldItems();
        setFieldValues();
        sortFieldItems();
        setCoverImageForMultipleData();
    }

    /**
     * Befüllt die Listen der ComboBoxen.
     */
    private void fillFieldItems() {
        for (Mp3FileData data : selectedData) {
            addToFieldItemsOnce(titleField, data.getTitle());
            addToFieldItemsOnce(artistField, data.getArtist());
            addToFieldItemsOnce(albumField, data.getAlbum());
            addToFieldItemsOnce(genreField, data.getGenre());
            addToFieldItemsOnce(trackField, data.getTrack());
            addToFieldItemsOnce(yearField, data.getYear());
        }
    }

    /**
     * Fügt der Liste der ComboBox den Wert hinzu, wenn dieser noch nicht
     * vorhanden ist.
     *
     * @param field Die ComboBox.
     * @param value Der hinzuzufügende Wert.
     */
    private void addToFieldItemsOnce(ComboBox<String> field, String value) {
        if (!field.getItems().contains(value) && !value.isEmpty()) {
            field.getItems().add(value);
        }
    }

    /**
     * Setzt den angezeigten Wert für jedes Feld. <br>
     * Das Feld für den Dateinamen erhält dabei immer den Wert der Konstante
     * NOT_CHANGABLE, da bei Mehrfachauswahl der Dateiname nicht geändert werden
     * darf.<br>
     * Enthält die Liste einer ComboBox mehrere Werte, so wird der angezeigte
     * Wert dieses Feldes auf DIFF_VALUES gesetzt.
     */
    private void setFieldValues() {
        fileNameField.setText(NOT_CHANGABLE);
        for (ComboBox<String> field : fields) {
            if (field.getItems().size() > 1) {
                field.setValue(DIFF_VALUES);
            } else if (!field.getItems().isEmpty()) {
                field.setValue(field.getItems().get(0));
            }
        }
    }

    /**
     * Sortiert die Listen der ComboBoxen.
     */
    private void sortFieldItems() {
        if (sortTitleBox.isSelected()) {
            titleField.getItems().sort(null);
        }
        if (sortAlbumBox.isSelected()) {
            albumField.getItems().sort(null);
        }
        if (sortArtistBox.isSelected()) {
            artistField.getItems().sort(null);
        }
        trackField.getItems().sort(Comparators.NUMBER_COMPARATOR);
        yearField.getItems().sort(Comparators.NUMBER_COMPARATOR);
        genreField.getItems().sort(null);
    }

    private void setCoverImageForMultipleData() {
        ArtworkData selectedArtworkData = selectedData.get(0).getArtworkData();
        for (Mp3FileData data : selectedData) {
            //TODO genauerer Image-Vergleich
            if (!Arrays.equals(data.getArtworkData().getBinaryData(), selectedArtworkData.getBinaryData())) {
                removeCover("Verschiedene Cover vorhanden");
                changeData.setArtworkData(null);
                return;
            }
        }
        setCover(selectedArtworkData);
        changeData.setArtworkData(selectedArtworkData);
    }

    /**
     * Aktiviert bzw. deaktiviert die Synchronisation der Felder für den
     * Dateinamen und den Titel.
     */
    private void setUpTitleSynchronization() {
        if (synchronizeTitleBox.isSelected()) {
            fileNameField.setDisable(true);
            fileNameField.textProperty().bind(titleField.valueProperty());
        } else {
            fileNameField.textProperty().unbind();
            fileNameField.setDisable(false);
        }
    }

    private void setCover(ArtworkData artworkData) {
        if (artworkData != null) {
            coverView.setImage(ByteFormatter.byteArrayToImage(artworkData.getBinaryData()));
            coverInfo.setText(artworkData.getMimeType() + " | " + artworkData.getHeight() + " x " + artworkData.getWidth()); //TODO Höhe x Breite oder Breite x Höhe?
        } else{
            removeCover("Kein Cover vorhanden");
        }
    }

    private void removeCover(String coverInfoText) {
        coverView.setImage(null);
        coverInfo.setText(coverInfoText);
    }

    @FXML
    private void chooseCover() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Album-Cover auswählen");
        fileChooser.getExtensionFilters()
                .addAll(new ExtensionFilter("All Images", "*.png",
                                "*.jpg", "*.jpeg", "*.bmp"),
                        new ExtensionFilter("bmp", "*.bmp"),
                        new ExtensionFilter("jpg, jpeg", "*.jpg",
                                "*.jpeg"),
                        new ExtensionFilter("png", "*.png"),
                        new ExtensionFilter("All Files", "*.*"));
        Window ownerWindow = root.getScene().getWindow();
        File imageAsFile = fileChooser.showOpenDialog(ownerWindow);
        if (imageAsFile != null) {
            byte[] imageAsByteArray = ByteFormatter.fileToByteArray(imageAsFile);
            Image image = ByteFormatter.byteArrayToImage(imageAsByteArray);
            ArtworkData artworkData = new ArtworkData(imageAsByteArray, Double.valueOf(image.getWidth()).intValue(),
                    Double.valueOf(image.getHeight()).intValue(), ImageFormats.getMimeTypeForBinarySignature(imageAsByteArray));
            changeData.setArtworkData(artworkData);
            setCover(artworkData);

        }
    }

    @FXML
    public void save() {
        taskPool.addTask(TaskFactory.createSaveFilesTask(FXCollections.observableArrayList(selectedData), new Mp3FileData(changeData)));
    }

    //@FXML
    public void discard() {
        updateFields();
    }

//    @FXML
    public void delete() {
    }
}
