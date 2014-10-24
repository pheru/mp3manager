package de.eru.mp3manager.data;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javax.enterprise.context.ApplicationScoped;

/**
 * Klasse zum verwalten einer Wiedergabeliste.
 *
 * @author Philipp Bruckner
 */
@ApplicationScoped
public class Playlist {

    public static final String FILE_EXTENSION = "mmpl";
    public static final String FILE_SPLIT = "</>";

    private String absolutePath = "D:\\Wiedergabeliste1.mmpl";
    private final ObservableList<Mp3FileData> titles = FXCollections.observableArrayList();
    private final ObservableList<Mp3FileData> titlesToPlay = FXCollections.observableArrayList();
    private final ObjectProperty<Mp3FileData> currentTitle = new SimpleObjectProperty<>();

    public ObservableList<Mp3FileData> getTitles() {
        return titles;
    }

    public ObservableList<Mp3FileData> getTitlesToPlay() {
        return titlesToPlay;
    }

    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public Mp3FileData getCurrentTitle() {
        return currentTitle.get();
    }

    public void setCurrentTitle(final Mp3FileData currentTitle) {
        this.currentTitle.set(currentTitle);
    }

    public ObjectProperty<Mp3FileData> currentTitleProperty() {
        return currentTitle;
    }
}
