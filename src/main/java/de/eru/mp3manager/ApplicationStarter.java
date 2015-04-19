package de.eru.mp3manager;

import com.melloware.jintellitype.JIntellitype;
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
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                JIntellitype.getInstance().cleanUp();
                settings.save();
            }
        });
        Thread.setDefaultUncaughtExceptionHandler((Thread t, Throwable e) -> {
            ExceptionHandler.handle(e);
        });
        initPrimaryStage(primaryStage);
        initSystemTrayIcon(primaryStage);
        primaryStage.show();
    }

    private void initPrimaryStage(Stage primaryStage) {
        Scene scene = new Scene(applicationView.getView());
        primaryStage.setTitle("MP3-Manager");
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
                System.exit(0);
            });
        }
    }

}
