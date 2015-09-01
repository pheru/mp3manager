package de.pheru.media.gui.applicationwindow.musicplayer;

import de.pheru.media.cdi.qualifiers.XMLSettings;
import de.pheru.media.data.Mp3FileData;
import de.pheru.media.data.Playlist;
import de.pheru.media.player.MusicPlayer;
import de.pheru.media.settings.Settings;
import de.pheru.media.util.ByteUtil;
import de.pheru.media.util.TimeUtil;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.media.MediaPlayer;
import javax.enterprise.context.ApplicationScoped;
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
    private ImageView volumeImageView;
    @FXML
    private Button playButton;
    @FXML
    private ImageView playButtonImageView;
    @FXML
    private ToggleButton randomButton;
    @FXML
    private ToggleButton repeatButton;

    @Inject
    @XMLSettings
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

        randomButton.selectedProperty().bindBidirectional(settings.musicPlayerRandomProperty());
        repeatButton.selectedProperty().bindBidirectional(settings.musicPlayerRepeatProperty());

        volumeSlider.valueProperty().bindBidirectional(settings.musicPlayerVolumeProperty());
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
        volumeImageView.imageProperty().bind(new ObjectBinding<Image>() {
            {
                bind(volumeProgressBar.progressProperty(), settings.musicPlayerMutedProperty());
            }

            @Override
            protected Image computeValue() {
                String s = "0";
                if (settings.isMusicPlayerMuted()) {
                    s = "x";
                    volumeProgressBar.setStyle("-fx-accent: grey;");
                } else {
                    volumeProgressBar.setStyle(null);
                    if (volumeProgressBar.getProgress() > 0.66) {
                        s = "3";
                    } else if (volumeProgressBar.getProgress() > 0.33) {
                        s = "2";
                    } else if (volumeProgressBar.getProgress() >= 0.01) {
                        s = "1";
                    }
                }
                return new Image("img/musicPlayer/player_volume_" + s + ".png");
            }
        });
        playButtonImageView.imageProperty().bind(new ObjectBinding<Image>() {
            {
                bind(player.statusProperty());
            }

            @Override
            protected Image computeValue() {
                String button = "play";
                if (player.getStatus() == MediaPlayer.Status.PLAYING) {
                    button = "pause";
                }
                return new Image("img/musicPlayer/player_" + button + ".png");
            }
        });
//        playlist.currentTitleIndexProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
//            updateCurrentTitleBinding(newValue.intValue());
//        });
        playlist.currentTitleIndexProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            Mp3FileData bindingData = playlist.getCurrentTitle() != null
                    ? playlist.getCurrentTitle() : Mp3FileData.PLACEHOLDER_DATA;
            updateCurrentTitleBinding(bindingData);
        });
    }

    private void updateCurrentTitleBinding(Mp3FileData newTitle) {
        titleLabel.textProperty().bind(newTitle.titleProperty());
        albumLabel.textProperty().bind(newTitle.albumProperty());
        artistLabel.textProperty().bind(newTitle.artistProperty());
        totalTimeLabel.textProperty().bind(createTimeBinding(newTitle.durationProperty()));
        coverView.imageProperty().bind(new ObjectBinding<Image>() {
            {
                bind(newTitle.artworkDataProperty());
            }

            @Override
            protected Image computeValue() {
                if (newTitle.getArtworkData() == null) {
                    return null;
                }
                return ByteUtil.byteArrayToImage(newTitle.getArtworkData().getBinaryData());
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
                return TimeUtil.secondsToDurationFormat(property.get(), false);
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
    private void durationSliderMousePressed() {
        durationSlider.valueProperty().unbind();
    }

    @FXML
    private void durationSliderMouseReleased() {
        player.seek(durationSlider.getValue() * player.getTotalTime());
        durationSlider.valueProperty().bind(durationSliderBinding);
    }

    @FXML
    private void volumeSliderKeyPressed(KeyEvent event) {
        double volume = settings.getMusicPlayerVolume();
        if (event.getCode() == KeyCode.RIGHT || event.getCode() == KeyCode.UP) {
            if (volume <= 99.0) {
                settings.setMusicPlayerVolume(volume + 1.0);
            } else {
                settings.setMusicPlayerVolume(100.0);
            }
            event.consume();
        } else if (event.getCode() == KeyCode.LEFT || event.getCode() == KeyCode.DOWN) {
            if (volume >= 1.0) {
                settings.setMusicPlayerVolume(volume - 1.0);
            } else {
                settings.setMusicPlayerVolume(0.0);
            }
            event.consume();
        }
    }

    @FXML
    private void volumeImageClicked() {
        settings.setMusicPlayerMuted(!settings.isMusicPlayerMuted());
    }
}
