package de.eru.mp3manager.cdi;

import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javax.annotation.PostConstruct;

/**
 * @author Philipp Bruckner
 */
public class InjectableList<T> extends SimpleListProperty<T>{

    @PostConstruct
    private void init(){
        set(FXCollections.observableArrayList());
    }
}
