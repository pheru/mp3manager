package de.pheru.media.desktop.cdi.producers;

import de.pheru.media.core.data.loader.AudioFileLoaderProvider;
import de.pheru.media.core.data.loader.DefaultAudioFileLoaderProvider;
import de.pheru.media.core.data.loader.Mp3FileLoader;

import javax.enterprise.inject.Produces;

public class AudioFileLoaderProviderProducer {

    @Produces
    public AudioFileLoaderProvider audioFileLoaderProvider() {
        return new DefaultAudioFileLoaderProvider(new Mp3FileLoader());
    }
}
