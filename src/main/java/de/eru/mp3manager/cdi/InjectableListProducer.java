package de.eru.mp3manager.cdi;

import de.eru.mp3manager.data.Mp3FileData;
import de.eru.pherufx.utils.InjectableList;
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
    @SelectedTableData(source = TableDataSource.MAIN)
    public InjectableList<Mp3FileData> selectedMain() {
        return selectedMain;
    }

    @Produces
    @SelectedTableData(source = TableDataSource.PLAYLIST)
    public InjectableList<Mp3FileData> selectedPlaylist() {
        return selectedPlaylist;
    }

    @Produces
    @TableData(source = TableDataSource.MAIN)
    public InjectableList<Mp3FileData> main() {
        return main;
    }
}
