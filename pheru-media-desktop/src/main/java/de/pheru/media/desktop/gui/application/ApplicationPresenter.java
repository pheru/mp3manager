package de.pheru.media.desktop.gui.application;

import de.pheru.fx.controls.notification.Notification;
import de.pheru.media.core.data.model.AudioFile;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class ApplicationPresenter implements Initializable{

    @FXML
    private VBox rootBox;
    @FXML
    private SplitPane splitPane;

    private Label playlistPlatzhalter = new Label("Playlist");
    private Label musicPlayerPlatzhalter = new Label("Musicplayer");

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        splitPane.getItems().add(0, playlistPlatzhalter);
        rootBox.getChildren().add(musicPlayerPlatzhalter);
    }

}