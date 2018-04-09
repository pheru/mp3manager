package de.pheru.media.core.data.loader;

import de.pheru.media.core.data.model.AudioFile;

import java.io.File;
import java.util.List;

public interface AudioFileLoader {

    List<String> getSupportedExtensions();

    AudioFile load(final File file, final ArtworkCreator artworkCreator) throws AudioFileLoaderException;

}
