package de.pheru.media.settings.xmladapters;

import de.pheru.media.settings.objectproperties.PosProperty;
import javafx.geometry.Pos;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 *
 * @author Philipp Bruckner
 */
public class PosPropertyAdapter extends XmlAdapter<String, PosProperty> {

    @Override
    public PosProperty unmarshal(String v) throws Exception {
        return new PosProperty(Pos.valueOf(v));
    }

    @Override
    public String marshal(PosProperty v) throws Exception {
        return String.valueOf(v.get());
    }
}
