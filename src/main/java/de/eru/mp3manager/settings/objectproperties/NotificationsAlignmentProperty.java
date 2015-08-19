package de.eru.mp3manager.settings.objectproperties;

import de.eru.pherufx.notifications.Notifications;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Wrapper für XML-Adapter.
 * @author Philipp Bruckner
 */
public class NotificationsAlignmentProperty extends SimpleObjectProperty<Notifications.Alignment>{

    public NotificationsAlignmentProperty() {
    }

    public NotificationsAlignmentProperty(Notifications.Alignment initialValue) {
        super(initialValue);
    }

    public NotificationsAlignmentProperty(Object bean, String name) {
        super(bean, name);
    }

    public NotificationsAlignmentProperty(Object bean, String name, Notifications.Alignment initialValue) {
        super(bean, name, initialValue);
    }
    
}
