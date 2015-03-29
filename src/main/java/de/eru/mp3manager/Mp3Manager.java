package de.eru.mp3manager;

import de.eru.pherufx.mvp.PheruFXApplication;
import java.util.logging.Level;
import java.util.logging.Logger;
import static javafx.application.Application.launch;
import org.jgroups.JChannel;
import org.jgroups.Message;

/**
 * Application-Klasse als Startpunkt für die JavaFX-Anwendung.
 *
 * @author Philipp Bruckner
 */
public class Mp3Manager extends PheruFXApplication {

    private static final String JAR_PATH = Mp3Manager.class.getProtectionDomain().getCodeSource().getLocation().getPath();
    //TODO Geht das auch in schön?
    public static final String APPLICATION_PATH = JAR_PATH.substring(0,
            JAR_PATH.lastIndexOf("/app") >= 0 ? JAR_PATH.lastIndexOf("/app") : JAR_PATH.length());

    private static final Logger JAUDIOTAGGER_LOGGER = Logger.getLogger("org.jaudiotagger");

    public static JChannel channel;

    public static void main(String[] args) {
        //TODO eventuell in ApplicationStarter um Dialog zu zeigen oder Anwendung in den Vordergrund zu holen
        try {
            channel = new JChannel();
            channel.connect("de.eru.mp3manager");
            if (channel.getView().getMembers().size() > 1) {
                if (args.length > 0) {
                    channel.send(new Message(null, args[0]));
                }
                channel.close();
                System.exit(0);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        JAUDIOTAGGER_LOGGER.setLevel(Level.WARNING);
        launch(args);
    }
}
