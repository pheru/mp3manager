/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.eru.mp3manager.settings.xmladapter;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 *
 * @author Philipp Bruckner
 */
public class BooleanPropertyAdapter extends XmlAdapter<String, BooleanProperty>{

    @Override
    public BooleanProperty unmarshal(String v) throws Exception {
        return new SimpleBooleanProperty(Boolean.valueOf(v));
    }

    @Override
    public String marshal(BooleanProperty v) throws Exception {
        return String.valueOf(v.get());
    }

}
