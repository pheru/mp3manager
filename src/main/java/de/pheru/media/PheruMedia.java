package de.pheru.media;

import de.pheru.fx.mvp.PheruFXApplication;
import de.pheru.media.cdi.qualifiers.XMLSettings;
import de.pheru.media.settings.Settings;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;

import javax.enterprise.util.AnnotationLiteral;
import java.util.logging.Level;

/**
 * Application-Klasse als Startpunkt für die JavaFX-Anwendung.
 *
 * @author Philipp Bruckner
 */
public class PheruMedia extends PheruFXApplication {

    private static final String CODE_SOURCE_LOCATION = PheruMedia.class.getProtectionDomain().getCodeSource().getLocation().getPath();
    private static final boolean PRODUCTION_MODE = !CODE_SOURCE_LOCATION.endsWith("target/classes/");

    public static final String APPLICATION_NAME = "Pheru Media";
    //TODO APPLICATION_PATH: einheitlich bzgl "/" am ende 
    public static final String APPLICATION_PATH = PRODUCTION_MODE
            ? CODE_SOURCE_LOCATION.substring(0, CODE_SOURCE_LOCATION.lastIndexOf("/app")) : CODE_SOURCE_LOCATION;

    private static final Logger LOGGER = createLogger(); //Muss nach APPLICATION_PATH initialisiert werden

    private static Alert startAlert;
    private static boolean cleanedUp = false;

    private static Logger createLogger() {
        System.getProperties().put("logfiles.location", APPLICATION_PATH);
        return LogManager.getLogger(PheruMedia.class);
    }

    public static void main(String[] args) {
        setUpLogging();
        LOGGER.info("Starte " + APPLICATION_NAME + "...");

        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                cleanUp();
            }
        });
        launch(args);
    }

    private static void setUpLogging() {
//        java.util.logging.Logger l = java.util.logging.Logger.getLogger(GlobalScreen.class.getPackage().getName());
//        l.setLevel(Level.WARNING);

        java.util.logging.LogManager.getLogManager().reset();
//        java.util.logging.Logger.getLogger("org.jboss.weld").setLevel(Level.WARNING);
        java.util.logging.Logger.getLogger("org.jaudiotagger").setLevel(Level.WARNING);
        java.util.logging.Logger.getLogger("org.jnativehook").setLevel(Level.WARNING);
    }

    @Override
    public void beforeStart() {
        //TODO Stage statt alert
        startAlert = new Alert(Alert.AlertType.NONE);
        startAlert.setResult(ButtonType.CLOSE);
        startAlert.setTitle(APPLICATION_NAME);
        startAlert.setContentText("Starte " + APPLICATION_NAME + "...");
        startAlert.show();
//        Stage stage = new Stage();//(Stage) startAlert.getDialogPane().getScene().getWindow();
//        stage.getIcons().add(new Image("img/trayIcon.png"));
    }

    @Override
    public void startFinished() {
        startAlert.hide();
    }

    @Override
    public void stop() throws Exception {
        //CleanUp muss hier ausgeführt werden, da ansonsten bei normalem Beenden der Anwendung 
        //das SystemTrayIcon nicht aufgeräumt wird und damit die Anwendung nicht stoppt.
        //TODO cleanup: kann nun evtl. aus stop() raus ///falsch, jnativehook läuft noch
        cleanUp();
    }

    public static void cleanUp() {
        if (!cleanedUp) {
            Settings settings = getWeldContainer().instance().select(Settings.class, new AnnotationLiteral<XMLSettings>() {
            }).get();
            settings.save();
            try {
                GlobalScreen.unregisterNativeHook();
            } catch (NativeHookException e) {
                LOGGER.error("Exception cleaning up JNativeHook!", e);
            }
            cleanedUp = true;
        }
    }

    private static class UncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

        @Override
        public void uncaughtException(Thread t, Throwable e) {
            LOGGER.fatal("Unexpected Exception!", e);
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Unerwarteter Fehler!");
                alert.setContentText("Um weiteres unerwartetes Verhalten zu vermeiden, sollte die Anwendung neu gestartet werden.");
                alert.showAndWait();
            });
        }
    }
}
