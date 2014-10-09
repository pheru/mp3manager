package de.eru.mp3manager;

import de.eru.mp3manager.gui.applicationwindow.application.ApplicationPresenter;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import de.eru.mp3manager.gui.applicationwindow.application.ApplicationView;
import java.util.logging.Level;
import java.util.logging.Logger;
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

    private static final Logger JAUDIOTAGGER_LOGGER = Logger.getLogger("org.jaudiotagger");

    @Override
    public void start(Stage primaryStage) throws Exception {
        ApplicationView applicationView = new ApplicationView();
        ApplicationPresenter applicationPresenter = (ApplicationPresenter) applicationView.getPresenter();
        applicationPresenter.setPrimaryStage(primaryStage);

        //TODO Test-Stage
        Rectangle rect = new Rectangle(400, 100);
        rect.setFill(Color.RED);
        rect.setArcHeight(15.0);
        rect.setArcWidth(50.0);
        Group group = new Group(rect);
        Stage stage = new Stage(StageStyle.TRANSPARENT);
        stage.setScene(new Scene(group, Color.TRANSPARENT));
//        stage.show();
    }

    public static void main(String[] args) {
        JAUDIOTAGGER_LOGGER.setLevel(Level.WARNING);
        launch(args);
    }
}
