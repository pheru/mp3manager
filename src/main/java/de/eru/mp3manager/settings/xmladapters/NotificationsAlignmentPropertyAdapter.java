package de.eru.mp3manager.settings.xmladapters;

import de.eru.mp3manager.settings.objectproperties.NotificationsAlignmentProperty;
import de.eru.pherufx.notifications.Notifications;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 *
 * @author Philipp Bruckner
 */
public class NotificationsAlignmentPropertyAdapter extends XmlAdapter<String, NotificationsAlignmentProperty>{

    @Override
    public NotificationsAlignmentProperty unmarshal(String v) throws Exception {
        return new NotificationsAlignmentProperty(Notifications.Alignment.valueOf(v));
    }

    @Override
    public String marshal(NotificationsAlignmentProperty v) throws Exception {
        return String.valueOf(v.get());
    }

}
