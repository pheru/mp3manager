package de.pheru.media.settings.objectproperties;

import javafx.beans.property.SimpleObjectProperty;
import javafx.util.Duration;

/**
 * Wrapper für XML-Adapter.
 */
public class DurationProperty extends SimpleObjectProperty<Duration> {

    public DurationProperty() {
    }

    public DurationProperty(Duration initialValue) {
        super(initialValue);
    }

    public DurationProperty(Object bean, String name) {
        super(bean, name);
    }

    public DurationProperty(Object bean, String name, Duration initialValue) {
        super(bean, name, initialValue);
    }

}
