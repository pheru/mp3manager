package de.pheru.media.core.data.loader;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DefaultAudioFileLoaderProvider implements AudioFileLoaderProvider {

    private final List<AudioFileLoader> audioFileLoaders;
    private final List<String> supportedFileExtensions;

    public DefaultAudioFileLoaderProvider(final AudioFileLoader... audioFileLoaders) {
        this.audioFileLoaders = Arrays.asList(audioFileLoaders);
        this.supportedFileExtensions = new ArrayList<>();
        for (final AudioFileLoader audioFileLoader : audioFileLoaders) {
            supportedFileExtensions.addAll(audioFileLoader.getSupportedExtensions());
        }
    }

    @Override
    public List<String> getSupportedFileExtensions() {
        return supportedFileExtensions;
    }

    @Override
    public AudioFileLoader getLoaderForFile(final File file) {
        for (final AudioFileLoader loader : audioFileLoaders) {
            for (final String supportedExtension : loader.getSupportedExtensions()) {
                if (file.getName().endsWith(supportedExtension)) {
                    return loader;
                }
            }
        }
        throw new UnsupportedAudioFileFormatException("No AudioFileLoader for file \"" + file.getName() + "\" found!");
    }
}
