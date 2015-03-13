package de.eru.mp3manager.settings;

import de.eru.mp3manager.Mp3Manager;
import de.eru.mp3manager.gui.applicationwindow.main.MainColumn;
import de.eru.mp3manager.utils.ExceptionHandler;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
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
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.ValidationException;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Klasse zum Speichern und Auslesen von Einstellungen.
 *
 * @author Philipp Bruckner
 */
@ApplicationScoped
@XmlRootElement
public class Settings {

    public static final String ABSOLUTE_PATH = Mp3Manager.APPLICATION_PATH + "/settings.xml";

    private final StringProperty musicDirectory = new SimpleStringProperty("");

    private final BooleanProperty applicationWindowMaximized = new SimpleBooleanProperty(false);
    private final DoubleProperty applicationWindowWidth = new SimpleDoubleProperty(1300.0);
    private final DoubleProperty applicationWindowHeight = new SimpleDoubleProperty(800.0);

    @XmlElementWrapper
    private final ObservableList<ColumnSettings> mainColumnSettings = FXCollections.observableArrayList();

    private final DoubleProperty musicPlayerVolume = new SimpleDoubleProperty(100.0);
    private final BooleanProperty musicPlayerRepeat = new SimpleBooleanProperty(false);
    private final BooleanProperty musicPlayerRandom = new SimpleBooleanProperty(false);

    private final StringProperty playlistFilePath = new SimpleStringProperty("");

    private final BooleanProperty editFileSortTitle = new SimpleBooleanProperty(false);
    private final BooleanProperty editFileSortAlbum = new SimpleBooleanProperty(false);
    private final BooleanProperty editFileSortArtist = new SimpleBooleanProperty(false);
    private final BooleanProperty editFileSynchronizeTitle = new SimpleBooleanProperty(false);

    public Settings() {
    }

    private void initMainColumnSettings() {
        for (MainColumn column : MainColumn.values()) {
            mainColumnSettings.add(new ColumnSettings(column));
        }
    }

    public boolean save() {
        try {
            JAXBContext context = JAXBContext.newInstance(Settings.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setEventHandler((ValidationEvent event) -> {
                ExceptionHandler.handle(new ValidationException("XML ist fehlerhaft"));
                return false;
            });
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(this, new File(ABSOLUTE_PATH));
            return true;
        } catch (JAXBException ex) {
            ExceptionHandler.handle(ex);
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
            return (Settings) unmarshaller.unmarshal(new File(ABSOLUTE_PATH));
        } catch (JAXBException ex) {
            ExceptionHandler.handle(ex, "XML fehlerhaft oder nicht vorhanden!");
            Settings defaultSettings = new Settings();
            defaultSettings.initMainColumnSettings();
//            if (defaultSettings.save()) {
//                return load();
//            }
            return defaultSettings;
        }
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
        return null; //TODO sollte kein null zurückgeben
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

    public Double getApplicationWindowWidth() {
        return applicationWindowWidth.get();
    }

    public void setApplicationWindowWidth(final Double applicationWindowWidth) {
        this.applicationWindowWidth.set(applicationWindowWidth);
    }

    public DoubleProperty applicationWindowWidthProperty() {
        return applicationWindowWidth;
    }

    public Double getApplicationWindowHeight() {
        return applicationWindowHeight.get();
    }

    public void setApplicationWindowHeight(final Double applicationWindowHeight) {
        this.applicationWindowHeight.set(applicationWindowHeight);
    }

    public DoubleProperty applicationWindowHeightProperty() {
        return applicationWindowHeight;
    }

    public Double getMusicPlayerVolume() {
        return musicPlayerVolume.get();
    }

    public void setMusicPlayerVolume(final Double musicPlayerVolume) {
        this.musicPlayerVolume.set(musicPlayerVolume);
    }

    public DoubleProperty musicPlayerVolumeProperty() {
        return musicPlayerVolume;
    }

    public Boolean isMusicPlayerRepeat() {
        return musicPlayerRepeat.get();
    }

    public void setMusicPlayerRepeat(final Boolean musicPlayerRepeat) {
        this.musicPlayerRepeat.set(musicPlayerRepeat);
    }

    public BooleanProperty musicPlayerRepeatProperty() {
        return musicPlayerRepeat;
    }

    public Boolean isMusicPlayerRandom() {
        return musicPlayerRandom.get();
    }

    public void setMusicPlayerRandom(final Boolean musicPlayerRandom) {
        this.musicPlayerRandom.set(musicPlayerRandom);
    }

    public BooleanProperty musicPlayerRandomProperty() {
        return musicPlayerRandom;
    }

    public Boolean isApplicationWindowMaximized() {
        return applicationWindowMaximized.get();
    }

    public void setApplicationWindowMaximized(final Boolean applicationWindowMaximized) {
        this.applicationWindowMaximized.set(applicationWindowMaximized);
    }

    public BooleanProperty applicationWindowMaximizedProperty() {
        return applicationWindowMaximized;
    }

    public Boolean isEditFileSortTitle() {
        return editFileSortTitle.get();
    }

    public void setEditFileSortTitle(final Boolean editFileSortTitle) {
        this.editFileSortTitle.set(editFileSortTitle);
    }

    public BooleanProperty editFileSortTitleProperty() {
        return editFileSortTitle;
    }

    public Boolean isEditFileSortAlbum() {
        return editFileSortAlbum.get();
    }

    public void setEditFileSortAlbum(final Boolean editFileSortAlbum) {
        this.editFileSortAlbum.set(editFileSortAlbum);
    }

    public BooleanProperty editFileSortAlbumProperty() {
        return editFileSortAlbum;
    }

    public Boolean isEditFileSortArtist() {
        return editFileSortArtist.get();
    }

    public void setEditFileSortArtist(final Boolean editFileSortArtist) {
        this.editFileSortArtist.set(editFileSortArtist);
    }

    public BooleanProperty editFileSortArtistProperty() {
        return editFileSortArtist;
    }

    public Boolean isEditFileSynchronizeTitle() {
        return editFileSynchronizeTitle.get();
    }

    public void setEditFileSynchronizeTitle(final Boolean editFileSynchronizeTitle) {
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

}
