package de.pheru.media.desktop.cdi.producers;

import de.pheru.media.core.data.model.AudioFile;
import de.pheru.media.desktop.cdi.qualifiers.AudioFiles;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class ObservableListProducer {

    @Produces
    @AudioFiles
    @ApplicationScoped
    public ObservableList<AudioFile> audioFiles() {
        return FXCollections.observableArrayList();
    }
}
