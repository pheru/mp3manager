package de.pheru.media.settings.objectproperties;

import de.pheru.fx.controls.notification.NotificationManager;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Wrapper für XML-Adapter.
 * @author Philipp Bruckner
 */
public class NotificationsAlignmentProperty extends SimpleObjectProperty<NotificationManager.Alignment>{

    public NotificationsAlignmentProperty() {
    }

    public NotificationsAlignmentProperty(NotificationManager.Alignment initialValue) {
        super(initialValue);
    }

    public NotificationsAlignmentProperty(Object bean, String name) {
        super(bean, name);
    }

    public NotificationsAlignmentProperty(Object bean, String name, NotificationManager.Alignment initialValue) {
        super(bean, name, initialValue);
    }
    
}
