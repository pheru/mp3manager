package de.pheru.media.gui;

import de.pheru.fx.mvp.PheruFXApplication;
import de.pheru.fx.util.properties.ObservableProperties;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;

import java.io.IOException;
import java.util.logging.Level;

/**
 * Application-Klasse als Startpunkt für die JavaFX-Anwendung.
 */
public class PheruMedia extends PheruFXApplication {

    private static final String CODE_SOURCE_LOCATION = PheruMedia.class.getProtectionDomain().getCodeSource().getLocation().getPath();
    private static final boolean PRODUCTION_MODE = !CODE_SOURCE_LOCATION.endsWith("target/classes/");

    public static final String APPLICATION_NAME = "Pheru Media";
    public static final String APPLICATION_ICON_PATH_64 = "img/pm64x64.png";
    public static final String APPLICATION_ICON_PATH_48 = "img/pm48x48.png";
    public static final String APPLICATION_ICON_PATH_32 = "img/pm32x32.png";
    public static final String APPLICATION_PATH = PRODUCTION_MODE
            ? CODE_SOURCE_LOCATION.substring(0, CODE_SOURCE_LOCATION.lastIndexOf("app/")) : CODE_SOURCE_LOCATION;

    private static final Logger LOGGER = createLogger(); //Muss nach APPLICATION_PATH initialisiert werden

    private static Stage splashStage;

    private static Logger createLogger() {
        System.getProperties().put("logfiles.location", APPLICATION_PATH);
        return LogManager.getLogger(PheruMedia.class);
    }

    public static void main(String[] args) {
        setUpLogging();
        LOGGER.info("Starte " + APPLICATION_NAME + "...");

        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());
        launch(args);
    }

    private static void setUpLogging() {
        java.util.logging.LogManager.getLogManager().reset();
        java.util.logging.Logger.getLogger("org.jaudiotagger").setLevel(Level.WARNING);
        java.util.logging.Logger.getLogger("org.jnativehook").setLevel(Level.WARNING);
    }

    @Override
    public void beforeStart() {
        splashStage = new Stage(StageStyle.TRANSPARENT);
        splashStage.setTitle(APPLICATION_NAME);
        splashStage.getIcons().addAll(new Image(APPLICATION_ICON_PATH_64),
                new Image(APPLICATION_ICON_PATH_48),
                new Image(APPLICATION_ICON_PATH_32));
        AnchorPane pane = new AnchorPane(new ImageView(new Image(APPLICATION_ICON_PATH_64)));
        Scene scene = new Scene(pane);
        scene.setFill(Color.TRANSPARENT);
        splashStage.setScene(scene);
        splashStage.show();
    }

    @Override
    public void afterStart() {
        splashStage.hide();
    }

    @Override
    public void stop() throws Exception {
        //CleanUp muss hier ausgeführt werden, da ansonsten bei normalem Beenden der Anwendung 
        //der jnativehook nicht entfernt wird und damit die Anwendung nicht stoppt.
        cleanUp();
    }

    public static void cleanUp() {
        final ObservableProperties settings = getWeldContainer().instance().select(ObservableProperties.class).get();
        try {
            settings.save(null);
        } catch (IOException e1) {
            LOGGER.warn("Exception saving settings. Trying again.", e1);
            try { //nochmal versuchen
                settings.save(null);
            } catch (IOException e2) {
                LOGGER.error("Exception saving settings.", e2);
                final Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Einstellungen konnten nicht gespeichert werden!");
                alert.showAndWait();
            }
        }
        try {
            GlobalScreen.unregisterNativeHook();
        } catch (NativeHookException e) {
            LOGGER.error("Exception cleaning up JNativeHook!", e);
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
