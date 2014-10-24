package de.eru.mp3manager.data;

import de.eru.mp3manager.Settings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * Klasse zum verwalten einer Wiedergabeliste.
 *
 * @author Philipp Bruckner
 */
@ApplicationScoped
public class Playlist {

    @Inject
    private Settings settings;
    
    public static final String FILE_EXTENSION = "mmpl";
    public static final String FILE_SPLIT = "</>";

    private String absolutePath;
    private final ObservableList<Mp3FileData> titles = FXCollections.observableArrayList();
    private final ObservableList<Integer> indicesToPlay = FXCollections.observableArrayList();
    private final IntegerProperty currentIndex = new SimpleIntegerProperty(0);

    public Mp3FileData getNextTitle() {
        if(indicesToPlay.indexOf(currentIndex.get()) == indicesToPlay.size() - 1){
            currentIndex.set(0);
        }
        return titles.get(currentIndex.get());
    }

    public Mp3FileData getPreviousTitle() {
        return titles.get(currentIndex.get());
    }

    public ObservableList<Mp3FileData> getTitles() {
        return titles;
    }

    public ObservableList<Integer> getTitlesToPlay() {
        return indicesToPlay;
    }

    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public Integer getCurrentIndex() {
        return currentIndex.get();
    }

    public void setCurrentIndex(final Integer currentIndex) {
        this.currentIndex.set(currentIndex);
    }

    public IntegerProperty currentIndexProperty() {
        return currentIndex;
    }
}
