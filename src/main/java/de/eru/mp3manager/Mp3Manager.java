package de.eru.mp3manager;

import de.eru.pherufx.cdi.PheruFXApplication;
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

    private static final Logger JAUDIOTAGGER_LOGGER = Logger.getLogger("org.jaudiotagger");

    public static JChannel CHANNEL;
    
    public static void main(String[] args) {
        //TODO Channel
//        try {
//            CHANNEL = new JChannel();
//            CHANNEL.connect("mp3manager");
//            if(CHANNEL.getView().getMembers().size() > 1){
//                CHANNEL.send(new Message(null, args));
//                CHANNEL.close();
//                System.exit(0);
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
        JAUDIOTAGGER_LOGGER.setLevel(Level.WARNING);
        launch(args);
    }
}
