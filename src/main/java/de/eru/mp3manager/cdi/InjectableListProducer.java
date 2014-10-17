/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.eru.mp3manager.cdi;

import de.eru.mp3manager.data.Mp3FileData;
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
