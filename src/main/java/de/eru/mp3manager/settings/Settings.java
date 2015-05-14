package de.eru.mp3manager.settings;

import de.eru.mp3manager.Mp3Manager;
import de.eru.mp3manager.gui.applicationwindow.main.MainColumn;
import de.eru.mp3manager.utils.ExceptionHandler;
import java.io.File;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javax.enterprise.context.ApplicationScoped;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationException;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
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

    public static final String FILE_PATH = Mp3Manager.APPLICATION_PATH + "/settings.xml";

    private static final String XMLPATH_ENDING = "/text()";
    private static final String XMLPATH_DIRECTORIES = "directories/";
    private static final String XMLPATH_APPLICATION_WINDOW = "applicationWindow/";
    private static final String XMLPATH_EDITFILETAB = "editFileTab/";
    private static final String XMLPATH_MUSICPLAYER = "musicPlayer/";

    @XmlPath(XMLPATH_DIRECTORIES + "music" + XMLPATH_ENDING)
    private final StringProperty musicDirectory = new SimpleStringProperty("");
    @XmlPath(XMLPATH_DIRECTORIES + "playlists" + XMLPATH_ENDING)
    private final StringProperty playlistFilePath = new SimpleStringProperty(Mp3Manager.APPLICATION_PATH);

    @XmlPath(XMLPATH_APPLICATION_WINDOW + "maximized" + XMLPATH_ENDING)
    private final BooleanProperty applicationWindowMaximized = new SimpleBooleanProperty(false);
    @XmlPath(XMLPATH_APPLICATION_WINDOW + "width" + XMLPATH_ENDING)
    private final DoubleProperty applicationWindowWidth = new SimpleDoubleProperty(1300.0);
    @XmlPath(XMLPATH_APPLICATION_WINDOW + "height" + XMLPATH_ENDING)
    private final DoubleProperty applicationWindowHeight = new SimpleDoubleProperty(800.0);
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
            JAXBContext context = JAXBContext.newInstance(Settings.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setEventHandler((ValidationEvent event) -> {
                ExceptionHandler.handle(new ValidationException("XML ist fehlerhaft"), "Fehler beim speichern der Einstellungen!",
                        "Exception validating settings.xml-file");
                return false;
            });
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(this, new File(FILE_PATH));
            return true;
        } catch (JAXBException ex) {
            ExceptionHandler.handle(ex, "Fehler beim speichern der Einstellungen!", "Exception parsing settings.xml-file");
            return false;
        }
    }

    public static Settings load() {
        try {
            JAXBContext context = JAXBContext.newInstance(Settings.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            unmarshaller.setEventHandler((ValidationEvent event) -> {
                ExceptionHandler.handle(new ValidationException("XML ist fehlerhaft"), "XML ist nicht valide!");
                return false;
            });
            if (!new File(FILE_PATH).exists()) {
                System.out.println("Settings nicht da"); //TODO Notification
                return createDefaultSettings();
            }
            return (Settings) unmarshaller.unmarshal(new File(FILE_PATH));
        } catch (JAXBException ex) {
            //TODO Text anpassen
            ExceptionHandler.handle(ex, "Einstellungen konnten nicht geladen werden!", "Exception parsing settings.xml-file!");
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

}
