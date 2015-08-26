package de.pheru.media.settings.xmladapters;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 *
 * @author Philipp Bruckner
 */
public class StringPropertyAdapter extends XmlAdapter<String, StringProperty>{

    @Override
    public StringProperty unmarshal(String v) throws Exception {
        return new SimpleStringProperty(v);
    }

    @Override
    public String marshal(StringProperty v) throws Exception {
        return v.get();
    }

}
