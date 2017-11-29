package de.pheru.media.desktop.cdi.producers;

import de.pheru.fx.util.properties.ObservableProperties;
import de.pheru.media.core.data.model.AudioFile;
import de.pheru.media.desktop.Setting;
import de.pheru.media.desktop.cdi.qualifiers.AudioFiles;
import de.pheru.media.desktop.cdi.qualifiers.CurrentAudioLibrary;
import de.pheru.media.desktop.cdi.qualifiers.Settings;
import de.pheru.media.desktop.data.AudioLibrary;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

@ApplicationScoped
public class AudioLibraryProducer {

    @Inject
    @Settings
    private ObservableProperties settings;

    @Produces
    @CurrentAudioLibrary
    @ApplicationScoped
    public AudioLibrary currentAudioLibrary() {
        final AudioLibrary currentAudioLibrary = new AudioLibrary();
        currentAudioLibrary.nameProperty().bindBidirectional(settings.stringProperty(Setting.CURRENT_AUDIO_LIBRARY));

        return currentAudioLibrary;
    }
}
