package de.eru.mp3manager.gui.notifications.progressnotification;

import com.airhacks.afterburner.views.FXMLView;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ProgressNotificationView extends FXMLView {
    private static Stage stage;

    public static void show() {
        stage = new Stage();
        Scene scene = new Scene(new ProgressNotificationView().getView());
        stage.setScene(scene);
        stage.setResizable(false);
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        stage.setX(bounds.getMaxX() - 400 - 15);
        stage.setY(bounds.getMaxY() - 81 - 50);
        stage.initStyle(StageStyle.UTILITY);
        stage.setAlwaysOnTop(true);
        stage.show();
    }
    
    public static void hide(){
        if(stage != null){
            stage.hide();
        }
    }
}
