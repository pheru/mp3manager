package de.eru.mp3manager.gui.utils;

import javafx.stage.Screen;

/**
 *
 * @author Philipp Bruckner
 */
public class Scale {
    
    private final double value = Screen.getPrimary().getDpi() / 96;

    public double getValue() {
        return value;
    }
}
