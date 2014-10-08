package de.eru.mp3manager;

import de.eru.mp3manager.gui.applicationwindow.main.MainColumn;
import de.eru.mp3manager.utils.SortedProperties;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javax.annotation.PostConstruct;

/**
 * Klasse zum Speichern und Auslesen von Einstellungen.
 *
 * @author Philipp Bruckner
 */
public class Settings {

    private final String absolutePath = getClass().getProtectionDomain().getCodeSource().getLocation().getPath() + "settings.properties";

    private final StringProperty musicDirectory = new SimpleStringProperty("");

    private final BooleanProperty applicationWindowFullScreen = new SimpleBooleanProperty(false);
    private final DoubleProperty applicationWindowWith = new SimpleDoubleProperty(1300.0);
    private final DoubleProperty applicationWindowHeight = new SimpleDoubleProperty(800.0);

    private final Map<String, BooleanProperty> mainColumnVisibilities = new HashMap<>();
    private final Map<String, DoubleProperty> mainColumnWidths = new HashMap<>();
    private final ObservableList<String> mainColumnsOrder = FXCollections.observableArrayList(MainColumn.FILENAME.columnName(),
            MainColumn.TITLE.columnName(), MainColumn.ALBUM.columnName(), MainColumn.ARTIST.columnName(), 
            MainColumn.TRACK.columnName(), MainColumn.GENRE.columnName(), MainColumn.DURATION.columnName(), 
            MainColumn.YEAR.columnName(), MainColumn.LAST_MODIFIED.columnName(), MainColumn.SIZE.columnName());

    private final DoubleProperty musicPlayerVolume = new SimpleDoubleProperty(100.0);
    private final BooleanProperty musicPlayerRepeat = new SimpleBooleanProperty(false);
    private final BooleanProperty musicPlayerRandom = new SimpleBooleanProperty(false);

    @PostConstruct
    private void init() {
        for (MainColumn column : MainColumn.values()) {
            mainColumnVisibilities.put(column.columnName(), new SimpleBooleanProperty(true));
            mainColumnWidths.put(column.columnName(), new SimpleDoubleProperty(60.0));
        }
        load();
    }

    public void load() {
        SortedProperties properties = new SortedProperties();
        try {
            properties.load(new FileReader(absolutePath));
            loadProperties(properties);
        } catch (IOException ex) {
            save();
            load();
//            ex.printStackTrace();
        }
    }

    private void loadProperties(Properties properties) {
        musicDirectory.set(properties.getProperty("musicDirectory"));
        applicationWindowFullScreen.set(Boolean.valueOf(properties.getProperty("applicationWindowFullScreen")));
        
        mainColumnsOrder.clear();
        String[] mainColumnsOrderSplit = properties.getProperty("mainColumnsOrder").split(",");
        for (String column : mainColumnsOrderSplit) {
            mainColumnsOrder.add(column);
        }
    }

    public void save() {
        try {
            SortedProperties properties = new SortedProperties();
            saveProperties(properties);
            properties.store(new FileOutputStream(absolutePath), null);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void saveProperties(Properties properties) {
        properties.setProperty("musicDirectory", musicDirectory.get());
        properties.setProperty("applicationWindowFullScreen", String.valueOf(applicationWindowFullScreen.get()));
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mainColumnsOrder.size(); i++) {
            sb.append(mainColumnsOrder.get(i));
            if (i < mainColumnsOrder.size() - 1) {
                sb.append(",");
            }
        }
        properties.setProperty("mainColumnsOrder", sb.toString());
    }

    public ObservableList<String> getMainColumnsOrder() {
        return mainColumnsOrder;
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

    public Boolean isApplicationWindowFullScreen() {
        return applicationWindowFullScreen.get();
    }

    public void setApplicationWindowFullScreen(final Boolean applicationWindowFullScreen) {
        this.applicationWindowFullScreen.set(applicationWindowFullScreen);
    }

    public BooleanProperty applicationWindowFullScreenProperty() {
        return applicationWindowFullScreen;
    }

    public Double getApplicationWindowWith() {
        return applicationWindowWith.get();
    }

    public void setApplicationWindowWith(final Double applicationWindowWith) {
        this.applicationWindowWith.set(applicationWindowWith);
    }

    public DoubleProperty applicationWindowWithProperty() {
        return applicationWindowWith;
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

}
