package de.eru.mp3manager;

import com.melloware.jintellitype.JIntellitype;
import de.eru.mp3manager.cdi.XMLSettings;
import de.eru.mp3manager.settings.Settings;
import de.eru.pherufx.mvp.StartEvent;
import de.eru.pherufx.mvp.PheruFXApplication;
import java.util.logging.Level;
import static javafx.application.Application.launch;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javax.enterprise.util.AnnotationLiteral;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Application-Klasse als Startpunkt für die JavaFX-Anwendung.
 *
 * @author Philipp Bruckner
 */
public class Mp3Manager extends PheruFXApplication {

    public static final String APPLICATION_NAME = "Mp3Manager";
    private static final String CODE_SOURCE_LOCATION = Mp3Manager.class.getProtectionDomain().getCodeSource().getLocation().getPath();
    private static final boolean PRODUCTION_MODE = !CODE_SOURCE_LOCATION.endsWith("target/classes/");
    //TODO sollte einheitlich sein, was das "/" am ende betrifft
    public static final String APPLICATION_PATH = PRODUCTION_MODE
            ? CODE_SOURCE_LOCATION.substring(0, CODE_SOURCE_LOCATION.lastIndexOf("/app")) : CODE_SOURCE_LOCATION;
    public static final String DLL_PATH = PRODUCTION_MODE ? APPLICATION_PATH : CODE_SOURCE_LOCATION.replace("target/classes/", "");

    private static final java.util.logging.Logger JAUDIOTAGGER_LOGGER = java.util.logging.Logger.getLogger("org.jaudiotagger");

    private static final Logger LOGGER = LogManager.getLogger(Mp3Manager.class);

    private static Alert startAlert;
    private static boolean cleanedUp = false;

    public static void main(String[] args) {
        System.getProperties().put("logfiles.path", APPLICATION_PATH);;
        
        Thread.setDefaultUncaughtExceptionHandler((Thread t, Throwable e) -> {
            LOGGER.fatal("Unexpected Exception on Thread: " + t.getName(), e);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Unerwarteter Fehler!");
            alert.setContentText("Um weiteres unerwartetes Verhalten zu vermeiden, sollte die Anwendung neu gestartet werden.");
            if (Platform.isFxApplicationThread()) {
                alert.showAndWait();
            } else {
                Platform.runLater(alert::showAndWait);
            }
        });
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                cleanUp();
            }
        });
        JAUDIOTAGGER_LOGGER.setLevel(Level.WARNING);

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
    }

    @Override
    public void stop() throws Exception {
        //CleanUp muss hier ausgeführt werden, da ansonsten bei normalem Beenden der Anwendung 
        //das SystemTrayIcon nicht aufgeräumt wird und damit die Anwendung nicht stoppt.
        cleanUp();
    }

    public static void cleanUp() { //TODO Auf Thread achten bei Dialogen
        if (!cleanedUp) {
            Settings settings = getWeldContainer().instance().select(Settings.class, new AnnotationLiteral<XMLSettings>() {
            }).get();
            settings.save();
            if (settings.isJIntelliTypeEnabled()) {
                JIntellitype.getInstance().cleanUp();
            }
            getWeldContainer().instance().select(Mp3SystemTrayIcon.class).get().shutdown();
            cleanedUp = true;
        }
    }
}
