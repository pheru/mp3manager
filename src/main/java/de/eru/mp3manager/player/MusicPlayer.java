package de.eru.mp3manager.player;

import de.eru.mp3manager.data.Playlist;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.media.MediaPlayer;
import javax.inject.Inject;

/**
 * Player zum Abspielen von Musik.
 *
 * @author Philipp Bruckner
 */
public class MusicPlayer {

    private final IntegerProperty currentTime = new SimpleIntegerProperty(88);
    private final IntegerProperty totalTime = new SimpleIntegerProperty(144);
    private final BooleanProperty repeat = new SimpleBooleanProperty(false);
    private final BooleanProperty random = new SimpleBooleanProperty(false);

    @Inject
    private Playlist playlist;

    private MediaPlayer player;

    public MusicPlayer() {
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
