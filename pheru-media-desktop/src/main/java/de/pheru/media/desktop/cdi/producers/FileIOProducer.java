package de.pheru.media.desktop.cdi.producers;

import de.pheru.media.core.io.file.FileIO;
import de.pheru.media.core.io.file.XmlIO;
import de.pheru.media.desktop.cdi.qualifiers.AudioLibraryDataIO;
import de.pheru.media.desktop.cdi.qualifiers.AudioLibraryIO;

import javax.enterprise.inject.Produces;

public class FileIOProducer {

    @Produces
    @AudioLibraryIO
    public FileIO audioLibraryIO(){
        return new XmlIO();
    }

    @Produces
    @AudioLibraryDataIO
    public FileIO audioLibraryDataIO(){
        return new XmlIO();
    }
}
