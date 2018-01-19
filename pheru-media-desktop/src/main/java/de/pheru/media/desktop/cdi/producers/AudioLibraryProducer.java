package de.pheru.media.desktop.cdi.producers;

import de.pheru.fx.util.properties.ObservableProperties;
import de.pheru.media.desktop.Setting;
import de.pheru.media.desktop.cdi.qualifiers.CurrentAudioLibrary;
import de.pheru.media.desktop.cdi.qualifiers.Settings;
import de.pheru.media.desktop.data.AudioLibrary;
import javafx.beans.property.ObjectProperty;

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
    public ObjectProperty<AudioLibrary> currentAudioLibrary() {
        final String currentLibraryName = settings.stringProperty(Setting.CURRENT_AUDIO_LIBRARY).get();
        //

        return null;
    }
}
