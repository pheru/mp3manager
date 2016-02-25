package de.pheru.media.player;

import de.pheru.media.cdi.qualifiers.XMLSettings;
import de.pheru.media.data.Mp3FileData;
import de.pheru.media.data.Playlist;
import de.pheru.media.settings.Settings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.File;

/**
 * Player zum Abspielen von der MP3-Dateien.
 *
 * @author Philipp Bruckner
 */
@ApplicationScoped
public class MusicPlayer {

    private static final Logger LOGGER = LogManager.getLogger(MusicPlayer.class);

    private final DoubleProperty currentTime = new SimpleDoubleProperty(0.0);
    private final DoubleProperty totalTime = new SimpleDoubleProperty(0.0);
    private final ObjectProperty<MediaPlayer.Status> status = new SimpleObjectProperty<>(MediaPlayer.Status.UNKNOWN);

    @Inject
    @XMLSettings
    private Settings settings;
    @Inject
    private Playlist playlist;

    private MediaPlayer player;

    public void playPause() {
        if (player != null && player.getStatus() == MediaPlayer.Status.PLAYING) {
            player.pause();
        } else if (player != null && player.getStatus() == MediaPlayer.Status.PAUSED) {
            player.play();
        } else if (playlist.getCurrentTitle() != null) {
            play(playlist.getCurrentTitle());
        }
    }

    public void play(Integer index) {
        playlist.setCurrentTitleIndex(index);
        play(playlist.getTitles().get(index));
    }

    private void play(Mp3FileData mp3) {
        //Player in neuem Thread zur Performanceverbesserung und/oder Standbyproblematik (#75)?
        //new Thread(() -> {
        if (player != null) {
            player.stop();
        }
        File file = new File(mp3.getAbsolutePath());
        Media media = new Media(file.toURI().toString());
        player = new MediaPlayer(media);
        player.setOnEndOfMedia(() -> {
            if (!playlist.next() || settings.isMusicPlayerRepeat()) {
                play(playlist.getCurrentTitle());
            } else {
                player.stop();
            }
        });
        player.play();
        status.bind(player.statusProperty());
        totalTime.bind(mp3.durationProperty());
        currentTime.bind(new DoubleBinding() {
            {
                bind(player.currentTimeProperty());
            }

            @Override
            protected double computeValue() {
                return player.getCurrentTime().toSeconds();
            }
        });
        player.volumeProperty().bind(new DoubleBinding() {
            {
                bind(settings.musicPlayerVolumeProperty(), settings.musicPlayerMutedProperty());
            }

            @Override
            protected double computeValue() {
                if (settings.isMusicPlayerMuted()) {
                    return 0.0;
                }
                return settings.getMusicPlayerVolume() / 100.0;
            }
        });
        //}).start();
    }

    public void stop() {
        if (player != null) {
            player.stop();
        }
    }

    public void next() {
        playlist.next();
        if (player != null && player.getStatus() == MediaPlayer.Status.PLAYING) {
            play(playlist.getCurrentTitle());
        }
    }

    public void previous() {
        playlist.previous();
        if (player != null && player.getStatus() == MediaPlayer.Status.PLAYING) {
            play(playlist.getCurrentTitle());
        }
    }

    public void seek(double seconds) {
        player.seek(Duration.seconds(seconds));
    }

    public MediaPlayer.Status getStatus() {
        return status.get();
    }

    public void setStatus(final MediaPlayer.Status status) {
        this.status.set(status);
    }

    public ObjectProperty<MediaPlayer.Status> statusProperty() {
        return status;
    }

    public Double getCurrentTime() {
        return currentTime.get();
    }

    public void setCurrentTime(final Double currentTime) {
        this.currentTime.set(currentTime);
    }

    public DoubleProperty currentTimeProperty() {
        return currentTime;
    }

    public Double getTotalTime() {
        return totalTime.get();
    }

    public void setTotalTime(final Double totalTime) {
        this.totalTime.set(totalTime);
    }

    public DoubleProperty totalTimeProperty() {
        return totalTime;
    }
}
