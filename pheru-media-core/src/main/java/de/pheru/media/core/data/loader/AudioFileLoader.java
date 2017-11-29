package de.pheru.media.core.data.loader;

import de.pheru.media.core.data.model.AudioFile;
import de.pheru.media.core.data.model.Artwork;

import java.io.File;
import java.util.List;

public interface AudioFileLoader {

    List<String> getSupportedExtensions();

    AudioFile load(final File file) throws AudioFileLoaderException;

    Artwork loadArtwork(final File file) throws AudioFileLoaderException;

}
