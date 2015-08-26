package de.pheru.media.cdi.producers;

import de.pheru.fx.mvp.InjectableList;
import de.pheru.media.cdi.qualifiers.TableData;
import de.pheru.media.data.Mp3FileData;
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
