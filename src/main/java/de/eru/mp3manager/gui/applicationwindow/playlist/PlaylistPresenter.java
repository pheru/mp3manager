package de.eru.mp3manager.gui.applicationwindow.playlist;

import de.eru.mp3manager.data.Mp3FileData;
import de.eru.mp3manager.data.Playlist;
import de.eru.mp3manager.service.FileService;
import de.eru.mp3manager.utils.formatter.TimeFormatter;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import javax.inject.Inject;

public class PlaylistPresenter implements Initializable {

    @FXML
    private TableView<Mp3FileData> table;
    @FXML
    private Label totalDurationLabel;
    @FXML
    private Label titlesSize;

    @Inject
    private Playlist playlist;

    private ObservableList<Mp3FileData> selectedTitles;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initTable();
        bindUI();
    }

    /**
     * Initialisiert die Tabelle.
     */
    private void initTable() {
        table.setItems(playlist.getTitles());
        selectedTitles = table.getSelectionModel().getSelectedItems();
    }

    /**
     * Bindet die UI-Elemente untereinander.
     */
    private void bindUI() {
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
    }

    @FXML
    private void savePlaylist() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Wiedergabeliste speichern");
        fileChooser.setInitialDirectory(new File("D:\\"));
        fileChooser.setInitialFileName("Wiedergabeliste." + Playlist.FILE_EXTENSION);
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Wiedergabelisten", "*." + Playlist.FILE_EXTENSION));
        File playlistFile = fileChooser.showSaveDialog(table.getScene().getWindow());
        if (playlistFile != null) {
            try {
                boolean savePlaylist = FileService.savePlaylist(playlistFile, playlist);
                System.out.println(savePlaylist);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void loadPlaylist() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Wiedergabeliste laden");
        fileChooser.setInitialDirectory(new File("D:\\"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Wiedergabelisten", "*." + Playlist.FILE_EXTENSION));
        File playlistFile = fileChooser.showOpenDialog(table.getScene().getWindow());
        if (playlistFile != null) {
            System.out.println("Playlist laden");
        }
    }

    @FXML
    private void deletePlaylist() {
        boolean deletePlaylist = FileService.deletePlaylist(playlist.getAbsolutePath());
    }
}
