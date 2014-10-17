package de.eru.mp3manager;

import de.eru.pherufx.cdi.PheruFXApplication;
import java.util.logging.Level;
import java.util.logging.Logger;
import static javafx.application.Application.launch;

/**
 * Application-Klasse als Startpunkt für die JavaFX-Anwendung.
 *
 * @author Philipp Bruckner
 */
public class Mp3Manager extends PheruFXApplication {

    private static final Logger JAUDIOTAGGER_LOGGER = Logger.getLogger("org.jaudiotagger");

    public static void main(String[] args) {
        JAUDIOTAGGER_LOGGER.setLevel(Level.WARNING);
        launch(args);
    }
}
