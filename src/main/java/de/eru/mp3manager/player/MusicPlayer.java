package de.eru.mp3manager.player;

import de.eru.mp3manager.Settings;
import de.eru.mp3manager.cdi.CurrentTitleEvent;
import de.eru.mp3manager.cdi.Updated;
import de.eru.mp3manager.data.Mp3FileData;
import de.eru.mp3manager.data.Playlist;
import java.io.File;
import java.net.MalformedURLException;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

/**
 * Player zum Abspielen von der MP3-Dateien.
 *
 * @author Philipp Bruckner
 */
@ApplicationScoped
public class MusicPlayer {

    private final DoubleProperty currentTime = new SimpleDoubleProperty(0.0);
    private final DoubleProperty totalTime = new SimpleDoubleProperty(0.0);
    private final DoubleProperty volume = new SimpleDoubleProperty(100.0);
    private final BooleanProperty repeat = new SimpleBooleanProperty(false);
    private final BooleanProperty random = new SimpleBooleanProperty(false);
    private final ObjectProperty<MediaPlayer.Status> status = new SimpleObjectProperty<>(MediaPlayer.Status.UNKNOWN);

    @Inject
    private Settings settings;
    @Inject
    private Playlist playlist;

    private MediaPlayer player;

    @PostConstruct
    private void init() {
        repeat.bindBidirectional(settings.musicPlayerRepeatProperty());
        random.bindBidirectional(settings.musicPlayerRandomProperty());
        volume.bindBidirectional(settings.musicPlayerVolumeProperty());
    }

    public void playPause() {
        if (player != null && player.getStatus() == MediaPlayer.Status.PLAYING) {
            player.pause();
        } else if (player != null && player.getStatus() == MediaPlayer.Status.PAUSED) {
            player.play();
        } else if (playlist.getCurrentTitle() != null) {
            play(playlist.getCurrentTitle());
        }
        //TODO Meldung ausgeben, wenn player == null und playlist leer?
    }

    private void play(Mp3FileData mp3) {
        if (player != null) {
            player.stop();
        }
        File file = new File(mp3.getAbsolutePath());
        try {
            Media media = new Media(file.toURI().toURL().toExternalForm());
            player = new MediaPlayer(media);
            player.setOnEndOfMedia(() -> { //TODO überprüfen
                if (!playlist.next() || repeat.get()) {
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
                    System.out.println(player.getCurrentTime().toSeconds() + " - " + player.getTotalDuration().toSeconds());
                    return player.getCurrentTime().toSeconds();
                }
            });
            player.volumeProperty().bind(volume.divide(100.0));
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }
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

    private void currentTitleUpdated(@Observes @Updated CurrentTitleEvent event) {
        if (player != null && player.getStatus() == MediaPlayer.Status.PLAYING) {
            play(event.getNewCurrentTitle());
        }
    }

    public BooleanProperty repeatProperty() {
        return repeat;
    }

    public BooleanProperty randomProperty() {
        return random;
    }

    public Double getVolume() {
        return volume.get();
    }

    public void setVolume(final Double volume) {
        this.volume.set(volume);
    }

    public DoubleProperty volumeProperty() {
        return volume;
    }

    public Boolean isRepeat() {
        return repeat.get();
    }

    public void setRepeat(final Boolean repeat) {
        this.repeat.set(repeat);
    }

    public Boolean isRandom() {
        return random.get();
    }

    public void setRandom(final Boolean random) {
        this.random.set(random);
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
