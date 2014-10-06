package de.eru.mp3manager;

import de.eru.mp3manager.gui.applicationwindow.main.ColumnNames;
import de.eru.mp3manager.utils.SortedProperties;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Klasse zum Speichern und Auslesen von Einstellungen.
 *
 * @author Philipp Bruckner
 */
public final class Settings {

    public static final Settings INSTANCE = new Settings();

    private final String absolutePath = getClass().getProtectionDomain().getCodeSource().getLocation().getPath() + "settings.properties";

    private final StringProperty musicDirectory = new SimpleStringProperty("");
    
    private final BooleanProperty applicationWindowFullScreen = new SimpleBooleanProperty(false);
    private final DoubleProperty applicationWindowWith = new SimpleDoubleProperty(800.0);
    private final DoubleProperty applicationWindowHeight = new SimpleDoubleProperty(500.0);
    
    private final BooleanProperty mainColumnFileNameVisible = new SimpleBooleanProperty(true);
    private final BooleanProperty mainColumnTitleVisible = new SimpleBooleanProperty(true);
    private final BooleanProperty mainColumnAlbumVisible = new SimpleBooleanProperty(true);
    private final BooleanProperty mainColumnArtistVisible = new SimpleBooleanProperty(true);
    private final BooleanProperty mainColumnTrackVisible = new SimpleBooleanProperty(true);
    private final BooleanProperty mainColumnGenreVisible = new SimpleBooleanProperty(true);
    private final BooleanProperty mainColumnDurationVisible = new SimpleBooleanProperty(true);
    private final BooleanProperty mainColumnYearVisible = new SimpleBooleanProperty(true);
    private final BooleanProperty mainColumnLastModifiedVisible = new SimpleBooleanProperty(true);
    private final BooleanProperty mainColumnSizeVisible = new SimpleBooleanProperty(true);
    private final StringProperty mainColumn1 = new SimpleStringProperty(ColumnNames.FILENAME);
    private final StringProperty mainColumn2 = new SimpleStringProperty(ColumnNames.TITLE);
    private final StringProperty mainColumn3 = new SimpleStringProperty(ColumnNames.ALBUM);
    private final StringProperty mainColumn4 = new SimpleStringProperty(ColumnNames.ARTIST);
    private final StringProperty mainColumn5 = new SimpleStringProperty(ColumnNames.TRACK);
    private final StringProperty mainColumn6 = new SimpleStringProperty(ColumnNames.GENRE);
    private final StringProperty mainColumn7 = new SimpleStringProperty(ColumnNames.DURATION);
    private final StringProperty mainColumn8 = new SimpleStringProperty(ColumnNames.YEAR);
    private final StringProperty mainColumn9 = new SimpleStringProperty(ColumnNames.LAST_MODIFIED);
    private final StringProperty mainColumn10 = new SimpleStringProperty(ColumnNames.SIZE);
    
    private final DoubleProperty musicPlayerVolume = new SimpleDoubleProperty(100.0);
    private final BooleanProperty musicPlayerRepeat = new SimpleBooleanProperty(false);
    private final BooleanProperty musicPlayerRandom = new SimpleBooleanProperty(false);

    private Settings() {
        load();
    }

    public void load() {
        SortedProperties properties = new SortedProperties();
        try {
            properties.load(new FileReader(absolutePath));
            musicDirectory.set(properties.getProperty("musicDirectory"));
            applicationWindowFullScreen.set(Boolean.valueOf(properties.getProperty("fullScreen")));
        } catch (IOException ex) {
            save();
            load();
//            ex.printStackTrace();
        }
    }

