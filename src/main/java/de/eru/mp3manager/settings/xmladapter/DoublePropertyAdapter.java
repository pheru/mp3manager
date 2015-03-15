/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.eru.mp3manager.settings.xmladapter;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 *
 * @author Philipp Bruckner
 */
public class DoublePropertyAdapter extends XmlAdapter<String, DoubleProperty>{

    @Override
    public DoubleProperty unmarshal(String v) throws Exception {
        return new SimpleDoubleProperty(Double.valueOf(v));
    }

    @Override
    public String marshal(DoubleProperty v) throws Exception {
        return String.valueOf(v.get());
    }

}
