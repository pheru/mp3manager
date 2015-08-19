package de.eru.mp3manager.settings.xmladapter;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 *
 * @author Philipp Bruckner
 */
public class IntegerPropertyAdapter extends XmlAdapter<String, IntegerProperty>{

    @Override
    public IntegerProperty unmarshal(String v) throws Exception {
        return new SimpleIntegerProperty(Integer.valueOf(v));
    }

    @Override
    public String marshal(IntegerProperty v) throws Exception {
        return String.valueOf(v.get());
    }

}
