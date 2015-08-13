package de.eru.mp3manager;

import com.melloware.jintellitype.JIntellitype;
import com.melloware.jintellitype.JIntellitypeException;
import de.eru.mp3manager.cdi.XMLSettings;
import de.eru.mp3manager.settings.Settings;
import de.eru.mp3manager.gui.applicationwindow.application.ApplicationView;
import de.eru.mp3manager.utils.ExceptionHandler;
import de.eru.pherufx.mvp.StartApplication;
import java.awt.SystemTray;
import java.awt.event.ActionEvent;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

/**
 *
 * @author Philipp Bruckner
 */
public class ApplicationStarter {

    @Inject
    @XMLSettings
    private Settings settings;
    @Inject
    private Mp3SystemTrayIcon systemTrayIcon;
    @Inject
    private ApplicationView applicationView;

    private void launchJavaFXApplication(@Observes @StartApplication Stage primaryStage) {
        initJIntelliType();
        try {
            initPrimaryStage(primaryStage);
            initSystemTrayIcon(primaryStage);
            primaryStage.show();
        } catch (Exception e) {
            ExceptionHandler.handle(e, "Fehler beim Starten der Anwendung!", "Exception initializing Application");
            Platform.exit();
        }
    }

    private void initJIntelliType() {
        String dll = is64BitOS() ? "/JIntellitype64.dll" : "/JIntellitype.dll";
        JIntellitype.setLibraryLocation(Mp3Manager.DLL_PATH + dll);
        try {
            JIntellitype.getInstance();
        } catch (JIntellitypeException e) {
            if (settings.isJIntelliTypeEnabled()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.getDialogPane().setPrefWidth(650.0);
                alert.setHeaderText("Initialisierung von JIntelliType fehlgeschlagen!");
                alert.setContentText("Stellen Sie sicher, dass sich unter \n\"" + Mp3Manager.DLL_PATH
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
        primaryStage.setTitle(Mp3Manager.APPLICATION_NAME);
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

        primaryStage.setScene(scene);
    }

    private void initSystemTrayIcon(Stage primaryStage) {
        if (SystemTray.isSupported()) {
            Platform.setImplicitExit(false);
            systemTrayIcon.addOnClick(() -> {
                Platform.runLater(() -> {
                    primaryStage.show();
                });
            });
            systemTrayIcon.addPopUpMenuItem("Öffnen", (ActionEvent e) -> {
                Platform.runLater(() -> {
                    primaryStage.show();
                });
            });
            systemTrayIcon.addPopUpMenuItem("Verstecken", (ActionEvent e) -> {
                Platform.runLater(() -> {
                    primaryStage.hide();
                });
            });
            systemTrayIcon.addPopUpMenuItem("Beenden", (ActionEvent e) -> {
                Platform.exit();
            });
        }
    }
}
