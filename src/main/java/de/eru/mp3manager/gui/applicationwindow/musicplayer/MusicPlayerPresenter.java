package de.eru.mp3manager.gui.applicationwindow.musicplayer;

import de.eru.mp3manager.player.MusicPlayer;
import de.eru.mp3manager.utils.TimeFormatter;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.IntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javax.annotation.PostConstruct;

public class MusicPlayerPresenter implements Initializable{

    @FXML
    private ProgressBar durationProgressBar;
    @FXML
    private Slider durationSlider;
    @FXML
    private Label currentTimeLabel;
    @FXML
    private Label totalTimeLabel;
    @FXML
    private ProgressBar volumeProgressBar;
    @FXML
    private Slider volumeSlider;
    @FXML
    private Label volumeLabel;

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

        volumeProgressBar.progressProperty().bind(volumeSlider.valueProperty());
        volumeLabel.textProperty().bind(new StringBinding() {
            {
                bind(volumeProgressBar.progressProperty());
            }

            @Override
            protected String computeValue() {
                Double progress = volumeProgressBar.progressProperty().get() * 100;
                return progress.intValue() + "%";
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
