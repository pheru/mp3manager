package de.eru.mp3manager.data;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Klasse zum verwalten einer Wiedergabeliste.
 *
 * @author Philipp Bruckner
 */
public class Playlist {
    
    public static final String FILE_EXTENSION = "mmpl";
    public static final String FILE_SPLIT = "</>";
    
    private String absolutePath = "D:\\Wiedergabeliste1.mmpl";
    private ObservableList<Mp3FileData> titles = FXCollections.observableArrayList();

    public ObservableList<Mp3FileData> getTitles() {
        return titles;
    }

    public void setTitles(ObservableList<Mp3FileData> titles) {
        this.titles = titles;
    }
    
    public void setAbsolutePath(String absolutePath){
        this.absolutePath = absolutePath;
    }
    
    public String getAbsolutePath(){
        return absolutePath;
    }
}
