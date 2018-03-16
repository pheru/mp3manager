package de.pheru.media.desktop.tasks;

import de.pheru.media.core.data.loader.AudioFileLoaderProvider;
import de.pheru.media.core.data.model.AudioFile;
import de.pheru.media.core.io.directory.DefaultDirectorySearcher;
import de.pheru.media.core.io.directory.DirectorySearcher;
import de.pheru.media.desktop.cdi.qualifiers.CurrentAudioLibrary;
import de.pheru.media.desktop.cdi.qualifiers.CurrentAudioLibraryData;
import de.pheru.media.desktop.data.AudioLibrary;
import de.pheru.media.desktop.data.AudioLibraryData;
import javafx.beans.property.ObjectProperty;
import javafx.concurrent.Task;

import javax.inject.Inject;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DirectoryReaderTask extends Task<DirectoryReaderTask.Result> {

    @Inject
    @CurrentAudioLibrary
    private ObjectProperty<AudioLibrary> currentAudioLibrary;
    @Inject
    @CurrentAudioLibraryData
    private ObjectProperty<AudioLibraryData> currentAudioLibraryData;
    @Inject
    private AudioFileLoaderProvider audioFileLoaderProvider;

    @Override
    protected Result call() throws Exception {
        return compareFilesWithAudioLibrary(getAllFiles());
    }

    private List<File> getAllFiles() {
        final List<File> files = new ArrayList<>();
        final DirectorySearcher directorySearcher = new DefaultDirectorySearcher();
        for (final String directory : currentAudioLibrary.get().getDirectories()) {
            files.addAll(directorySearcher.searchFiles(audioFileLoaderProvider.getSupportedFileExtensions(), directory));
        }
        return files;
    }

    private Result compareFilesWithAudioLibrary(final List<File> directoryFiles) {
        final Result result = new Result();
        final List<AudioFile> audioFilesCopy = new ArrayList<>(currentAudioLibraryData.get().getAllAudioFiles());
        for (final File directoryFile : directoryFiles) {
            final AudioFile audioFile = getAudioFileByAbsolutePath(audioFilesCopy, directoryFile.getAbsolutePath());
            if (audioFile != null) {
                result.getUpdateableAudioFiles().add(audioFile);
                audioFilesCopy.remove(audioFile);
            } else {
                result.getNewFiles().add(directoryFile);
            }
        }
        result.getRemovableAudioFiles().addAll(audioFilesCopy);
        return result;
    }

    private AudioFile getAudioFileByAbsolutePath(final List<AudioFile> list, final String absolutePath) {
        for (final AudioFile audioFile : list) {
            if (audioFile.getAbsolutePath().equals(absolutePath)) {
                return audioFile;
            }
        }
        return null;
    }

    public class Result {
        private final List<File> newFiles = new ArrayList<>();
        private final List<AudioFile> removableAudioFiles = new ArrayList<>();
        private final List<AudioFile> updateableAudioFiles = new ArrayList<>();

        public List<File> getNewFiles() {
            return newFiles;
        }

        public List<AudioFile> getRemovableAudioFiles() {
            return removableAudioFiles;
        }

        public List<AudioFile> getUpdateableAudioFiles() {
            return updateableAudioFiles;
        }
    }
}
