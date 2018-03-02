package de.pheru.media.desktop;

import de.pheru.fx.mvp.PheruFXLoader;
import de.pheru.fx.util.properties.ObservableProperties;
import de.pheru.media.core.io.file.FileIO;
import de.pheru.media.desktop.cdi.qualifiers.*;
import de.pheru.media.desktop.data.AudioLibrary;
import de.pheru.media.desktop.data.AudioLibraryData;
import javafx.beans.property.ObjectProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class DesktopApplicationLoader extends PheruFXLoader {

    private static final Logger LOGGER = LogManager.getLogger(DesktopApplicationLoader.class);

    @Inject
    @Settings
    private ObservableProperties settings;
    @Inject
    @AudioLibraryIO
    private FileIO audioLibraryIO;
    @Inject
    @AudioLibraryDataIO
    private FileIO audioLibraryDataIO;
    @Inject
    @CurrentAudioLibrary
    private ObjectProperty<AudioLibrary> currentAudioLibrary;
    @Inject
    @CurrentAudioLibraryData
    private ObjectProperty<AudioLibraryData> currentAudioLibraryData;

    @Override
    public void load() throws Exception {
        updateMessage("Lade aktuelle Musik-Bibliothek...");
        updateProgress(0, 100);
        loadCurrentAudioLibrary();
        updateProgress(50, 100);
        loadCurrentAudioLibraryData();
        updateProgress(100, 100);
    }

    private void loadCurrentAudioLibrary() {
        final String currentLibraryFileName = settings.stringProperty(Setting.CURRENT_AUDIO_LIBRARY_FILENAME).get();
        try {
            currentAudioLibrary.set(audioLibraryIO.read(
                    new File(AudioLibrary.DIRECTORY + "/" + currentLibraryFileName),
                    AudioLibrary.class));
        } catch (final IOException e) {
            if (e instanceof FileNotFoundException) {
                LOGGER.info("No current audiolibrary found.");
            } else {
                LOGGER.error("Exception loading current audiolibrary!", e);
                //TODO allg. fehler beim audiolibrary laden
            }
        }
    }

    private void loadCurrentAudioLibraryData() {
        if (currentAudioLibrary.get() == null) {
            LOGGER.info("Loading data for current audiolibrary skipped.");
            return;
        }
        try {
            currentAudioLibraryData.set(audioLibraryDataIO.read(
                    new File(AudioLibraryData.DIRECTORY + "/" + currentAudioLibrary.get().getFileName()),
                    AudioLibraryData.class));
        } catch (final IOException e) {
            if (e instanceof FileNotFoundException) {
                LOGGER.info("No data for current audiolibrary found.");
            } else {
                LOGGER.error("Exception loading data for current audiolibrary!", e);
                //TODO allg. fehler beim audiolibrarydata laden
            }
        }
    }
}
