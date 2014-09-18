package de.eru.mp3manager.gui.notifications.progressnotification;

import de.eru.mp3manager.service.TaskPool;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ResourceBundle;
import javafx.beans.binding.StringBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javax.inject.Inject;

public class ProgressNotificationPresenter implements Initializable {

    @FXML
    private Label title;
    @FXML
    private Label message;
    @FXML
    private ProgressBar progress;
    @FXML
    private Label progressLabel;

    @Inject
    private TaskPool taskPool;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        bindUI();
    }

    private void bindUI() {
        title.textProperty().bind(taskPool.titleProperty());
        message.textProperty().bind(taskPool.messageProperty());
        progress.progressProperty().bind(taskPool.progressProperty());
        progressLabel.textProperty().bind(new StringBinding() {
            {
                bind(progress.progressProperty());
            }

            @Override
            protected String computeValue() {
                Double d = progress.getProgress();
                if (d < 0.0) {
                    return "";
                }
                d *= 100;
                return d.intValue() + "%";
            }
        });
        progress.progressProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            if(newValue.doubleValue() == 1.0){
               ProgressNotificationView.hide();
            }
        });
    }
}
