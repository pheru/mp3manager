package de.eru.mp3manager.data.utils;

import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javax.annotation.PostConstruct;

/**
 *TODO
 * @author Philipp Bruckner
 * @param <T> TODO
 */
public class InjectableList<T> extends SimpleListProperty<T>{

    @PostConstruct
    private void init(){
        set(FXCollections.observableArrayList());
    }
}
