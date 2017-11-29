package de.pheru.media.core.data.loader;

import java.io.File;
import java.util.List;

public interface AudioFileLoaderProvider {

    List<String> getSupportedFileExtensions();

    AudioFileLoader getLoaderForFile(final File file);
}
