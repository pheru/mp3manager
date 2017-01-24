package de.pheru.media.gui;

import de.pheru.fx.mvp.StartApplication;
import de.pheru.fx.util.properties.ObservableProperties;
import de.pheru.media.gui.applicationwindow.application.ApplicationView;
import de.pheru.media.util.GlobalKeyListener;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

public class ApplicationStarter {

    private static final Logger LOGGER = LogManager.getLogger(ApplicationStarter.class);

    @Inject
    private ObservableProperties settings;
    @Inject
    private GlobalKeyListener globalKeyListener;
    @Inject
    private ApplicationView applicationView;

    private void launchJavaFXApplication(@Observes @StartApplication Stage primaryStage) {
        try {
            initNotifications();
            if (settings.booleanProperty(Settings.SHORTCUTS_ENABLED).get()) {
                initJNativeHook();
            }
            initPrimaryStage(primaryStage);
            primaryStage.show();
        } catch (Exception e) {
            LOGGER.fatal("Exception initializing Application!", e);
            Alert alert = new Alert(Alert.AlertType.ERROR, "Fehler beim Starten der Anwendung!");
            alert.showAndWait();
            Platform.exit();
        }
    }

    private void initNotifications() {
        //TODO
//        Notification.getDefaults().positionProperty().bind(settings.notificationsPositionProperty());
//        Notifications.defaultDurationProperty().bind(settings.notificationsDurationProperty());
    }

    private void initJNativeHook() {
        try {
            GlobalScreen.addNativeKeyListener(globalKeyListener);
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException | UnsatisfiedLinkError e) {
            LOGGER.error("Exception/Error initializing JNativeHook!", e);
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.getDialogPane().setPrefWidth(650.0);
            alert.setHeaderText("Shortcuts konnten nicht initialisiert werden!");
            alert.setContentText("Shortcuts werden deaktiviert.");
            alert.showAndWait();
            settings.booleanProperty(Settings.SHORTCUTS_ENABLED).set(false);
        }
    }

    private void initPrimaryStage(Stage primaryStage) {
        Scene scene = new Scene(applicationView.getView());

        primaryStage.setTitle(PheruMedia.APPLICATION_NAME);
        primaryStage.getIcons().addAll(new Image(PheruMedia.APPLICATION_ICON_PATH_64),
                new Image(PheruMedia.APPLICATION_ICON_PATH_48),
                new Image(PheruMedia.APPLICATION_ICON_PATH_32));

        final BooleanProperty maximized = settings.booleanProperty(Settings.APPLICATIONWINDOW_MAXIMIZED);
        final DoubleProperty width = settings.doubleProperty(Settings.APPLICATIONWINDOW_WIDTH);
        final DoubleProperty height = settings.doubleProperty(Settings.APPLICATIONWINDOW_HEIGHT);

        primaryStage.setWidth(width.get());
        primaryStage.setHeight(height.get());
        primaryStage.setMaximized(maximized.get());

        maximized.bind(primaryStage.maximizedProperty());
        if (!maximized.get()) {
            width.bind(primaryStage.widthProperty());
            height.bind(primaryStage.heightProperty());
        }
        primaryStage.setOnCloseRequest(new OnCloseRequestHandler());
        primaryStage.setScene(scene);
    }

    private class OnCloseRequestHandler implements EventHandler<WindowEvent> {

        @Override
        public void handle(WindowEvent event) {
            final BooleanProperty dontShowAgain = settings.booleanProperty(Settings.DONT_SHOW_AGAIN_CLOSE_APPLICATION_DIALOG);
            if (!dontShowAgain.get()) {
                final Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setHeaderText(null);
                final Label text = new Label(PheruMedia.APPLICATION_NAME + " wirklich schließen?");
                final CheckBox dontShowAgainBox = new CheckBox("Diese Meldung nicht mehr anzeigen");
                dontShowAgainBox.selectedProperty().bindBidirectional(dontShowAgain);
                final VBox content = new VBox(text, dontShowAgainBox);
                content.setSpacing(15);
                alert.getDialogPane().setContent(content);
                if (alert.showAndWait().get() != ButtonType.OK) {
                    event.consume();
                }
            }
        }
    }
}
