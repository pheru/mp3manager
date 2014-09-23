package de.eru.mp3manager;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import de.eru.mp3manager.gui.applicationwindow.application.ApplicationView;
import java.awt.SystemTray;
import java.awt.event.ActionEvent;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.StageStyle;

/**
 * Application-Klasse als Startpunkt für die JavaFX-Anwendung.
 *
 * @author Philipp Bruckner
 */
public class Mp3Manager extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        ApplicationView applicationView = new ApplicationView();
        Scene scene = new Scene(applicationView.getView());
        primaryStage.setTitle("MP3-Manager");
        primaryStage.setWidth(1300);
        primaryStage.setHeight(800);
        primaryStage.setScene(scene);
//        primaryStage.show();
        setUpTrayIcon(primaryStage);
        
        Rectangle rect = new Rectangle(400, 100);
        rect.setFill(Color.RED);
        rect.setArcHeight(15.0);
        rect.setArcWidth(50.0);
        Group group = new Group(rect);
        Stage stage = new Stage(StageStyle.TRANSPARENT);
        stage.setScene(new Scene(group, Color.TRANSPARENT));
//        stage.show();
    }

    private void setUpTrayIcon(Stage primaryStage) {
        if (SystemTray.isSupported()) {
            Platform.setImplicitExit(false);
            Mp3SystemTrayIcon.INSTANCE.addOnClick(() -> {
                Platform.runLater(() -> {
                    primaryStage.show();
                });
            });
            Mp3SystemTrayIcon.INSTANCE.addPopUpMenuItem("Öffnen", (ActionEvent e) -> {
                Platform.runLater(() -> {
                    primaryStage.show();
                });
            });
            Mp3SystemTrayIcon.INSTANCE.addPopUpMenuItem("Verstecken", (ActionEvent e) -> {
                Platform.runLater(() -> {
                    primaryStage.hide();
                });
            });
            Mp3SystemTrayIcon.INSTANCE.addPopUpMenuItem("Beenden", (ActionEvent e) -> {
                System.exit(0);
            });
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
