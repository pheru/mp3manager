package de.pheru.media.settings.objectproperties;

import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;

/**
 * Wrapper für XML-Adapter.
 */
public class PosProperty extends SimpleObjectProperty<Pos>{

    public PosProperty() {
    }

    public PosProperty(Pos initialValue) {
        super(initialValue);
    }

    public PosProperty(Object bean, String name) {
        super(bean, name);
    }

    public PosProperty(Object bean, String name, Pos initialValue) {
        super(bean, name, initialValue);
    }
    
}
