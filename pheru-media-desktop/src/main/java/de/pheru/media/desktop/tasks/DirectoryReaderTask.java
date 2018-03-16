package de.pheru.media.desktop.tasks;

import de.pheru.media.core.data.loader.AudioFileLoaderProvider;
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

public class UpdateAudioLibraryDataTask extends Task<UpdateAudioLibraryDataTask.Result> {

    @Inject
    @CurrentAudioLibrary
    private ObjectProperty<AudioLibrary> currentAudioLibrary;
    @Inject
    @CurrentAudioLibraryData
    private ObjectProperty<AudioLibraryData> currentAudioLibraryData;
    @Inject
    private AudioFileLoaderProvider audioFileLoaderProvider;

    public void update() {

    }

    private List<File> getAllFiles() {
        final List<File> files = new ArrayList<>();
        final DirectorySearcher directorySearcher = new DefaultDirectorySearcher();
        for (final String directory : currentAudioLibrary.get().getDirectories()) {
            files.addAll(directorySearcher.searchFiles(audioFileLoaderProvider.getSupportedFileExtensions(), directory));
        }
        return files;
    }

    @Override
    protected Result call() throws Exception {
        return null;
    }

    public class Result{
        private List<File> newFiles;
        private List<File> removableFiles;
        private List<File> updateableFiles;

        public List<File> getNewFiles() {
            return newFiles;
        }

        public void setNewFiles(final List<File> newFiles) {
            this.newFiles = newFiles;
        }

        public List<File> getRemovableFiles() {
            return removableFiles;
        }

        public void setRemovableFiles(final List<File> removableFiles) {
            this.removableFiles = removableFiles;
        }

        public List<File> getUpdateableFiles() {
            return updateableFiles;
        }

        public void setUpdateableFiles(final List<File> updateableFiles) {
            this.updateableFiles = updateableFiles;
        }
    }
}
