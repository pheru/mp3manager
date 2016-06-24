package de.pheru.media.settings;

import de.pheru.media.gui.applicationwindow.main.MainTableColumn;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;

import javax.xml.bind.annotation.XmlElement;

public class MainTableColumnSettings {

    @XmlElement //nötig, da kein public setter vorhanden ist
    private MainTableColumn column;
    private final DoubleProperty width = new SimpleDoubleProperty(0.0);
    private final BooleanProperty visible = new SimpleBooleanProperty(false);

    public MainTableColumnSettings(MainTableColumn column) {
        this.column = column;
        this.width.set(column.getDefaultWidth());
        this.visible.set(column.isDefaultVisible());
    }

    public MainTableColumnSettings(MainTableColumn column, double width, boolean visible) {
        this.column = column;
        this.width.set(width);
        this.visible.set(visible);
    }

    public MainTableColumnSettings() {
    }

    public MainTableColumn getColumn() {
        return column;
    }

    public double getWidth() {
        return width.get();
    }

    public void setWidth(double width) {
        this.width.set(width);
    }

    public DoubleProperty widthProperty() {
        return width;
    }

    public boolean isVisible() {
        return visible.get();
    }

    public void setVisible(boolean visible) {
        this.visible.set(visible);
    }

    public BooleanProperty visibleProperty() {
        return visible;
    }

}
