package de.pheru.media.desktop;

import de.pheru.fx.mvp.PheruFXApplication;
import de.pheru.fx.mvp.PheruFXEntryPoint;
import de.pheru.fx.mvp.PheruFXLoader;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.logging.Level;

public class DesktopApplication extends PheruFXApplication {

    private static final Logger LOGGER = LogManager.getLogger(DesktopApplication.class);

    public static final String APPLICATION_NAME = "Pheru Media";
    // Bei Aenderungen auch log4j2.xml anpassen
    public static final String APPLICATION_DATA_HOME = System.getProperty("user.home") + "/.pherumedia";

    public static void main(final String[] args) {
        LOGGER.info("Application started.");
        setUpLogging();
        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());
        launch(args);
    }

    private static void setUpLogging() {
        java.util.logging.LogManager.getLogManager().reset();
        java.util.logging.Logger.getLogger("org.jaudiotagger").setLevel(Level.WARNING);
        java.util.logging.Logger.getLogger("org.jnativehook").setLevel(Level.WARNING);
    }

    @Override
    protected Class<? extends PheruFXEntryPoint> getEntryPointClass() {
        return EntryPoint.class;
    }

    @Override
    protected Class<? extends PheruFXLoader> getLoaderClass() {
        return DesktopApplicationLoader.class;
    }

    @Override
    protected Stage createSplashStage() {
        return new SplashStage();
    }

    private static class UncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

        @Override
        public void uncaughtException(final Thread t, final Throwable e) {
            LOGGER.fatal("Unexpected Exception!", e);
            Platform.runLater(() -> {
                final Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Unerwarteter Fehler!");
                alert.setContentText("Um weiteres unerwartetes Verhalten zu vermeiden, starten Sie die Anwendung neu.");
                alert.showAndWait();
            });
        }
    }
}
