package de.eru.mp3manager.data;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author Philipp Bruckner
 */
public class Artist {
    
    private final ObservableList<Album> albums = FXCollections.observableArrayList();
    private final StringProperty name = new SimpleStringProperty("");
    
    public ObservableList<Album> getAlbums(){
        return albums;
    }
    
    public StringProperty nameProperty(){
        return name;
    }
    
    public String getName(){
        return name.get();
    }
}
