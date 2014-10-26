package de.eru.mp3manager.gui.applicationwindow.application;

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
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.SplitPane;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

@ApplicationScoped
public class ApplicationPresenter implements Initializable {

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
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

    @FXML
    private void changeDirectory(){
        mainPresenter.changeDirectory();
    }
    
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
