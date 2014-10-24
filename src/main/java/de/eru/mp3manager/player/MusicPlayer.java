package de.eru.mp3manager.player;

import de.eru.mp3manager.Settings;
import de.eru.mp3manager.data.Mp3FileData;
import de.eru.mp3manager.data.Playlist;
import java.io.File;
import java.net.MalformedURLException;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * Player zum Abspielen von Musik.
 *
 * @author Philipp Bruckner
 */
@ApplicationScoped
public class MusicPlayer {

    private final IntegerProperty currentTime = new SimpleIntegerProperty(88);
    private final IntegerProperty totalTime = new SimpleIntegerProperty(144);
    private final DoubleProperty volume = new SimpleDoubleProperty(100.0);
    private final BooleanProperty repeat = new SimpleBooleanProperty(false);
    private final BooleanProperty random = new SimpleBooleanProperty(false);
    private final ObjectProperty<Status> status = new SimpleObjectProperty<>(Status.UNKNOWN); //TODO ALternative?

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
        } else if (!playlist.getTitles().isEmpty()) {
            play(playlist.getTitles().get(playlist.getCurrentIndex()));
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
            status.bind(player.statusProperty());
            //TODO...
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
//        play(playlist.getNextTitle());
    }

    public void previous() {
//        play(playlist.getPreviousTitle());
    }

    public IntegerProperty currentTimeProperty() {
        return currentTime;
    }

    public IntegerProperty totalTimeProperty() {
        return totalTime;
    }

    public BooleanProperty repeatProperty() {
        return repeat;
    }

    public BooleanProperty randomProperty() {
        return random;
    }
}
