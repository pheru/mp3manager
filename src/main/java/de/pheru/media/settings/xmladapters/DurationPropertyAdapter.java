package de.pheru.media.settings.xmladapters;

import de.pheru.media.settings.objectproperties.DurationProperty;
import javafx.util.Duration;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * @author Philipp Bruckner
 */
public class DurationPropertyAdapter extends XmlAdapter<String, DurationProperty> {

    @Override
    public DurationProperty unmarshal(String v) throws Exception {
        return new DurationProperty(Duration.valueOf(v));
    }

    @Override
    public String marshal(DurationProperty v) throws Exception {
        return String.valueOf(v.get());
    }
}
