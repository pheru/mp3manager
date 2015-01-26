package de.eru.mp3manager.gui.applicationwindow.musicplayer;

import de.eru.mp3manager.Settings;
import de.eru.mp3manager.cdi.CurrentTitleEvent;
import de.eru.mp3manager.cdi.Updated;
import de.eru.mp3manager.data.Mp3FileData;
import de.eru.mp3manager.data.Playlist;
import de.eru.mp3manager.player.MusicPlayer;
import de.eru.mp3manager.utils.formatter.ByteFormatter;
import de.eru.mp3manager.utils.formatter.TimeFormatter;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.DoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.MediaPlayer;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

@ApplicationScoped
public class MusicPlayerPresenter implements Initializable {

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
    private Button playButton;
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

    private DoubleBinding durationSliderBinding;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        bindUI();
    }

    private void bindUI() {
        durationSliderBinding = new DoubleBinding() {
            {
                bind(player.currentTimeProperty());
            }

            @Override
            protected double computeValue() {
                double percentage = (double) player.getCurrentTime() / player.getTotalTime();
                if (Double.valueOf(percentage).isNaN()) {
                    return 0.0;
                }
                return percentage;
            }
        };
        durationSlider.valueProperty().bind(durationSliderBinding);
        durationProgressBar.progressProperty().bind(durationSlider.valueProperty().add(0.005)); //add() damit der Slider die Progressbar komplett überdeckt
        currentTimeLabel.textProperty().bind(createTimeBinding(player.currentTimeProperty()));

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
        playButton.textProperty().bind(new StringBinding() {
            {
                bind(player.statusProperty());
            }

            @Override
            protected String computeValue() {
                if (player.getStatus() != MediaPlayer.Status.PLAYING) {
                    return "Play";
                } else {
                    return "Pause";
                }
            }
        });
        randomButton.selectedProperty().bindBidirectional(settings.musicPlayerRandomProperty());
        repeatButton.selectedProperty().bindBidirectional(settings.musicPlayerRepeatProperty());
        volumeSlider.valueProperty().bindBidirectional(settings.musicPlayerVolumeProperty());
//        playlist.currentTitleIndexProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
//            updateCurrentTitleBinding(newValue.intValue());
//        });
    }

    private void currentTitleUpdated(@Observes @Updated CurrentTitleEvent event) { //TODO Aufbau/Ablauf nochmal überdenken
        updateCurrentTitleBinding(event.getNewCurrentTitle());
    }

    public void updateCurrentTitleBinding(Mp3FileData newTitle) {
        titleLabel.textProperty().bind(newTitle.titleProperty());
        albumLabel.textProperty().bind(newTitle.albumProperty());
        artistLabel.textProperty().bind(newTitle.artistProperty());
        totalTimeLabel.textProperty().bind(createTimeBinding(newTitle.durationProperty()));
        coverView.imageProperty().bind(new ObjectBinding<Image>() {
            {
                bind(newTitle.coverProperty());
            }

            @Override
            protected Image computeValue() {
                return ByteFormatter.byteArrayToImage(newTitle.getCover());
            }
        });
    }

    private StringBinding createTimeBinding(DoubleProperty property) {
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

    @FXML
    private void playPause() {
        player.playPause();
    }

    @FXML
    private void next() {
        player.next();
    }
    
    @FXML
    private void previous() {
        player.previous();
    }

    @FXML
    private void stop() {
        player.stop();
    }

    @FXML
    private void sliderPressed() {
        durationSlider.valueProperty().unbind();
    }

    @FXML
    private void sliderReleased() {
        player.seek(durationSlider.getValue() * player.getTotalTime());
        durationSlider.valueProperty().bind(durationSliderBinding);
    }
}
