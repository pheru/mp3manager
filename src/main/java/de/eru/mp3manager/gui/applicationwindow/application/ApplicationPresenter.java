package de.eru.mp3manager.gui.applicationwindow.application;

import com.melloware.jintellitype.JIntellitype;
import de.eru.mp3manager.cdi.XMLSettings;
import de.eru.mp3manager.settings.Settings;
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
import de.eru.pherufx.focus.FocusTraversal;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Region;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

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
        setUpJIntelliType();
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

    private void setUpJIntelliType() {
//        JIntellitype.getInstance().registerHotKey(1, 0, (int) 'A');
//        JIntellitype.getInstance().addHotKeyListener((int identifier) -> {
//            System.out.println(identifier);
//        });
        
        JIntellitype.getInstance().addIntellitypeListener((int command) -> {
            switch (command) {
                case JIntellitype.APPCOMMAND_MEDIA_PLAY_PAUSE:
                    Platform.runLater(musicPlayer::playPause);
                    break;
                case JIntellitype.APPCOMMAND_MEDIA_PREVIOUSTRACK:
                    Platform.runLater(musicPlayer::previous);
                    break;
                case JIntellitype.APPCOMMAND_MEDIA_NEXTTRACK:
                    Platform.runLater(musicPlayer::next);
                    break;
                case JIntellitype.APPCOMMAND_MEDIA_STOP:
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
