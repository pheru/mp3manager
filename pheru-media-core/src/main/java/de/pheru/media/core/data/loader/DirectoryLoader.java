package de.pheru.media.core.data.loader;

import java.io.File;
import java.util.List;

public interface DirectoryLoader {

    List<File> collectAudioFiles(final List<String> supportedFileExtensions, final String directory);
}
