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
import javafx.scene.control.SplitPane;

public class ApplicationPresenter implements Initializable {

    @FXML
    private SplitPane splitPane;
    @FXML
    private Tab editFileTab;
    @FXML
    private Tab playlistTab;
    @FXML
    private VBox musicPlayerBox;

    private PlaylistPresenter playlistPresenter;
    private EditFilePresenter editFilePresenter;
    private MainPresenter mainPresenter;
    private MusicPlayerPresenter musicPlayerPresenter;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initViewsAndPresenters();
//        mainPresenter.readFiles("D:\\projekte\\TestMusik"); //TODO Richtige Stelle für diesen Aufruf ?
//        mainPresenter.readFiles("E:\\Musik"); //TODO Richtige Stelle für diesen Aufruf?
        mainPresenter.readFiles(Settings.INSTANCE.getMusicDirectory()); //TODO Richtige Stelle für diesen Aufruf ?
    }

    /**
     * Initialisiert alle Views und deren Presenter.
     */
    private void initViewsAndPresenters() {
        //Tab zum editieren der MP3-Dateien
        EditFileView editFileView = new EditFileView();
        editFilePresenter = (EditFilePresenter) editFileView.getPresenter();
        editFileTab.setContent(editFileView.getView());

        //Music-Player
        MusicPlayerView musicPlayerView = new MusicPlayerView();
        musicPlayerPresenter = (MusicPlayerPresenter) musicPlayerView
                .getPresenter();
        musicPlayerBox.getChildren().add(musicPlayerView.getView());

        //Tab für die aktuelle Wiedergabe
        PlaylistView playlistView = new PlaylistView();
        playlistPresenter = (PlaylistPresenter) playlistView.getPresenter();
        playlistTab.setContent(playlistView.getView());

        //MainView mit Tabelle
        MainView mainView = new MainView();
        mainPresenter = (MainPresenter) mainView.getPresenter();
        splitPane.getItems().add(mainView.getView());
    }

}
