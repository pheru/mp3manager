package de.eru.mp3manager.data;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

/**
 *
 * @author Philipp Bruckner
 */
public class Album {

    private final ObservableList<Mp3FileData> songs = FXCollections.observableArrayList();
    private final StringProperty name = new SimpleStringProperty("");
    private final StringProperty genre = new SimpleStringProperty("");
    private final BooleanProperty genreConflict = new SimpleBooleanProperty(false);
    private final StringProperty year = new SimpleStringProperty("");
    private final BooleanProperty yearConflict = new SimpleBooleanProperty(false);
    private Byte[] cover;
    private final BooleanProperty coverConflict = new SimpleBooleanProperty(false);

    public Album(){
        songs.addListener(new ListChangeListener<Mp3FileData>() {
            @Override
            public void onChanged(ListChangeListener.Change<? extends Mp3FileData> change) {
                
            }
        });
    }
    
    public ObservableList<Mp3FileData> getSongs() {
        return songs;
    }

    public StringProperty nameProperty() {
        return name;
    }

    public String getName() {
        return name.get();
    }
    
    public StringProperty genreProperty() {
        return genre;
    }

    public String getGenre() {
        return genre.get();
    }
    
    public StringProperty yearProperty() {
        return year;
    }

    public String getYear() {
        return year.get();
    }
    
    public Byte[] getCover() {
        return cover;
    }

}
