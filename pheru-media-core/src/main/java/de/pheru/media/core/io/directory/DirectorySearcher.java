package de.pheru.media.core.io.directory;

import java.io.File;
import java.util.List;

public interface DirectorySearcher {

    List<File> searchFiles(final List<String> supportedFileExtensions, final String directory);
}
