package de.pheru.media.desktop.cdi.producers;

import de.pheru.media.desktop.cdi.qualifiers.CurrentAudioLibraryData;
import de.pheru.media.desktop.data.AudioLibraryData;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class AudioLibraryDataPropertyProducer {

    @Produces
    @CurrentAudioLibraryData
    @ApplicationScoped
    public ObjectProperty<AudioLibraryData> currentAudioLibraryData() {
        return new SimpleObjectProperty<>();
    }
}
