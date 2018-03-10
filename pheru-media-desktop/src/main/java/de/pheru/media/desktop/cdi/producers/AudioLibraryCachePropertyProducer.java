package de.pheru.media.desktop.cdi.producers;

import de.pheru.media.desktop.cdi.qualifiers.CurrentAudioLibraryCache;
import de.pheru.media.desktop.data.AudioLibraryCache;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class AudioLibraryCachePropertyProducer {

    @Produces
    @CurrentAudioLibraryCache
    @ApplicationScoped
    public ObjectProperty<AudioLibraryCache> currentAudioLibraryCache() {
        return new SimpleObjectProperty<>();
    }
}
