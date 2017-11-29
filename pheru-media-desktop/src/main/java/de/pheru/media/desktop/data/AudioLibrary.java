package de.pheru.media.desktop.data;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class AudioLibrary {

    private final StringProperty name = new SimpleStringProperty();
    private final ObservableList<String> directories = FXCollections.observableArrayList();

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(final String name) {
        this.name.set(name);
    }

    public ObservableList<String> getDirectories() {
        return directories;
    }

}
