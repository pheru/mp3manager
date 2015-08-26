package de.eru.mp3manager.cdi.producers;

import de.eru.mp3manager.cdi.qualifiers.TableData;
import de.eru.mp3manager.data.Mp3FileData;
import de.eru.pherufx.mvp.InjectableList;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.New;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

/**
 *
 * @author Philipp
 */
@ApplicationScoped
public class InjectableListProducer {

    @Inject
    @New
    InjectableList<Mp3FileData> selectedMain;
    @Inject
    @New
    InjectableList<Mp3FileData> selectedPlaylist;
    @Inject
    @New
    InjectableList<Mp3FileData> main;

    @Produces
    @TableData(TableData.Source.MAIN_SELECTED)
    public InjectableList<Mp3FileData> selectedMain() {
        return selectedMain;
    }

    @Produces
    @TableData(TableData.Source.PLAYLIST_SELECTED)
    public InjectableList<Mp3FileData> selectedPlaylist() {
        return selectedPlaylist;
    }

    @Produces
    @TableData(TableData.Source.MAIN)
    public InjectableList<Mp3FileData> main() {
        return main;
    }
}
