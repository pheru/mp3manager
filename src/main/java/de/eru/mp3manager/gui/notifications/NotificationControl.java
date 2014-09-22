package de.eru.mp3manager.gui.notifications;

import de.eru.mp3manager.gui.notifications.progressnotification.ProgressNotificationView;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author Philipp Bruckner
 */
public class NotificationControl {

    public static final NotificationControl INSTANCE = new NotificationControl();
    
    private final ObservableList<Stage> notifications = FXCollections.observableArrayList();
    
    private NotificationControl() {
        notifications.addListener((ListChangeListener.Change<? extends Stage> c) -> {
            System.out.println("change");
        });
    }
    
    public void showProgressNotification(){
        Stage stage = new Stage();
        Scene scene = new Scene(new ProgressNotificationView().getView());
        stage.setScene(scene);
        stage.setResizable(false);
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        stage.setX(bounds.getMaxX() - 400 - 15);
        stage.setY(bounds.getMaxY() - 81 - 50);
        stage.initStyle(StageStyle.UTILITY);
        stage.setAlwaysOnTop(true);
        notifications.add(stage);
        stage.show();
    }
    
    public void showInfoNotification(){
        
    }
}
