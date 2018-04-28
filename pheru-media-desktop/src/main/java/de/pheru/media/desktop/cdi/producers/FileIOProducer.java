package de.pheru.media.desktop.cdi.producers;

import de.pheru.media.core.data.loader.ArtworkCreator;
import de.pheru.media.core.io.file.FileIO;
import de.pheru.media.core.io.file.JsonIO;
import de.pheru.media.desktop.cdi.qualifiers.AudioLibraryDataIO;
import de.pheru.media.desktop.cdi.qualifiers.AudioLibraryIO;
import de.pheru.media.desktop.cdi.qualifiers.Caching;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;

public class FileIOProducer {

    @Inject
    @Caching
    private ArtworkCreator artworkCreator;

    @Produces
    @AudioLibraryIO
    public FileIO audioLibraryIO() {
        return new JsonIO();
    }

    @Produces
    @AudioLibraryDataIO
    public FileIO audioLibraryDataIO() {
        return new JsonIO();
    }

}
