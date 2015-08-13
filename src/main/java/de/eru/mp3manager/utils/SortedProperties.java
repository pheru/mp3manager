package de.eru.mp3manager.utils;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;
import java.util.TreeSet;

/**
 *
 * @author Philipp Bruckner
 */
public class SortedProperties extends Properties { //TODO Benötigt?

    @Override
    public synchronized Enumeration<Object> keys() {
        return Collections.enumeration(new TreeSet<Object>(super.keySet()));
    }

}
