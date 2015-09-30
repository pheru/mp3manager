package de.pheru.media;

import com.melloware.jintellitype.JIntellitype;
import com.melloware.jintellitype.JIntellitypeException;
import de.pheru.fx.controls.notification.Notifications;
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
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Philipp Bruckner
 */
public class ApplicationStarter {

    private static final Logger LOGGER = LogManager.getLogger(ApplicationStarter.class);

    @Inject
    @XMLSettings
    private Settings settings;
    @Inject
    private ApplicationView applicationView;

    private void launchJavaFXApplication(@Observes @StartApplication Stage primaryStage) {
        try {
            initNotifications();
            initJIntelliType();
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
        Notifications.alignmentProperty().bind(settings.notificationsAlignmentProperty());
        Notifications.defaultTimerProperty().bind(settings.notificationsTimerProperty());
    }

    private void initJIntelliType() {
        String dll = is64BitOS() ? "/JIntellitype64.dll" : "/JIntellitype.dll";
        JIntellitype.setLibraryLocation(PheruMedia.DLL_PATH + dll);
        try {
            JIntellitype.getInstance();
        } catch (JIntellitypeException e) {
            LOGGER.error("Exception initializing JIntelliType!", e);
            if (settings.isJIntelliTypeEnabled()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.getDialogPane().setPrefWidth(650.0);
                alert.setHeaderText("Initialisierung von JIntelliType fehlgeschlagen!");
                alert.setContentText("Stellen Sie sicher, dass sich unter \n\"" + PheruMedia.DLL_PATH
                        + "\"\n die Dateien \"JIntellitype.dll\" und \"JIntellitype64.dll\" befindet.\n\n"
                        + "Shortcuts wurden deaktiviert.");
                alert.showAndWait();
            }
            settings.setJIntelliTypeProhibited(true);
            settings.setJIntelliTypeEnabled(false);
        }
    }

    private boolean is64BitOS() {
        String arch = System.getenv("PROCESSOR_ARCHITECTURE");
        String wow64Arch = System.getenv("PROCESSOR_ARCHITEW6432");
        return arch.endsWith("64") || (wow64Arch != null && wow64Arch.endsWith("64"));
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
        primaryStage.setOnCloseRequest(createOnCloseRequestHandler());
        primaryStage.setScene(scene);
    }
    
    private EventHandler<WindowEvent> createOnCloseRequestHandler(){
        return (WindowEvent event) -> {
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
        };
    }
}
