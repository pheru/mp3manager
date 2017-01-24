package de.pheru.media.gui.applicationwindow.application;

import de.pheru.fx.util.properties.ObservableProperties;
import de.pheru.media.gui.Settings;
import de.pheru.media.gui.applicationwindow.main.MainPresenter;
import de.pheru.media.gui.applicationwindow.main.MainView;
import de.pheru.media.gui.applicationwindow.musicplayer.MusicPlayerPresenter;
import de.pheru.media.gui.applicationwindow.musicplayer.MusicPlayerView;
import de.pheru.media.gui.applicationwindow.playlist.PlaylistPresenter;
import de.pheru.media.gui.applicationwindow.playlist.PlaylistView;
import de.pheru.media.gui.player.MusicPlayer;
import de.pheru.media.util.GlobalKeyListener;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.VBox;
import org.jnativehook.keyboard.NativeKeyEvent;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

@ApplicationScoped
public class ApplicationPresenter implements Initializable {

    @FXML
    private SplitPane splitPane;
    @FXML
    private VBox musicPlayerBox;
    @FXML
    private CheckMenuItem repeatMenuItem;
    @FXML
    private CheckMenuItem randomMenuItem;

    @Inject
    private ObservableProperties settings;
    @Inject
    private GlobalKeyListener globalKeyListener;
    @Inject
    private MusicPlayer musicPlayer;

    @Inject
    private MainView mainView;
    private MainPresenter mainPresenter;
    @Inject
    private PlaylistView playlistView;
    private PlaylistPresenter playlistPresenter;
    @Inject
    private MusicPlayerView musicPlayerView;
    private MusicPlayerPresenter musicPlayerPresenter;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initViewsAndPresenters();
        bindUI();
        setUpShortcuts();
        mainPresenter.readDirectory();
    }

    /**
     * Initialisiert alle Views und deren Presenter.
     */
    private void initViewsAndPresenters() {
        mainPresenter = (MainPresenter) mainView.getPresenter();
        splitPane.getItems().add(mainView.getView());

        playlistPresenter = (PlaylistPresenter) playlistView.getPresenter();
        splitPane.getItems().add(0, playlistView.getView());
        SplitPane.setResizableWithParent(playlistView.getView(), false);

        musicPlayerPresenter = (MusicPlayerPresenter) musicPlayerView.getPresenter();
        musicPlayerBox.getChildren().add(musicPlayerView.getView());
    }

    private void bindUI() {
        randomMenuItem.selectedProperty().bindBidirectional(settings.booleanProperty(Settings.MUSICPLAYER_RANDOM));
        repeatMenuItem.selectedProperty().bindBidirectional(settings.booleanProperty(Settings.MUSICPLAYER_REPEAT));
        Platform.runLater(() -> { //Wird benötigt, weil die Divider intern angepasst werden und damit den Wert überschreiben würden
            splitPane.getDividers().get(0).positionProperty().bindBidirectional(settings.doubleProperty(Settings.APPLICATIONWINDOW_SPLITPOSIITON));
        });
    }

    private void setUpShortcuts() {
        globalKeyListener.addKeyPressedHandler((NativeKeyEvent e) -> {
            switch (e.getKeyCode()) {
                case NativeKeyEvent.VC_MEDIA_PLAY:
                    Platform.runLater(musicPlayer::playPause);
                    break;
                case NativeKeyEvent.VC_MEDIA_PREVIOUS:
                    Platform.runLater(musicPlayer::previous);
                    break;
                case NativeKeyEvent.VC_MEDIA_NEXT:
                    Platform.runLater(musicPlayer::next);
                    break;
                case NativeKeyEvent.VC_MEDIA_STOP:
                    Platform.runLater(musicPlayer::stop);
                    break;
            }
        });
    }

    @FXML
    private void changeDirectory() {
        mainPresenter.changeDirectory();
    }

    @FXML
    private void exit() {
        Platform.exit();
    }

    @FXML
    private void saveChanges() {
        //TODO
//        editFilePresenter.save();
    }

    @FXML
    private void discardChanges() {
        //TODO
//        editFilePresenter.discard();
    }

    @FXML
    private void playPause() {
        musicPlayer.playPause();
    }

    @FXML
    private void stop() {
        musicPlayer.stop();
    }

    @FXML
    private void next() {
        musicPlayer.next();
    }

    @FXML
    private void previous() {
        musicPlayer.previous();
    }

}
