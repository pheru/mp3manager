package de.pheru.media.cdi.producers;

import de.pheru.media.cdi.qualifiers.TableData;
import de.pheru.media.data.Mp3FileData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

/**
 *
 * @author Philipp
 */
@ApplicationScoped
public class ObservableListProducer {

    @Produces
    @TableData(TableData.Source.MAIN)
    @ApplicationScoped
    public ObservableList<Mp3FileData> main() {
        return FXCollections.observableArrayList();
    }

    @Produces
    @TableData(TableData.Source.MAIN_SELECTED)
    @ApplicationScoped
    public ObservableList<Mp3FileData> mainSelected() {
        return FXCollections.observableArrayList();
    }

    @Produces
    @TableData(TableData.Source.PLAYLIST_SELECTED)
    @ApplicationScoped
    public ObservableList<Mp3FileData> playlistSelected() {
        return FXCollections.observableArrayList();
    }
}
