package de.pheru.media.cdi.producers;

import de.pheru.fx.mvp.ObservableListWrapper;
import de.pheru.media.cdi.qualifiers.TableData;
import de.pheru.media.data.Mp3FileData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

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
    public ObservableListWrapper<Mp3FileData> mainSelected() {
        // Die Liste der ausgewählten Elemente einer Tabelle kann nicht von außen ersetzt werden.
        // Nachdem es Probleme mit dem Binding gab, wird hier keine neue Liste, sondern
        // ein Wrapper für die tatsächliche Liste erzeugt.
        return new ObservableListWrapper<>();
    }

}
