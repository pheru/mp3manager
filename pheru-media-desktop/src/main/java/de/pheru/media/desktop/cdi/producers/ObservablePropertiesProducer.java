package de.pheru.media.desktop.cdi.producers;

import de.pheru.fx.mvp.qualifiers.PrimaryStage;
import de.pheru.fx.util.properties.ObservableProperties;
import de.pheru.media.desktop.DesktopApplication;
import de.pheru.media.desktop.cdi.qualifiers.Settings;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.FileNotFoundException;
import java.io.IOException;

@Singleton // Singleton, da ObservableProperties nicht proxyable (kein no-arg Konstruktor)
public class ObservablePropertiesProducer {

    private static final Logger LOGGER = LogManager.getLogger(ObservablePropertiesProducer.class);

    private static final String FILENAME = "settings.properties";

    @Inject
    @PrimaryStage
    private Stage primaryStage;

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
                showAlert();
            }
        }
        return settings;
    }

    private void showAlert() {
        Platform.runLater(() -> {
            final Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("Fehler beim Starten der Anwendung");
            alert.setContentText("Einstellungen konnten nicht geladen werden!");

            // Die primaryStage ist normalerweise beim Laden der Einstellungen noch nicht sichtbar,
            // daher muss ggfs. darauf gewartet werden
            if (primaryStage.isShowing()) {
                alert.show();
            } else {
                primaryStage.showingProperty().addListener(new ChangeListener<Boolean>() {
                    @Override
                    public void changed(final ObservableValue<? extends Boolean> observable, final Boolean oldValue, final Boolean newValue) {
                        alert.show();
                        primaryStage.showingProperty().removeListener(this);
                    }
                });
            }
        });
    }

}