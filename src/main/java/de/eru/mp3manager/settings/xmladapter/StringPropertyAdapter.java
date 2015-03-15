/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.eru.mp3manager.settings.xmladapter;

import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;
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
