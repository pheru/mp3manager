package de.pheru.media.core.io.directory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DefaultDirectorySearcher implements DirectorySearcher {

    @Override
    public List<File> searchFiles(final List<String> supportedFileExtensions, final String directory) {
        final List<File> fileList = new ArrayList<>();
        collectSupportedFiles(supportedFileExtensions, directory, fileList);
        return fileList;
    }

    private void collectSupportedFiles(final List<String> supportedFileExtensions, final String directory, final List<File> fileList) {
        final File dir = new File(directory);
        final File[] files = dir.listFiles();

        if (files != null) {
            for (final File file : files) {
                if (file.isDirectory()) {
                    collectSupportedFiles(supportedFileExtensions, file.getAbsolutePath(), fileList);
                } else if (isSupported(supportedFileExtensions, file)) {
                    fileList.add(file);
                }
            }
        }
    }

    private boolean isSupported(final List<String> supportedFileExtensions, final File file) {
        for (final String supportedExtension : supportedFileExtensions) {
            if (file.getName().endsWith(supportedExtension)) {
                return true;
            }
        }
        return false;
    }

}
