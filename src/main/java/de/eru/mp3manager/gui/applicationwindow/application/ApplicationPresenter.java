package de.eru.mp3manager.gui.applicationwindow.application;

import de.eru.mp3manager.Mp3SystemTrayIcon;
import de.eru.mp3manager.Settings;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.layout.VBox;
import de.eru.mp3manager.gui.applicationwindow.editfile.EditFilePresenter;
import de.eru.mp3manager.gui.applicationwindow.editfile.EditFileView;
import de.eru.mp3manager.gui.applicationwindow.main.MainPresenter;
import de.eru.mp3manager.gui.applicationwindow.main.MainView;
import de.eru.mp3manager.gui.applicationwindow.musicplayer.MusicPlayerPresenter;
import de.eru.mp3manager.gui.applicationwindow.musicplayer.MusicPlayerView;
import de.eru.mp3manager.gui.applicationwindow.playlist.PlaylistPresenter;
import de.eru.mp3manager.gui.applicationwindow.playlist.PlaylistView;
import de.eru.mp3manager.player.MusicPlayer;
import de.eru.mp3manager.utils.Logfile;
import java.awt.SystemTray;
import java.awt.event.ActionEvent;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

@ApplicationScoped
public class ApplicationPresenter implements Initializable {

    @FXML
    private GridPane root;
    @FXML
    private SplitPane splitPane;
    @FXML
    private Tab editFileTab;
    @FXML
    private Tab playlistTab;
    @FXML
    private VBox musicPlayerBox;
    @FXML
    private CheckMenuItem repeatMenuItem;
    @FXML
    private CheckMenuItem randomMenuItem;

    @Inject
    private Settings settings;
    @Inject
    private Mp3SystemTrayIcon systemTrayIcon;
    @Inject
    private MusicPlayer musicPlayer;

    @Inject
    private PlaylistView playlistView;
    private PlaylistPresenter playlistPresenter;
    @Inject
    private EditFileView editFileView;
    private EditFilePresenter editFilePresenter;
    @Inject
    private MainView mainView;
    private MainPresenter mainPresenter;
    @Inject
    private MusicPlayerView musicPlayerView;
    private MusicPlayerPresenter musicPlayerPresenter;

    private Stage primaryStage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                settings.save();
            }
        });
        initViewsAndPresenters();
        bindUI();
        mainPresenter.readDirectory();
    }

    /**
     * Initialisiert alle Views und deren Presenter.
     */
    private void initViewsAndPresenters() {
        //Tab zum editieren der MP3-Dateien
        editFilePresenter = (EditFilePresenter) editFileView.getPresenter();
        editFileTab.setContent(editFileView.getView());

        //Music-Player
        musicPlayerPresenter = (MusicPlayerPresenter) musicPlayerView
                .getPresenter();
        musicPlayerBox.getChildren().add(musicPlayerView.getView());

        //Tab für die aktuelle Wiedergabe
        playlistPresenter = (PlaylistPresenter) playlistView.getPresenter();
        playlistTab.setContent(playlistView.getView());

        //MainView mit Tabelle
        mainPresenter = (MainPresenter) mainView.getPresenter();
        splitPane.getItems().add(mainView.getView());
    }
    
    private void bindUI(){
        randomMenuItem.selectedProperty().bindBidirectional(settings.musicPlayerRandomProperty());
        repeatMenuItem.selectedProperty().bindBidirectional(settings.musicPlayerRepeatProperty());
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
        initPrimaryStage();
        initSystemTrayIcon();
    }

    private void initPrimaryStage() {
        Scene scene = new Scene(root);
        primaryStage.setTitle("MP3-Manager");
        primaryStage.setWidth(settings.getApplicationWindowWidth());
        settings.applicationWindowWidthProperty().bind(primaryStage.widthProperty());
        primaryStage.setHeight(settings.getApplicationWindowHeight());
        settings.applicationWindowHeightProperty().bind(primaryStage.heightProperty());
        primaryStage.setMaximized(settings.isApplicationWindowMaximized());
        settings.applicationWindowMaximizedProperty().bind(primaryStage.maximizedProperty());
        primaryStage.setScene(scene);
    }

    private void initSystemTrayIcon() {
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
                System.exit(0);
            });
        }
    }
    
    @FXML
    private void changeDirectory(){
        mainPresenter.changeDirectory();
    }
    
    @Inject
    private Event<Logfile> event;
    
    @FXML
    private void exit(){
        System.exit(0);
    }
    
    @FXML
    private void saveChanges(){
        editFilePresenter.save();
    }
    
    @FXML
    private void discardChanges(){
        editFilePresenter.discard();
    }
    
    @FXML
    private void delete(){
        editFilePresenter.delete();
    }
    
    @FXML
    private void playPause(){
        musicPlayer.playPause();
    }
    
    @FXML
    private void stop(){
        musicPlayer.stop();
    }
    
    @FXML
    private void next(){
        musicPlayer.next();
    }
    
    @FXML
    private void previous(){
        musicPlayer.previous();
    }
    
    @FXML
    private void about(){
    }
}
