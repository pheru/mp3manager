package de.pheru.media.settings;

import de.pheru.fx.controls.notification.Notification;
import de.pheru.fx.controls.notification.Notifications;
import de.pheru.media.PheruMedia;
import de.pheru.media.gui.applicationwindow.main.MainColumn;
import de.pheru.media.settings.objectproperties.NotificationsAlignmentProperty;
import java.io.File;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javax.enterprise.context.ApplicationScoped;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.persistence.oxm.annotations.XmlPath;

/**
 * Klasse zum Speichern und Auslesen von Einstellungen.
 *
 * @author Philipp Bruckner
 */
@ApplicationScoped
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class Settings {

    public static final String FILE_PATH = PheruMedia.APPLICATION_PATH + "/settings.xml";

    private static final String XMLPATH_ENDING = "/text()";

    private static final String XMLPATH_GENERAL = "general/";
    private static final String XMLPATH_DIRECTORIES = "directories/";
    private static final String XMLPATH_APPLICATION_WINDOW = "applicationWindow/";
    private static final String XMLPATH_MUSICPLAYER = "musicPlayer/";
    private static final String XMLPATH_EDITFILETAB = "editFileTab/";
    private static final String XMLPATH_NOTIFICATIONS = "notifications/";
    private static final String XMLPATH_DIALOGS = "dialogs/";

    private static final Logger LOGGER = LogManager.getLogger(Settings.class);

    //TODO Binding?
    @XmlPath(XMLPATH_GENERAL + "jIntelliTypeEnabled" + XMLPATH_ENDING)
    private final BooleanProperty jIntelliTypeEnabled = new SimpleBooleanProperty(true);
    private final BooleanProperty jIntelliTypeProhibited = new SimpleBooleanProperty(false);

    @XmlPath(XMLPATH_DIRECTORIES + "music" + XMLPATH_ENDING)
    private final StringProperty musicDirectory = new SimpleStringProperty("");
    @XmlPath(XMLPATH_DIRECTORIES + "playlists" + XMLPATH_ENDING)
    private final StringProperty playlistFilePath = new SimpleStringProperty(PheruMedia.APPLICATION_PATH);

    @XmlPath(XMLPATH_APPLICATION_WINDOW + "maximized" + XMLPATH_ENDING)
    private final BooleanProperty applicationWindowMaximized = new SimpleBooleanProperty(false);
    @XmlPath(XMLPATH_APPLICATION_WINDOW + "width" + XMLPATH_ENDING)
    private final DoubleProperty applicationWindowWidth = new SimpleDoubleProperty(800.0);
    @XmlPath(XMLPATH_APPLICATION_WINDOW + "height" + XMLPATH_ENDING)
    private final DoubleProperty applicationWindowHeight = new SimpleDoubleProperty(500.0);
    @XmlPath(XMLPATH_APPLICATION_WINDOW + "splitPosition" + XMLPATH_ENDING)
    private final DoubleProperty applicationWindowSplitPosition = new SimpleDoubleProperty(0.0);

    @XmlPath(XMLPATH_MUSICPLAYER + "volume" + XMLPATH_ENDING)
    private final DoubleProperty musicPlayerVolume = new SimpleDoubleProperty(100.0);
    @XmlPath(XMLPATH_MUSICPLAYER + "muted" + XMLPATH_ENDING)
    private final BooleanProperty musicPlayerMuted = new SimpleBooleanProperty(false);
    @XmlPath(XMLPATH_MUSICPLAYER + "repeat" + XMLPATH_ENDING)
    private final BooleanProperty musicPlayerRepeat = new SimpleBooleanProperty(false);
    @XmlPath(XMLPATH_MUSICPLAYER + "random" + XMLPATH_ENDING)
    private final BooleanProperty musicPlayerRandom = new SimpleBooleanProperty(false);

    @XmlPath(XMLPATH_EDITFILETAB + "sortTitles" + XMLPATH_ENDING)
    private final BooleanProperty editFileSortTitle = new SimpleBooleanProperty(false);
    @XmlPath(XMLPATH_EDITFILETAB + "sortAlbums" + XMLPATH_ENDING)
    private final BooleanProperty editFileSortAlbum = new SimpleBooleanProperty(false);
    @XmlPath(XMLPATH_EDITFILETAB + "sortArtists" + XMLPATH_ENDING)
    private final BooleanProperty editFileSortArtist = new SimpleBooleanProperty(false);
    @XmlPath(XMLPATH_EDITFILETAB + "synchronizeTitle" + XMLPATH_ENDING)
    private final BooleanProperty editFileSynchronizeTitle = new SimpleBooleanProperty(false);

    @XmlPath(XMLPATH_NOTIFICATIONS + "alignment" + XMLPATH_ENDING)
    private final NotificationsAlignmentProperty notificationsAlignment = new NotificationsAlignmentProperty(Notifications.Alignment.BOTTOM_RIGHT);
    @XmlPath(XMLPATH_NOTIFICATIONS + "timer" + XMLPATH_ENDING)
    private final IntegerProperty notificationsTimer = new SimpleIntegerProperty(5);
    
    @XmlPath(XMLPATH_DIALOGS + "dontShowAgainApplicationCloseDialog" + XMLPATH_ENDING)
    private final BooleanProperty dontShowAgainApplicationCloseDialog = new SimpleBooleanProperty(false);

    @XmlElementWrapper(name = "tableColumns")
    @XmlElement(name = "tableColumn")
    private final ObservableList<ColumnSettings> mainColumnSettings = FXCollections.observableArrayList();

    public Settings() {
    }

    private void initDefaultMainColumnSettings() {
        for (MainColumn column : MainColumn.values()) {
            mainColumnSettings.add(new ColumnSettings(column));
        }
    }

    public boolean save() {
        try {
            LOGGER.debug("Saving settings...");
            JAXBContext context = JAXBContext.newInstance(Settings.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setEventHandler((ValidationEvent event) -> {
                LOGGER.error("Exception validating settings!", event.getLinkedException());
                return false;
            });
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(this, new File(FILE_PATH));
            LOGGER.debug("Settings saved.");
            return true;
        } catch (Exception e) {
            //TODO FXThread
            LOGGER.error("Exception parsing settings.xml!", e);
            Alert alert = new Alert(Alert.AlertType.ERROR, "Einstellungen konnten nicht gespeichert werden!");
            alert.showAndWait();
            return false;
        }
    }

    public static Settings load() {
        try {
            if (!new File(FILE_PATH).exists()) {
                Notifications.createNotification(Notification.Type.INFO)
                        .setText("Es konnten keine Einstellungen gefunden werden.\n"
                                + "Eine neue Datei wird angelegt.")
                        .show();
                return createDefaultSettings();
            }
            JAXBContext context = JAXBContext.newInstance(Settings.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            unmarshaller.setEventHandler((ValidationEvent event) -> {
                LOGGER.error("Invalid settings.xml!", event.getLinkedException());
                return false;
            });
            return (Settings) unmarshaller.unmarshal(new File(FILE_PATH));
        } catch (Exception e) {
            LOGGER.error("Exception parsing settings.xml!", e);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Einstellungen konnten nicht geladen werden!");
            alert.setContentText("Es werden neue Einstellungen angelegt.");
            alert.showAndWait();
            return createDefaultSettings();
        }
    }

    private static Settings createDefaultSettings() {
        Settings defaultSettings = new Settings();
        defaultSettings.initDefaultMainColumnSettings();
        return defaultSettings;
    }

    public ObservableList<ColumnSettings> getAllMainColumnSettings() {
        return mainColumnSettings;
    }

    public ColumnSettings getMainColumnSettings(MainColumn column) {
        for (ColumnSettings mainColumnSetting : mainColumnSettings) {
            if (mainColumnSetting.getColumn().equals(column)) {
                return mainColumnSetting;
            }
        }
        return null;
    }

    public boolean isJIntelliTypeEnabled() {
        return jIntelliTypeEnabled.get();
    }

    public void setJIntelliTypeEnabled(final boolean jIntelliTypeEnabled) {
        this.jIntelliTypeEnabled.set(jIntelliTypeEnabled);
    }

    public BooleanProperty jIntelliTypeEnabledProperty() {
        return jIntelliTypeEnabled;
    }

    public boolean isJIntelliTypeProhibited() {
        return jIntelliTypeProhibited.get();
    }

    public void setJIntelliTypeProhibited(final boolean jIntelliTypeProhibited) {
        this.jIntelliTypeProhibited.set(jIntelliTypeProhibited);
    }

    public BooleanProperty jIntelliTypeProhibitedProperty() {
        return jIntelliTypeProhibited;
    }

    public String getMusicDirectory() {
        return musicDirectory.get();
    }

    public void setMusicDirectory(final String musicDirectory) {
        this.musicDirectory.set(musicDirectory);
    }

    public StringProperty musicDirectoryProperty() {
        return musicDirectory;
    }

    public double getApplicationWindowWidth() {
        return applicationWindowWidth.get();
    }

    public void setApplicationWindowWidth(final double applicationWindowWidth) {
        this.applicationWindowWidth.set(applicationWindowWidth);
    }

    public DoubleProperty applicationWindowWidthProperty() {
        return applicationWindowWidth;
    }

    public double getApplicationWindowHeight() {
        return applicationWindowHeight.get();
    }

    public void setApplicationWindowHeight(final double applicationWindowHeight) {
        this.applicationWindowHeight.set(applicationWindowHeight);
    }

    public DoubleProperty applicationWindowHeightProperty() {
        return applicationWindowHeight;
    }

    public double getMusicPlayerVolume() {
        return musicPlayerVolume.get();
    }

    public void setMusicPlayerVolume(final double musicPlayerVolume) {
        this.musicPlayerVolume.set(musicPlayerVolume);
    }

    public DoubleProperty musicPlayerVolumeProperty() {
        return musicPlayerVolume;
    }

    public boolean isMusicPlayerRepeat() {
        return musicPlayerRepeat.get();
    }

    public void setMusicPlayerRepeat(final boolean musicPlayerRepeat) {
        this.musicPlayerRepeat.set(musicPlayerRepeat);
    }

    public BooleanProperty musicPlayerRepeatProperty() {
        return musicPlayerRepeat;
    }

    public boolean isMusicPlayerRandom() {
        return musicPlayerRandom.get();
    }

    public void setMusicPlayerRandom(final boolean musicPlayerRandom) {
        this.musicPlayerRandom.set(musicPlayerRandom);
    }

    public BooleanProperty musicPlayerRandomProperty() {
        return musicPlayerRandom;
    }

    public boolean isApplicationWindowMaximized() {
        return applicationWindowMaximized.get();
    }

    public void setApplicationWindowMaximized(final boolean applicationWindowMaximized) {
        this.applicationWindowMaximized.set(applicationWindowMaximized);
    }

    public BooleanProperty applicationWindowMaximizedProperty() {
        return applicationWindowMaximized;
    }

    public boolean isEditFileSortTitle() {
        return editFileSortTitle.get();
    }

    public void setEditFileSortTitle(final boolean editFileSortTitle) {
        this.editFileSortTitle.set(editFileSortTitle);
    }

    public BooleanProperty editFileSortTitleProperty() {
        return editFileSortTitle;
    }

    public boolean isEditFileSortAlbum() {
        return editFileSortAlbum.get();
    }

    public void setEditFileSortAlbum(final boolean editFileSortAlbum) {
        this.editFileSortAlbum.set(editFileSortAlbum);
    }

    public BooleanProperty editFileSortAlbumProperty() {
        return editFileSortAlbum;
    }

    public boolean isEditFileSortArtist() {
        return editFileSortArtist.get();
    }

    public void setEditFileSortArtist(final boolean editFileSortArtist) {
        this.editFileSortArtist.set(editFileSortArtist);
    }

    public BooleanProperty editFileSortArtistProperty() {
        return editFileSortArtist;
    }

    public boolean isEditFileSynchronizeTitle() {
        return editFileSynchronizeTitle.get();
    }

    public void setEditFileSynchronizeTitle(final boolean editFileSynchronizeTitle) {
        this.editFileSynchronizeTitle.set(editFileSynchronizeTitle);
    }

    public BooleanProperty editFileSynchronizeTitleProperty() {
        return editFileSynchronizeTitle;
    }

    public String getPlaylistFilePath() {
        return playlistFilePath.get();
    }

    public void setPlaylistFilePath(final String playlistFilePath) {
        this.playlistFilePath.set(playlistFilePath);
    }

    public StringProperty playlistFilePathProperty() {
        return playlistFilePath;
    }

    public double getApplicationWindowSplitPosition() {
        return applicationWindowSplitPosition.get();
    }

    public void setApplicationWindowSplitPosition(final double applicationWindowSplitPosition) {
        this.applicationWindowSplitPosition.set(applicationWindowSplitPosition);
    }

    public DoubleProperty applicationWindowSplitPositionProperty() {
        return applicationWindowSplitPosition;
    }

    public boolean isMusicPlayerMuted() {
        return musicPlayerMuted.get();
    }

    public void setMusicPlayerMuted(final boolean musicPlayerMuted) {
        this.musicPlayerMuted.set(musicPlayerMuted);
    }

    public BooleanProperty musicPlayerMutedProperty() {
        return musicPlayerMuted;
    }

    public Notifications.Alignment getNotificationsAlignment() {
        return notificationsAlignment.get();
    }

    public void setNotificationsAlignment(final Notifications.Alignment notificationsAlignment) {
        this.notificationsAlignment.set(notificationsAlignment);
    }

    public ObjectProperty<Notifications.Alignment> notificationsAlignmentProperty() {
        return notificationsAlignment;
    }

    public int getNotificationsTimer() {
        return notificationsTimer.get();
    }

    public void setNotificationsTimer(final int notificationsTimer) {
        this.notificationsTimer.set(notificationsTimer);
    }

    public IntegerProperty notificationsTimerProperty() {
        return notificationsTimer;
    }

    public Boolean isDontShowAgainApplicationCloseDialog() {
        return dontShowAgainApplicationCloseDialog.get();
    }

    public void setDontShowAgainApplicationCloseDialog(final Boolean dontShowAgainApplicationCloseDialog) {
        this.dontShowAgainApplicationCloseDialog.set(dontShowAgainApplicationCloseDialog);
    }

    public BooleanProperty dontShowAgainApplicationCloseDialogProperty() {
        return dontShowAgainApplicationCloseDialog;
    }

}
