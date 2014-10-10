package de.eru.mp3manager.player;

import de.eru.mp3manager.Settings;
import de.eru.mp3manager.data.Playlist;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.media.MediaPlayer;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * Player zum Abspielen von Musik.
 *
 * @author Philipp Bruckner
 */
public class MusicPlayer {

    private final IntegerProperty currentTime = new SimpleIntegerProperty(88);
    private final IntegerProperty totalTime = new SimpleIntegerProperty(144);
    private final DoubleProperty volume = new SimpleDoubleProperty(100.0);
    private final BooleanProperty repeat = new SimpleBooleanProperty(false);
    private final BooleanProperty random = new SimpleBooleanProperty(false);

    @Inject
    private Settings settings;
    @Inject
    private Playlist playlist;

    private MediaPlayer player;

    @PostConstruct
    private void init(){
        repeat.bindBidirectional(settings.musicPlayerRepeatProperty());//TODO Bidirectional wirklich nötig?
        random.bindBidirectional(settings.musicPlayerRandomProperty());
        volume.bindBidirectional(settings.musicPlayerVolumeProperty());
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