    public void save() {
        try {
            SortedProperties properties = new SortedProperties();
            properties.load(new FileReader(absolutePath));
            properties.setProperty("musicDirectory", musicDirectory.get());
            properties.setProperty("fullScreen", String.valueOf(applicationWindowFullScreen.get()));
            properties.store(new FileOutputStream(absolutePath), null);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
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

    public Boolean isMainColumnFileNameVisible() {
        return mainColumnFileNameVisible.get();
    }

    public void setMainColumnFileNameVisible(final Boolean mainColumnFileNameVisible) {
        this.mainColumnFileNameVisible.set(mainColumnFileNameVisible);
    }

    public BooleanProperty mainColumnFileNameVisibleProperty() {
        return mainColumnFileNameVisible;
    }

    public Boolean isMainColumnTitleVisible() {
        return mainColumnTitleVisible.get();
    }

    public void setMainColumnTitleVisible(final Boolean mainColumnTitleVisible) {
        this.mainColumnTitleVisible.set(mainColumnTitleVisible);
    }

    public BooleanProperty mainColumnTitleVisibleProperty() {
        return mainColumnTitleVisible;
    }

    public Boolean isMainColumnAlbumVisible() {
        return mainColumnAlbumVisible.get();
    }

    public void setMainColumnAlbumVisible(final Boolean mainColumnAlbumVisible) {
        this.mainColumnAlbumVisible.set(mainColumnAlbumVisible);
    }

    public BooleanProperty mainColumnAlbumVisibleProperty() {
        return mainColumnAlbumVisible;
    }

    public Boolean isMainColumnArtistVisible() {
        return mainColumnArtistVisible.get();
    }

    public void setMainColumnArtistVisible(final Boolean mainColumnArtistVisible) {
        this.mainColumnArtistVisible.set(mainColumnArtistVisible);
    }

    public BooleanProperty mainColumnArtistVisibleProperty() {
        return mainColumnArtistVisible;
    }

    public Boolean isMainColumnTrackVisible() {
        return mainColumnTrackVisible.get();
    }

    public void setMainColumnTrackVisible(final Boolean mainColumnTrackVisible) {
        this.mainColumnTrackVisible.set(mainColumnTrackVisible);
    }

    public BooleanProperty mainColumnTrackVisibleProperty() {
        return mainColumnTrackVisible;
    }

    public Boolean isMainColumnGenreVisible() {
        return mainColumnGenreVisible.get();
    }

    public void setMainColumnGenreVisible(final Boolean mainColumnGenreVisible) {
        this.mainColumnGenreVisible.set(mainColumnGenreVisible);
    }

    public BooleanProperty mainColumnGenreVisibleProperty() {
        return mainColumnGenreVisible;
    }

    public Boolean isMainColumnDurationVisible() {
        return mainColumnDurationVisible.get();
    }

    public void setMainColumnDurationVisible(final Boolean mainColumnDurationVisible) {
        this.mainColumnDurationVisible.set(mainColumnDurationVisible);
    }

    public BooleanProperty mainColumnDurationVisibleProperty() {
        return mainColumnDurationVisible;
    }

    public Boolean isMainColumnYearVisible() {
        return mainColumnYearVisible.get();
    }

    public void setMainColumnYearVisible(final Boolean mainColumnYearVisible) {
        this.mainColumnYearVisible.set(mainColumnYearVisible);
    }

    public BooleanProperty mainColumnYearVisibleProperty() {
        return mainColumnYearVisible;
    }

    public Boolean isMainColumnLastModifiedVisible() {
        return mainColumnLastModifiedVisible.get();
    }

    public void setMainColumnLastModifiedVisible(final Boolean mainColumnLastModifiedVisible) {
        this.mainColumnLastModifiedVisible.set(mainColumnLastModifiedVisible);
    }

    public BooleanProperty mainColumnLastModifiedVisibleProperty() {
        return mainColumnLastModifiedVisible;
    }

    public Boolean isMainColumnSizeVisible() {
        return mainColumnSizeVisible.get();
    }

    public void setMainColumnSizeVisible(final Boolean mainColumnSizeVisible) {
        this.mainColumnSizeVisible.set(mainColumnSizeVisible);
    }

    public BooleanProperty mainColumnSizeVisibleProperty() {
        return mainColumnSizeVisible;
    }

    public String getMainColumn1() {
        return mainColumn1.get();
    }

    public void setMainColumn1(final String mainColumn1) {
        this.mainColumn1.set(mainColumn1);
    }

    public StringProperty mainColumn1Property() {
        return mainColumn1;
    }

    public String getMainColumn2() {
        return mainColumn2.get();
    }

    public void setMainColumn2(final String mainColumn2) {
        this.mainColumn2.set(mainColumn2);
    }

    public StringProperty mainColumn2Property() {
        return mainColumn2;
    }

    public String getMainColumn3() {
        return mainColumn3.get();
    }

    public void setMainColumn3(final String mainColumn3) {
        this.mainColumn3.set(mainColumn3);
    }

    public StringProperty mainColumn3Property() {
        return mainColumn3;
    }

    public String getMainColumn4() {
        return mainColumn4.get();
    }

    public void setMainColumn4(final String mainColumn4) {
        this.mainColumn4.set(mainColumn4);
    }

    public StringProperty mainColumn4Property() {
        return mainColumn4;
    }

    public String getMainColumn5() {
        return mainColumn5.get();
    }

    public void setMainColumn5(final String mainColumn5) {
        this.mainColumn5.set(mainColumn5);
    }

    public StringProperty mainColumn5Property() {
        return mainColumn5;
    }

    public String getMainColumn6() {
        return mainColumn6.get();
    }

    public void setMainColumn6(final String mainColumn6) {
        this.mainColumn6.set(mainColumn6);
    }

    public StringProperty mainColumn6Property() {
        return mainColumn6;
    }

    public String getMainColumn7() {
        return mainColumn7.get();
    }

    public void setMainColumn7(final String mainColumn7) {
        this.mainColumn7.set(mainColumn7);
    }

    public StringProperty mainColumn7Property() {
        return mainColumn7;
    }

    public String getMainColumn8() {
        return mainColumn8.get();
    }

    public void setMainColumn8(final String mainColumn8) {
        this.mainColumn8.set(mainColumn8);
    }

    public StringProperty mainColumn8Property() {
        return mainColumn8;
    }

    public String getMainColumn9() {
        return mainColumn9.get();
    }

    public void setMainColumn9(final String mainColumn9) {
        this.mainColumn9.set(mainColumn9);
    }

    public StringProperty mainColumn9Property() {
        return mainColumn9;
    }

    public String getMainColumn10() {
        return mainColumn10.get();
    }

    public void setMainColumn10(final String mainColumn10) {
        this.mainColumn10.set(mainColumn10);
    }

    public StringProperty mainColumn10Property() {
        return mainColumn10;
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
