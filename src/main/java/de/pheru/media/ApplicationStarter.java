package de.pheru.media;

import de.pheru.fx.controls.notification.NotificationManager;
import de.pheru.fx.mvp.StartApplication;
import de.pheru.media.cdi.qualifiers.XMLSettings;
import de.pheru.media.gui.applicationwindow.application.ApplicationView;
import de.pheru.media.settings.Settings;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
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

/**
 * @author Philipp Bruckner
 */
public class ApplicationStarter {

    private static final Logger LOGGER = LogManager.getLogger(ApplicationStarter.class);

    @Inject
    @XMLSettings
    private Settings settings;
    @Inject
    private GlobalKeyListener globalKeyListener;
    @Inject
    private ApplicationView applicationView;

    private void launchJavaFXApplication(@Observes @StartApplication Stage primaryStage) {
        try {
            initNotifications();
            if (settings.isShortcutsEnabled()) {
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
        NotificationManager.alignmentProperty().bind(settings.notificationsAlignmentProperty());
//        Notifications.defaultDurationProperty().bind(settings.notificationsTimerProperty()); //TODO Not.-Settings: Duration-Binding
    }

    private void initJNativeHook() {
        GlobalScreen.addNativeKeyListener(globalKeyListener); //TODO GlobalScreen kann Uns.LinkError werfen (v.a. wenn Anwendung mehrfach gestartet wird) Siehe #56
        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException e) {
            LOGGER.error("Exception initializing JNativeHook!", e);
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.getDialogPane().setPrefWidth(650.0);
            alert.setHeaderText("Shortcuts!"); //TODO Shortcuts-Alert: Header u. Content
            alert.setContentText("TODO");
            alert.showAndWait();
            settings.setShortcutsEnabled(false);
            settings.setShortcutsProhibited(true);
        }
    }

    private void initPrimaryStage(Stage primaryStage) {
        Scene scene = new Scene(applicationView.getView());

        primaryStage.setTitle(PheruMedia.APPLICATION_NAME);
        primaryStage.getIcons().add(new Image("img/trayIcon.png"));
        primaryStage.setWidth(settings.getApplicationWindowWidth());
        primaryStage.setHeight(settings.getApplicationWindowHeight());
        primaryStage.setMaximized(settings.isApplicationWindowMaximized());

        settings.applicationWindowMaximizedProperty().bind(primaryStage.maximizedProperty());
        if (!settings.isApplicationWindowMaximized()) {
            settings.applicationWindowWidthProperty().bind(primaryStage.widthProperty());
            settings.applicationWindowHeightProperty().bind(primaryStage.heightProperty());
        }
        settings.applicationWindowMaximizedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            if (newValue) {
                settings.applicationWindowWidthProperty().unbind();
                settings.applicationWindowHeightProperty().unbind();
            } else {
                settings.applicationWindowWidthProperty().bind(primaryStage.widthProperty());
                settings.applicationWindowHeightProperty().bind(primaryStage.heightProperty());
            }
        });
        primaryStage.setOnCloseRequest(new OnCloseRequestHandler());
        primaryStage.setScene(scene);
    }

    private class OnCloseRequestHandler implements EventHandler<WindowEvent> {

        @Override
        public void handle(WindowEvent event) {
            if (!settings.isDontShowAgainApplicationCloseDialog()) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setHeaderText(null);
                Label text = new Label(PheruMedia.APPLICATION_NAME + " wirklich schließen?");
                CheckBox rememberDecisionBox = new CheckBox("Diese Meldung nicht mehr anzeigen");
                rememberDecisionBox.selectedProperty().bindBidirectional(settings.dontShowAgainApplicationCloseDialogProperty());
                VBox content = new VBox(text, rememberDecisionBox);
                content.setSpacing(15);
                alert.getDialogPane().setContent(content);
                if (alert.showAndWait().get() != ButtonType.OK) {
                    event.consume();
                }
            }
        }
    }
}
