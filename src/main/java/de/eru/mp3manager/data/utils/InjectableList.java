package de.eru.mp3manager.data.utils;

import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javax.annotation.PostConstruct;

/**
 *
 * @author Philipp Bruckner
 * @param <T> TODO
 */
public class InjectableList<T> extends SimpleListProperty<T>{

    private final List<ListChangeListener> listeners = new ArrayList<>();
    
    @PostConstruct
    private void init(){
        set(FXCollections.observableArrayList());
    }

    @Override
    public void addListener(ListChangeListener<? super T> listener) {
        listeners.add(listener);
        super.addListener(listener);
    }

    @Override
    public void removeListener(ListChangeListener<? super T> listener) {
        listeners.remove(listener);
        super.removeListener(listener);
    }

    @Override
    public void set(ObservableList<T> newValue) {
        super.set(newValue);
        for (ListChangeListener listChangeListener : listeners) {
            super.addListener(listChangeListener);
        }
    }
    
    
    
}
