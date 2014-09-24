package de.eru.mp3manager.gui.applicationwindow.editfile;

import de.eru.mp3manager.data.Mp3FileData;
import de.eru.mp3manager.data.utils.InjectableList;
import de.eru.mp3manager.utils.TaskPool;
import de.eru.mp3manager.utils.formatter.ByteFormatter;
import de.eru.mp3manager.utils.factories.ComparatorFactory;
import de.eru.mp3manager.utils.factories.TaskFactory;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Comparator;
import java.util.ResourceBundle;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import org.jaudiotagger.audio.exceptions.CannotWriteException;

public class EditFilePresenter implements Initializable {

    public static final String DIFF_VALUES = "<Verschiedene Werte>";
    public static final String NOT_CHANGABLE = "<Bei Mehrfachauswahl nicht editierbar>";

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
    private Button saveButton;

    private final ObservableList<ComboBox<String>> fields = FXCollections.observableArrayList();

    Comparator<String> numberComparator = ComparatorFactory.createNumberComparator();

    @Inject
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
     * F�gt alle ComboBoxen der Liste mit Feldern hinzu.
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
        coverPane.maxHeightProperty().bind(root.heightProperty().subtract(coverPane.layoutYProperty()));
        coverView.fitHeightProperty().bind(coverView.fitWidthProperty());
        coverView.fitWidthProperty().bind(new DoubleBinding() {
            {
                bind(coverPane.widthProperty(), coverPane.maxHeightProperty());
            }

            @Override
            protected double computeValue() {
                double value;
                if (coverPane.widthProperty().get() > coverPane.maxHeightProperty().get()) {
                    value = coverPane.maxHeightProperty().get();
                } else {
                    value = coverPane.widthProperty().get();
                }
                return value - saveButton.getHeight() - 3; // -3 wegen Separator
            }
        });
        changeData.fileNameProperty().bind(fileNameField.textProperty().concat(".mp3"));
        changeData.titleProperty().bind(titleField.valueProperty());
        changeData.albumProperty().bind(albumField.valueProperty());
        changeData.artistProperty().bind(artistField.valueProperty());
        changeData.genreProperty().bind(genreField.valueProperty());
        changeData.yearProperty().bind(yearField.valueProperty());
        changeData.trackProperty().bind(trackField.valueProperty());
        changeData.coverProperty().bind(new ObjectBinding<byte[]>() {
            {
                bind(coverView.imageProperty());
            }

            @Override
            protected byte[] computeValue() {
                if (coverView.getImage() == null) {
                    return new byte[0];
                }
                BufferedImage bufferedImage = SwingFXUtils.fromFXImage(coverView.getImage(), null);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                try {
                    ImageIO.write(bufferedImage, "png", outputStream);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                return outputStream.toByteArray();
            }
        });
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
    }

    /**
     * Aktualisiert die Felder mit den Werten der ausgew�hlten Mp3FileData-Objekte.
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
     * Bef�llt die Felder mit den Werten eines einzelnen Mp3FileData-Objektes.
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
        coverView.setImage(ByteFormatter.byteArrayToImage(singleData.getCover()));
    }

    /**
     *
     * Bef�llt das Feld mit dem �bergebenen Wert. <br>
     * Ist der Wert dar�ber hinaus kein leerer String, so wird er auch der Liste der ComboBox hinzuge�ft.
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
     * Bef�llt die Felder mit den Werten mehrerer Mp3FileData-Objekte. <br>
     * Dabei werden alle Werte den Listen der ComboBoxen hinzugef�gt. <br>
     * Es werden nur Werte hinzugef�gt, welche noch nicht in der Liste vorhanden sind. <br>
     * Ist am Ende nur ein Wert in der Liste, so wird dieser im Feld angezeigt. Sind es mehrere, so wird im Feld der Wert der Konstante DIFF_VALUES angezeigt.
     */
    private void fillFieldsWithMultipleData() {
        fillFieldItems();
        setFieldValues();
        sortFieldItems();
    }

    /**
     * Bef�llt die Listen der ComboBoxen.
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
     * F�gt der Liste der ComboBox den Wert hinzu, wenn dieser noch nicht vorhanden ist.
     *
     * @param field Die ComboBox.
     * @param value Der hinzuzuf�gende Wert.
     */
    private void addToFieldItemsOnce(ComboBox<String> field, String value) {
        if (!field.getItems().contains(value) && !value.isEmpty()) {
            field.getItems().add(value);
        }
    }

    /**
     * Setzt den angezeigten Wert f�r jedes Feld. <br>
     * Das Feld f�r den Dateinamen erh�lt dabei immer den Wert der Konstante NOT_CHANGABLE, da bei Mehrfachauswahl der Dateiname nicht ge�ndert werden darf.<br>
     * Enth�lt die Liste einer ComboBox mehrere Werte, so wird der angezeigte Wert dieses Feldes auf DIFF_VALUES gesetzt.
     */
    private void setFieldValues() {
        fileNameField.setText(NOT_CHANGABLE);
        for (ComboBox<String> field : fields) {
            if (field.getItems().size() > 1) {
                field.setValue(DIFF_VALUES);
            } else {
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
        trackField.getItems().sort(numberComparator);
        yearField.getItems().sort(numberComparator);
        genreField.getItems().sort(null);
    }

    /**
     * Aktiviert bzw. deaktiviert die Synchronisation der Felder f�r den Dateinamen und den Titel.
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

    @FXML
    public void save(){
        taskPool.addTask(TaskFactory.createSaveFilesTask(selectedData, changeData));
    }
}
