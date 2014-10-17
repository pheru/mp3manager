package de.eru.mp3manager.gui.applicationwindow.musicplayer;

import de.eru.mp3manager.Settings;
import de.eru.mp3manager.data.Mp3FileData;
import de.eru.mp3manager.data.Playlist;
import de.eru.mp3manager.player.MusicPlayer;
import de.eru.mp3manager.utils.formatter.ByteFormatter;
import de.eru.mp3manager.utils.formatter.TimeFormatter;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javax.inject.Inject;

public class MusicPlayerPresenter implements Initializable{

    @FXML
    private Label titleLabel;
    @FXML
    private Label albumLabel;
    @FXML
    private Label artistLabel;
    @FXML
    private ProgressBar durationProgressBar;
    @FXML
    private Slider durationSlider;
    @FXML
    private Label currentTimeLabel;
    @FXML
    private Label totalTimeLabel;
    @FXML
    private ImageView coverView;
    @FXML
    private ProgressBar volumeProgressBar;
    @FXML
    private Slider volumeSlider;
    @FXML
    private Label volumeLabel;
    @FXML
    private ToggleButton randomButton;
    @FXML
    private ToggleButton repeatButton;

    @Inject
    private Settings settings;
    @Inject
    private Playlist playlist;
    @Inject
    private MusicPlayer player;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        player = new MusicPlayer();
        bindUI();
    }

    private void bindUI() {
        durationProgressBar.progressProperty().bind(durationSlider.valueProperty());
        currentTimeLabel.textProperty().bind(createTimeBinding(player.currentTimeProperty()));
        totalTimeLabel.textProperty().bind(createTimeBinding(player.totalTimeProperty()));

        volumeProgressBar.progressProperty().bind(volumeSlider.valueProperty().divide(100.0));
        volumeLabel.textProperty().bind(new StringBinding() {
            {
                bind(volumeProgressBar.progressProperty());
            }

            @Override
            protected String computeValue() {
                return Double.valueOf(volumeSlider.valueProperty().get()).intValue() + "%";
            }
        });
        randomButton.selectedProperty().bindBidirectional(settings.musicPlayerRandomProperty());
        repeatButton.selectedProperty().bindBidirectional(settings.musicPlayerRepeatProperty());
        volumeSlider.valueProperty().bindBidirectional(settings.musicPlayerVolumeProperty());
        playlist.currentTitleProperty().addListener(new ChangeListener<Mp3FileData>() {

            @Override
            public void changed(ObservableValue<? extends Mp3FileData> observable, Mp3FileData oldValue, Mp3FileData newValue) {
                titleLabel.setText(newValue.getTitle());
                albumLabel.setText(newValue.getAlbum());
                artistLabel.setText(newValue.getArtist());
                coverView.setImage(ByteFormatter.byteArrayToImage(newValue.getCover()));
            }
        });
    }

    private StringBinding createTimeBinding(IntegerProperty property) {
        return new StringBinding() {
            {
                bind(property);
            }

            @Override
            protected String computeValue() {
                return TimeFormatter.secondsToDurationFormat(property.get(), false);
            }
        };
    }
}
