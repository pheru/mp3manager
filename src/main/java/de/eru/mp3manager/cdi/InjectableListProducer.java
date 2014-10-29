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
    InjectableList<Mp3FileData> main;
    @Inject
    @New
    InjectableList<Mp3FileData> playlist;

    @Produces
    @SelectedTableData(source = SelectedTableData.Source.MAIN)
    public InjectableList<Mp3FileData> createOne() {
        return main;
    }

    @Produces
    @SelectedTableData(source = SelectedTableData.Source.PLAYLIST)
    public InjectableList<Mp3FileData> createTwo() {
        return playlist;
    }
}
