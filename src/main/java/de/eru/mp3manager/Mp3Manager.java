package de.eru.mp3manager;

import com.melloware.jintellitype.JIntellitype;
import de.eru.mp3manager.cdi.XMLSettings;
import de.eru.mp3manager.settings.Settings;
import de.eru.pherufx.mvp.StartEvent;
import de.eru.pherufx.mvp.PheruFXApplication;
import java.util.logging.Level;
import java.util.logging.Logger;
import static javafx.application.Application.launch;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javax.enterprise.util.AnnotationLiteral;

/**
 * Application-Klasse als Startpunkt für die JavaFX-Anwendung.
 *
 * @author Philipp Bruckner
 */
public class Mp3Manager extends PheruFXApplication {

    private static final String CODE_SOURCE_LOCATION = Mp3Manager.class.getProtectionDomain().getCodeSource().getLocation().getPath();

    private static final boolean PRODUCTION_MODE = !CODE_SOURCE_LOCATION.endsWith("target/classes/");

    //TODO sollte einheitlich sein, was das "/" am ende betrifft
    public static final String APPLICATION_PATH = PRODUCTION_MODE
            ? CODE_SOURCE_LOCATION.substring(0, CODE_SOURCE_LOCATION.lastIndexOf("/app")) : CODE_SOURCE_LOCATION;
    public static final String DLL_PATH = PRODUCTION_MODE ? APPLICATION_PATH : CODE_SOURCE_LOCATION.replace("target/classes/", "");

    private static final Logger JAUDIOTAGGER_LOGGER = Logger.getLogger("org.jaudiotagger");

    public static final String APPLICATION_NAME = "Mp3Manager";

    private static Alert startAlert;

    private static boolean cleanedUp = false;

    public static void main(String[] args) {
        JAUDIOTAGGER_LOGGER.setLevel(Level.WARNING);
        //TODO auch für 32 bit
        JIntellitype.setLibraryLocation(Mp3Manager.DLL_PATH + "/JIntellitype64.dll");
        
        setOnStarting((StartEvent event) -> {
            startAlert = new Alert(Alert.AlertType.NONE);
            startAlert.setResult(ButtonType.CLOSE);
            startAlert.setTitle(APPLICATION_NAME);
            startAlert.setContentText("Starte " + APPLICATION_NAME + "...");
//            startAlert.setGraphic(new ImageView("img/clock_48.png"));
            startAlert.show();
        });
        setOnStartFinished((StartEvent event) -> {
            startAlert.hide();
        });
        launch(args);

        cleanUp();
    }

    public static void cleanUp() {
        if (!cleanedUp) {
            JIntellitype.getInstance().cleanUp();
            getWeldContainer().instance().select(Settings.class, new AnnotationLiteral<XMLSettings>() {
            }).get().save();
            getWeldContainer().instance().select(Mp3SystemTrayIcon.class).get().shutdown();
            cleanedUp = true;
        }
    }
}