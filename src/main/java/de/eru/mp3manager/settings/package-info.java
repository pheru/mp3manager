@XmlJavaTypeAdapters({
    @XmlJavaTypeAdapter(value = StringPropertyAdapter.class, type = StringProperty.class),
    @XmlJavaTypeAdapter(value = BooleanPropertyAdapter.class, type = BooleanProperty.class),
    @XmlJavaTypeAdapter(value = IntegerPropertyAdapter.class, type = IntegerProperty.class),
    @XmlJavaTypeAdapter(value = NotificationsAlignmentPropertyAdapter.class, type = NotificationsAlignmentProperty.class),
    @XmlJavaTypeAdapter(value = DoublePropertyAdapter.class, type = DoubleProperty.class)})
package de.eru.mp3manager.settings;

import de.eru.mp3manager.settings.objectproperties.NotificationsAlignmentProperty;
import de.eru.mp3manager.settings.xmladapter.StringPropertyAdapter;
import de.eru.mp3manager.settings.xmladapter.DoublePropertyAdapter;
import de.eru.mp3manager.settings.xmladapter.BooleanPropertyAdapter;
import de.eru.mp3manager.settings.xmladapter.IntegerPropertyAdapter;
import de.eru.mp3manager.settings.xmladapter.NotificationsAlignmentPropertyAdapter;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters;
