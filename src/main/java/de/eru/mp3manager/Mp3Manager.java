package de.eru.mp3manager;

import de.eru.pherufx.mvp.StartEvent;
import de.eru.pherufx.mvp.PheruFXApplication;
import java.util.logging.Level;
import java.util.logging.Logger;
import static javafx.application.Application.launch;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.jgroups.JChannel;

/**
 * Application-Klasse als Startpunkt für die JavaFX-Anwendung.
 *
 * @author Philipp Bruckner
 */
public class Mp3Manager extends PheruFXApplication {

    private static final String CODE_SOURCE_LOCATION = Mp3Manager.class.getProtectionDomain().getCodeSource().getLocation().getPath();
    
    private static final boolean PRODUCTION_MODE = !CODE_SOURCE_LOCATION.endsWith("target/classes/");
    
    //TODO sollte immer auf / enden
    public static final String APPLICATION_PATH = PRODUCTION_MODE ? 
            CODE_SOURCE_LOCATION.substring(0,CODE_SOURCE_LOCATION.lastIndexOf("/app")) : CODE_SOURCE_LOCATION;
    public static final String DLL_PATH = PRODUCTION_MODE ? APPLICATION_PATH : CODE_SOURCE_LOCATION.replace("target/classes/", "");

    private static final Logger JAUDIOTAGGER_LOGGER = Logger.getLogger("org.jaudiotagger");

    public static JChannel channel;
    private static Alert startAlert;

    public static void main(String[] args) {
        //TODO Channel
//        try {
//            channel = new JChannel();
//            channel.connect("mp3manager");
//            if(channel.getView().getMembers().size() > 1){
//                channel.send(new Message(null, args));
//                channel.close();
//                System.exit(0);
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
        setOnStarting((StartEvent event) -> {
            startAlert = new Alert(Alert.AlertType.NONE);
            startAlert.setResult(ButtonType.CLOSE);
            startAlert.setTitle("Mp3Manager");
            startAlert.setContentText("Starte Mp3Manager...");
//            startAlert.setGraphic(new ImageView("img/clock_48.png"));
            startAlert.show();
        });
        setOnStartFinished((StartEvent event) -> {
            startAlert.hide();
        });
        JAUDIOTAGGER_LOGGER.setLevel(Level.WARNING);
        launch(args);
    }
}
