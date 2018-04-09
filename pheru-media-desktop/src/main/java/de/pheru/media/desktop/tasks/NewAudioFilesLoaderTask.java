package de.pheru.media.desktop.tasks;

import de.pheru.media.core.data.loader.ArtworkCreator;
import de.pheru.media.core.data.loader.AudioFileLoader;
import de.pheru.media.core.data.loader.AudioFileLoaderProvider;
import de.pheru.media.core.data.model.AudioFile;
import javafx.concurrent.Task;

import java.io.File;
import java.util.List;

//TODO WIP
public class NewAudioFilesLoaderTask extends Task<List<AudioFile>> {

    private final List<File> newFiles;
    private final AudioFileLoaderProvider audioFileLoaderProvider;
    private final ArtworkCreator artworkCreator;

    public NewAudioFilesLoaderTask(final List<File> newFiles,
                                   final AudioFileLoaderProvider audioFileLoaderProvider,
                                   final ArtworkCreator artworkCreator) {
        this.newFiles = newFiles;
        this.audioFileLoaderProvider = audioFileLoaderProvider;
        this.artworkCreator = artworkCreator;
    }

    @Override
    protected List<AudioFile> call() throws Exception {
        for (final File newFile : newFiles) {
            final AudioFileLoader audioFileLoader = audioFileLoaderProvider.getLoaderForFile(newFile);
            final AudioFile audioFile = audioFileLoader.load(newFile, artworkCreator);
        }
        return null;
    }

}
