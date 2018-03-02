package de.pheru.media.desktop.cdi.producers;

import de.pheru.media.desktop.cdi.qualifiers.CurrentAudioLibrary;
import de.pheru.media.desktop.data.AudioLibrary;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class AudioLibraryPropertyProducer {

    @Produces
    @CurrentAudioLibrary
    @ApplicationScoped
    public ObjectProperty<AudioLibrary> currentAudioLibrary() {
        return new SimpleObjectProperty<>();
    }
}
