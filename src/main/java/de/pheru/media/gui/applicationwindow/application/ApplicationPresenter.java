package de.pheru.media.gui.applicationwindow.application;

import de.pheru.fx.util.focus.FocusTraversal;
import de.pheru.media.GlobalKeyListener;
import de.pheru.media.cdi.qualifiers.XMLSettings;
import de.pheru.media.gui.applicationwindow.editfile.EditFilePresenter;
import de.pheru.media.gui.applicationwindow.editfile.EditFileView;
import de.pheru.media.gui.applicationwindow.main.MainPresenter;
import de.pheru.media.gui.applicationwindow.main.MainView;
import de.pheru.media.gui.applicationwindow.musicplayer.MusicPlayerPresenter;
import de.pheru.media.gui.applicationwindow.musicplayer.MusicPlayerView;
import de.pheru.media.gui.applicationwindow.playlist.PlaylistPresenter;
import de.pheru.media.gui.applicationwindow.playlist.PlaylistView;
import de.pheru.media.player.MusicPlayer;
import de.pheru.media.settings.Settings;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.jnativehook.keyboard.NativeKeyEvent;

@ApplicationScoped
public class ApplicationPresenter implements Initializable {

    @FXML
    private SplitPane splitPane;
    @FXML
    private TabPane tabPane;
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
    @XMLSettings
    private Settings settings;
    @Inject
    private GlobalKeyListener globalKeyListener;
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
        FocusTraversal.setTabKeyEventHandlerForNode(tabPane, () -> {
            int tabIndex = tabPane.getSelectionModel().getSelectedIndex();
            tabIndex++;
            if (tabIndex >= tabPane.getTabs().size()) {
                tabIndex = 0;
            }
            tabPane.getSelectionModel().select(tabIndex);
        }, () -> {
            int tabIndex = tabPane.getSelectionModel().getSelectedIndex();
            tabIndex--;
            if (tabIndex < 0) {
                tabIndex = tabPane.getTabs().size() - 1;
            }
            tabPane.getSelectionModel().select(tabIndex);
        });
        setUpShortcuts();
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

    private void bindUI() {
        randomMenuItem.selectedProperty().bindBidirectional(settings.musicPlayerRandomProperty());
        repeatMenuItem.selectedProperty().bindBidirectional(settings.musicPlayerRepeatProperty());
        Region content = (Region) tabPane.getSelectionModel().getSelectedItem().getContent();
        tabPane.setMinWidth(content.getMinWidth());
        tabPane.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Tab> observable, Tab oldValue, Tab newValue) -> {
            Region newContent = (Region) newValue.getContent();
            tabPane.setMinWidth(newContent.getMinWidth());
        });
        Platform.runLater(() -> { //Wird benötigt, weil die Divider intern angepasst werden und damit den Wert überschreiben würden
            splitPane.getDividers().get(0).positionProperty().bindBidirectional(settings.applicationWindowSplitPositionProperty());
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
        editFilePresenter.save();
    }

    @FXML
    private void discardChanges() {
        editFilePresenter.discard();
    }

    @FXML
    private void delete() {
        editFilePresenter.delete();
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

    @FXML
    private void about() {
    }
}
