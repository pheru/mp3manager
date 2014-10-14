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

    private final BooleanProperty applicationWindowMaximized = new SimpleBooleanProperty(false);
    private final DoubleProperty applicationWindowWidth = new SimpleDoubleProperty(1300.0);
    private final DoubleProperty applicationWindowHeight = new SimpleDoubleProperty(800.0);

    private final Map<String, BooleanProperty> mainColumnVisibilities = new HashMap<>();
    private final Map<String, DoubleProperty> mainColumnWidths = new HashMap<>();
    private final ObservableList<String> mainColumnsOrder = FXCollections.observableArrayList();

    private final DoubleProperty musicPlayerVolume = new SimpleDoubleProperty(100.0);
    private final BooleanProperty musicPlayerRepeat = new SimpleBooleanProperty(false);
    private final BooleanProperty musicPlayerRandom = new SimpleBooleanProperty(false);

    @PostConstruct
    private void init() {
        for (MainColumn column : MainColumn.values()) {
            mainColumnVisibilities.put(column.getColumnName(), new SimpleBooleanProperty(column.isDefaultVisible()));
            mainColumnWidths.put(column.getColumnName(), new SimpleDoubleProperty(column.getDefaultWidth()));
            mainColumnsOrder.add(column.getColumnName());
        }
        load();
    }

    public void load() {
        SortedProperties properties = new SortedProperties();
        try {
            properties.load(new FileReader(absolutePath));
            try {
                loadProperties(properties);
            } catch (NumberFormatException e) {
                System.out.println("Fehlerhafte Settings-Datei!");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void loadProperties(Properties properties) {
        musicDirectory.set(properties.getProperty("musicDirectory"));
        applicationWindowMaximized.set(Boolean.valueOf(properties.getProperty("applicationWindowFullScreen")));
        applicationWindowWidth.set(Double.valueOf(properties.getProperty("applicationWindowWidth")));
        applicationWindowHeight.set(Double.valueOf(properties.getProperty("applicationWindowHeight")));
        musicPlayerVolume.set(Double.valueOf(properties.getProperty("musicPlayerVolume")));
        musicPlayerRandom.set(Boolean.valueOf(properties.getProperty("musicPlayerRandom")));
        musicPlayerRepeat.set(Boolean.valueOf(properties.getProperty("musicPlayerRepeat")));

        mainColumnsOrder.clear();
        String[] mainColumnsOrderSplit = properties.getProperty("mainColumnsOrder").split(",");
        for (String column : mainColumnsOrderSplit) {
            mainColumnsOrder.add(column);
        }
        for (Map.Entry<String, BooleanProperty> entrySet : mainColumnVisibilities.entrySet()) {
            entrySet.getValue().set(Boolean.valueOf(properties.getProperty(entrySet.getKey() + "Visible")));
        }
        for (Map.Entry<String, DoubleProperty> entrySet : mainColumnWidths.entrySet()) {
            entrySet.getValue().set(Double.valueOf(properties.getProperty(entrySet.getKey() + "Width")));
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
        properties.setProperty("applicationWindowFullScreen", String.valueOf(applicationWindowMaximized.get()));
        properties.setProperty("applicationWindowWidth", String.valueOf(applicationWindowWidth.get()));
        properties.setProperty("applicationWindowHeight", String.valueOf(applicationWindowHeight.get()));
        properties.setProperty("musicPlayerVolume", String.valueOf(musicPlayerVolume.get()));
        properties.setProperty("musicPlayerRepeat", String.valueOf(musicPlayerRepeat.get()));
        properties.setProperty("musicPlayerRandom", String.valueOf(musicPlayerRandom.get()));

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mainColumnsOrder.size(); i++) {
            sb.append(mainColumnsOrder.get(i));
            if (i < mainColumnsOrder.size() - 1) {
                sb.append(",");
            }
        }
        properties.setProperty("mainColumnsOrder", sb.toString());
        for (Map.Entry<String, BooleanProperty> entrySet : mainColumnVisibilities.entrySet()) {
            properties.setProperty(entrySet.getKey() + "Visible", String.valueOf(entrySet.getValue().get()));
        }
        for (Map.Entry<String, DoubleProperty> entrySet : mainColumnWidths.entrySet()) {
            properties.setProperty(entrySet.getKey() + "Width", String.valueOf(entrySet.getValue().get()));
        }
    }

    public Map<String, BooleanProperty> mainColumnVisibleProperties() {
        return mainColumnVisibilities;
    }

    public Map<String, DoubleProperty> mainColumnWidthProperties() {
        return mainColumnWidths;
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

}
