package de.pheru.media.settings;

import de.pheru.media.gui.PheruMedia;
import de.pheru.media.gui.applicationwindow.main.MainTableColumn;
import de.pheru.media.settings.objectproperties.PosProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.persistence.oxm.annotations.XmlPath;

import javax.enterprise.context.ApplicationScoped;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.File;

@ApplicationScoped
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class Settings {

    public static final String FILE_PATH = PheruMedia.APPLICATION_PATH + "settings.xml";

    private static final Logger LOGGER = LogManager.getLogger(Settings.class);

    private static final String XMLPATH_GENERAL = "general/";
    private static final String XMLPATH_DIRECTORIES = "directories/";
    private static final String XMLPATH_APPLICATION_WINDOW = "applicationWindow/";
    private static final String XMLPATH_MUSICPLAYER = "musicPlayer/";
    private static final String XMLPATH_EDITFILEVIEW = "editFileView/";
    private static final String XMLPATH_NOTIFICATIONS = "notifications/";
    private static final String XMLPATH_DIALOGS = "dialogs/";

    private static final String XMLSUBPATH_DONTSHOWAGAIN = "/dontShowAgain/";

    private static final String XMLPATH_ENDING = "/text()";

    @XmlPath(XMLPATH_GENERAL + "shortcutsEnabled" + XMLPATH_ENDING)
    private final BooleanProperty shortcutsEnabled = new SimpleBooleanProperty(true);

    @XmlPath(XMLPATH_DIRECTORIES + "music" + XMLPATH_ENDING)
    private final StringProperty musicDirectory = new SimpleStringProperty("");
    @XmlPath(XMLPATH_DIRECTORIES + "playlists" + XMLPATH_ENDING)
    private final StringProperty playlistsDirectory = new SimpleStringProperty(PheruMedia.APPLICATION_PATH);

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

    @XmlPath(XMLPATH_EDITFILEVIEW + "sortTitles" + XMLPATH_ENDING)
    private final BooleanProperty editFileViewSortTitles = new SimpleBooleanProperty(false);
    @XmlPath(XMLPATH_EDITFILEVIEW + "sortAlbums" + XMLPATH_ENDING)
    private final BooleanProperty editFileViewSortAlbums = new SimpleBooleanProperty(false);
    @XmlPath(XMLPATH_EDITFILEVIEW + "sortArtists" + XMLPATH_ENDING)
    private final BooleanProperty editFileViewSortArtists = new SimpleBooleanProperty(false);
    @XmlPath(XMLPATH_EDITFILEVIEW + "synchronizeTitle" + XMLPATH_ENDING)
    private final BooleanProperty editFileViewSynchronizeTitle = new SimpleBooleanProperty(false);

    @XmlPath(XMLPATH_NOTIFICATIONS + "alignment" + XMLPATH_ENDING)
    private final PosProperty notificationsPosition = new PosProperty(Pos.BOTTOM_RIGHT);
    @XmlPath(XMLPATH_NOTIFICATIONS + "duration" + XMLPATH_ENDING)
    private final IntegerProperty notificationsDuration = new SimpleIntegerProperty(5);

    @XmlPath(XMLPATH_DIALOGS + XMLSUBPATH_DONTSHOWAGAIN + "closeApplication" + XMLPATH_ENDING)
    private final BooleanProperty dontShowAgainCloseApplicationDialog = new SimpleBooleanProperty(false);

    @XmlElementWrapper(name = "mainTableColumns")
    @XmlElement(name = "mainTableColumn")
    private final ObservableList<MainTableColumnSettings> mainTableColumnSettings = FXCollections.observableArrayList();

    Settings() {
        musicPlayerVolume.addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            setMusicPlayerMuted(false);
        });
    }

    protected void initDefaultMainColumnSettings() {
        mainTableColumnSettings.clear();
        for (MainTableColumn column : MainTableColumn.values()) {
            mainTableColumnSettings.add(new MainTableColumnSettings(column));
        }
    }

    public boolean save() {
        try {
            JAXBContext context = JAXBContext.newInstance(Settings.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setEventHandler((ValidationEvent event) -> {
                LOGGER.error("Exception validating settings!", event.getLinkedException());
                return false;
            });
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(this, new File(FILE_PATH));
            LOGGER.info("Einstellungen erfolgreich gespeichert.");
        } catch (JAXBException e) {
            LOGGER.error("Exception parsing settings.xml!", e);
            return false;
        }
        return true;
    }

    public ObservableList<MainTableColumnSettings> getAllMainTableColumnSettings() {
        return mainTableColumnSettings;
    }

    public MainTableColumnSettings getMainTableColumnSettings(MainTableColumn column) {
        for (MainTableColumnSettings mainColumnSetting : mainTableColumnSettings) {
            if (mainColumnSetting.getColumn() == column) {
                return mainColumnSetting;
            }
        }
        return null;
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

    public boolean getEditFileViewSortTitles() {
        return editFileViewSortTitles.get();
    }

    public void setEditFileViewSortTitles(final boolean editFileViewSortTitles) {
        this.editFileViewSortTitles.set(editFileViewSortTitles);
    }

    public BooleanProperty editFileViewSortTitlesProperty() {
        return editFileViewSortTitles;
    }

    public boolean getEditFileViewSortAlbums() {
        return editFileViewSortAlbums.get();
    }

    public void setEditFileViewSortAlbums(final boolean editFileViewSortAlbums) {
        this.editFileViewSortAlbums.set(editFileViewSortAlbums);
    }

    public BooleanProperty editFileViewSortAlbumsProperty() {
        return editFileViewSortAlbums;
    }

    public boolean getEditFileViewSortArtists() {
        return editFileViewSortArtists.get();
    }

    public void setEditFileViewSortArtists(final boolean editFileViewSortArtists) {
        this.editFileViewSortArtists.set(editFileViewSortArtists);
    }

    public BooleanProperty editFileViewSortArtistsProperty() {
        return editFileViewSortArtists;
    }

    public boolean getEditFileViewSynchronizeTitle() {
        return editFileViewSynchronizeTitle.get();
    }

    public void setEditFileViewSynchronizeTitle(final boolean editFileViewSynchronizeTitle) {
        this.editFileViewSynchronizeTitle.set(editFileViewSynchronizeTitle);
    }

    public BooleanProperty editFileViewSynchronizeTitleProperty() {
        return editFileViewSynchronizeTitle;
    }

    public String getPlaylistsDirectory() {
        return playlistsDirectory.get();
    }

    public void setPlaylistsDirectory(final String playlistsDirectory) {
        this.playlistsDirectory.set(playlistsDirectory);
    }

    public StringProperty playlistsDirectoryProperty() {
        return playlistsDirectory;
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

    public Pos getNotificationsPosition() {
        return notificationsPosition.get();
    }

    public void setNotificationsPosition(final Pos notificationsPosition) {
        this.notificationsPosition.set(notificationsPosition);
    }

    public ObjectProperty<Pos> notificationsPositionProperty() {
        return notificationsPosition;
    }

    public int getNotificationsDuration() {
        return notificationsDuration.get();
    }

    public void setNotificationsDuration(final int notificationsDuration) {
        this.notificationsDuration.set(notificationsDuration);
    }

    public IntegerProperty notificationsDurationProperty() {
        return notificationsDuration;
    }

    public Boolean isDontShowAgainApplicationCloseDialog() {
        return dontShowAgainCloseApplicationDialog.get();
    }

    public void setDontShowAgainCloseApplicationDialog(final Boolean dontShowAgainCloseApplicationDialog) {
        this.dontShowAgainCloseApplicationDialog.set(dontShowAgainCloseApplicationDialog);
    }

    public BooleanProperty dontShowAgainCloseApplicationDialogProperty() {
        return dontShowAgainCloseApplicationDialog;
    }

    public Boolean isShortcutsEnabled() {
        return shortcutsEnabled.get();
    }

    public void setShortcutsEnabled(final Boolean shortcutsEnabled) {
        this.shortcutsEnabled.set(shortcutsEnabled);
    }

    public BooleanProperty shortcutsEnabledProperty() {
        return shortcutsEnabled;
    }

}
