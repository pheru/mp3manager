package de.eru.mp3manager.gui.notifications.progressnotification;

import de.eru.pherufx.gui.JavaFXView;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ProgressNotificationView extends JavaFXView {

    private static Stage stage;

    public static void show() {
        stage = new Stage();
        VBox view = (VBox) new ProgressNotificationView().getView();
        Scene scene = new Scene(view);
        stage.setScene(scene);
        stage.setResizable(false);
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        stage.setX(bounds.getMaxX() - view.getPrefWidth());
        stage.setY(bounds.getMaxY() - 81);
        stage.initStyle(StageStyle.UTILITY);
        stage.setAlwaysOnTop(true);
        stage.show();
    }

    public static void hide() {
        if (stage != null) {
            stage.hide();
        }
    }
}
