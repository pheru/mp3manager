package de.pheru.media.desktop.cdi.producers;

import de.pheru.fx.mvp.qualifiers.PrimaryStage;
import de.pheru.fx.util.properties.ObservableProperties;
import de.pheru.media.desktop.DesktopApplication;
import de.pheru.media.desktop.cdi.qualifiers.Settings;
import de.pheru.media.desktop.cdi.qualifiers.StartFinishedActions;
import de.pheru.media.desktop.util.PrioritizedRunnable;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.SortedSet;

@Singleton // Singleton, da ObservableProperties nicht proxyable (kein no-arg Konstruktor)
public class ObservablePropertiesProducer {

    private static final Logger LOGGER = LogManager.getLogger(ObservablePropertiesProducer.class);

    private static final String FILENAME = "settings.properties";

    @Inject
    @PrimaryStage
    private Stage primaryStage;
    @Inject
    @StartFinishedActions
    private SortedSet<PrioritizedRunnable> startFinishedActions;

    @Produces
    @Settings
    @Singleton // Singleton, da ObservableProperties nicht proxyable (kein no-arg Konstruktor)
    public ObservableProperties settings() {
        final ObservableProperties settings = new ObservableProperties(DesktopApplication.APPLICATION_DATA_HOME + "/" + FILENAME);
        try {
            settings.load();
        } catch (final IOException e) {
            if (e instanceof FileNotFoundException) {
                LOGGER.info("No settings found.");
            } else {
                LOGGER.error("Exception loading settings!", e);
                startFinishedActions.add(new PrioritizedRunnable(
                        this::showAlert, PrioritizedRunnable.Priority.MEDIUM));
            }
        }
        return settings;
    }

    private void showAlert() {
        final Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText("Fehler beim Starten der Anwendung");
        alert.setContentText("Einstellungen konnten nicht geladen werden!");
        alert.show();
    }

}