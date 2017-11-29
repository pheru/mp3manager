package de.pheru.media.desktop;

import de.pheru.fx.mvp.PheruFXEntryPoint;
import de.pheru.fx.util.properties.ObservableProperties;
import de.pheru.media.desktop.cdi.qualifiers.Settings;
import de.pheru.media.desktop.gui.application.ApplicationView;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import java.io.IOException;

public class EntryPoint implements PheruFXEntryPoint {

    private static final Logger LOGGER = LogManager.getLogger(EntryPoint.class);

    @Inject
    @Settings
    private ObservableProperties settings;

    @Inject
    private ApplicationView applicationView;

    @Override
    public void start(Stage stage) throws Exception {
        try {
            initPrimaryStage(stage);
            stage.show();
        } catch (Exception e) {
            LOGGER.fatal("Exception initializing Application!", e);
            Alert alert = new Alert(Alert.AlertType.ERROR, "Fehler beim Starten der Anwendung!");
            alert.showAndWait();
            Platform.exit();
        }
    }

    private void initPrimaryStage(Stage primaryStage) {
        final Scene scene = new Scene(applicationView.getView());

        primaryStage.setTitle(DesktopApplication.APPLICATION_NAME);

        final BooleanProperty maximized = settings.booleanProperty(Setting.APPLICATIONWINDOW_MAXIMIZED);
        final DoubleProperty width = settings.doubleProperty(Setting.APPLICATIONWINDOW_WIDTH);
        final DoubleProperty height = settings.doubleProperty(Setting.APPLICATIONWINDOW_HEIGHT);

        primaryStage.setWidth(width.get());
        primaryStage.setHeight(height.get());
        primaryStage.setMaximized(maximized.get());

        maximized.bind(primaryStage.maximizedProperty());
        if (!primaryStage.isMaximized()) {
            width.bind(primaryStage.widthProperty());
            height.bind(primaryStage.heightProperty());
        }
        maximized.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                width.unbind();
                height.unbind();
            } else {
                width.bind(primaryStage.widthProperty());
                height.bind(primaryStage.heightProperty());
            }
        });
        primaryStage.setScene(scene);
    }

    @Override
    public void stop() throws Exception {
        LOGGER.info("Shutting down application...");
        try {
            settings.save(null);
            LOGGER.info("Settings saved.");
        } catch (final IOException e) {
            LOGGER.error("Exception trying to save the settings!", e);
            final Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Fehler beim Schließen der Anwendung");
            alert.setContentText("Einstellungen konnten nicht gespeichert werden!");
            alert.showAndWait();
        }
        LOGGER.info("Application has been shut down.");
    }
}
