package de.pheru.media.gui.applicationwindow.editfile;

import de.pheru.fx.util.focus.FocusTraversal;
import de.pheru.media.cdi.qualifiers.TableData;
import de.pheru.media.cdi.qualifiers.XMLSettings;
import de.pheru.media.data.ArtworkData;
import de.pheru.media.data.Mp3FileData;
import de.pheru.media.gui.nodes.NumberComboBox;
import de.pheru.media.settings.Settings;
import de.pheru.media.task.SaveFilesTask;
import de.pheru.media.task.TaskPool;
import de.pheru.media.util.ByteUtil;
import de.pheru.media.util.Comparators;
import java.io.File;
import java.io.IOException;
import java.net.URL;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jaudiotagger.tag.id3.valuepair.ImageFormats;

@ApplicationScoped
public class EditFilePresenter implements Initializable {

    public static final String DIFF_VALUES = "<Verschiedene Werte>";
    public static final String NOT_EDITABLE = "<Bei Mehrfachauswahl nicht editierbar>";

    private static final Logger LOGGER = LogManager.getLogger(EditFilePresenter.class);

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
    private NumberComboBox<String> yearField;
    @FXML
    private NumberComboBox<String> trackField;
    @FXML
    private ComboBox<String> genreField;
    @FXML
    private Pane coverPane;
    @FXML
    private ImageView coverView;
    @FXML
    private Label coverInfo;

    @Inject
    @XMLSettings
    private Settings settings;
    @Inject
    @TableData(TableData.Source.MAIN_SELECTED)
    private ObservableList<Mp3FileData> selectedData;
    @Inject
    private TaskPool taskPool;

    private final ChangeListener<Boolean> sortListener = (ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
        updateFields(); //Es wird ein komplettes Update durchgeführt, um auch die ursprüngliche Reihenfolge wiederherzustellen
    };

    private final ObservableList<ComboBox<String>> fields = FXCollections.observableArrayList();

    private final Mp3FileData changeData = new Mp3FileData();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addAllFieldsToList();
        bindUI();
        setUpListeners();
        FocusTraversal.createFocusTraversalGroup("editFileFields", fileNameField, titleField.getEditor(), albumField.getEditor(),
                artistField.getEditor(), trackField.getEditor(), yearField.getEditor(), genreField.getEditor());
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
        changeData.fileNameProperty().bind(fileNameField.textProperty().concat(".mp3"));
        changeData.titleProperty().bind(titleField.valueProperty());
        changeData.albumProperty().bind(albumField.valueProperty());
        changeData.artistProperty().bind(artistField.valueProperty());
        changeData.genreProperty().bind(genreField.valueProperty());
        changeData.yearProperty().bind(yearField.valueProperty());
        changeData.trackProperty().bind(trackField.valueProperty());
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
        synchronizeTitleBox.selectedProperty().bindBidirectional(settings.editFileSynchronizeTitleProperty());
        sortTitleBox.selectedProperty().bindBidirectional(settings.editFileSortTitleProperty());
        sortAlbumBox.selectedProperty().bindBidirectional(settings.editFileSortAlbumProperty());
        sortArtistBox.selectedProperty().bindBidirectional(settings.editFileSortArtistProperty());
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
        fileNameField.setText(NOT_EDITABLE);
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
        for (int i = 0; i < selectedData.size() - 1; i++) {
            //TODO genauerer Image-Vergleich
            if (!ArtworkData.equals(selectedData.get(i).getArtworkData(), selectedData.get(i + 1).getArtworkData())) {
                removeCover("<Verschiedene Cover vorhanden>");
                changeData.setArtworkData(null);
                return;
            }
        }
        setCover(selectedData.get(0).getArtworkData());
        changeData.setArtworkData(selectedData.get(0).getArtworkData());
    }

    /**
     * Aktiviert bzw. deaktiviert die Synchronisation der Felder für den
     * Dateinamen und den Titel.
     */
    private void setUpTitleSynchronization() {
        if (synchronizeTitleBox.isSelected()) {
            fileNameField.setDisable(true);
            fileNameField.textProperty().bind(titleField.getEditor().textProperty());
        } else {
            fileNameField.textProperty().unbind();
            fileNameField.setDisable(false);
        }
    }

    private void setCover(ArtworkData artworkData) {
        if (artworkData != null) {
            coverView.setImage(ByteUtil.byteArrayToImage(artworkData.getBinaryData()));
            //TODO CoverInfo: Höhe x Breite oder Breite x Höhe?
            coverInfo.setText(artworkData.getMimeType() + " | " + artworkData.getHeight() + " x " + artworkData.getWidth()); 
        } else {
            removeCover("<Kein Cover vorhanden>");
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
            byte[] imageAsByteArray;
            try {
                imageAsByteArray = ByteUtil.fileToByteArray(imageAsFile);
            } catch (IOException e) {
                LOGGER.error("Exception converting file to byte-Array!", e);
                //TODO chooseCover: Exception behandeln
                imageAsByteArray = new byte[0];
            }
            Image image = ByteUtil.byteArrayToImage(imageAsByteArray);
            ArtworkData artworkData = new ArtworkData(imageAsByteArray, Double.valueOf(image.getWidth()).intValue(),
                    Double.valueOf(image.getHeight()).intValue(), ImageFormats.getMimeTypeForBinarySignature(imageAsByteArray));
            changeData.setArtworkData(artworkData);
            setCover(artworkData);

        }
    }

    @FXML
    public void save() {
        taskPool.addTask(new SaveFilesTask(FXCollections.observableArrayList(selectedData), new Mp3FileData(changeData)));
    }

    //@FXML
    public void discard() {
        updateFields();
    }

//    @FXML
    public void delete() {
    }
}
