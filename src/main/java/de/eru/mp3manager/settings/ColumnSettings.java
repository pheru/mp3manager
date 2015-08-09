package de.eru.mp3manager.settings;

import de.eru.mp3manager.gui.applicationwindow.main.MainColumn;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javax.xml.bind.annotation.XmlElement;

/**
 *
 * @author Philipp Bruckner
 */
public class ColumnSettings {

    @XmlElement //nötig, da kein public setter vorhanden ist
    private MainColumn column;
    //XmlElement //TODO Adapter wären vorhanden
    //Darf nicht direkt annotiert werden, da die public getter/setter verwendet werden sollen (ansonsten wäre ein Adapter nötig)
    private final DoubleProperty width = new SimpleDoubleProperty(0.0);
    //XmlElement //TODO Adapter wären vorhanden
    //Darf nicht direkt annotiert werden, da die public getter/setter verwendet werden sollen (ansonsten wäre ein Adapter nötig)
    private final BooleanProperty visible = new SimpleBooleanProperty(false);

    public ColumnSettings(MainColumn column) {
        this.column = column;
        this.width.set(column.getDefaultWidth());
        this.visible.set(column.isDefaultVisible());
    }
    public ColumnSettings(MainColumn column, double width, boolean visible) {
        this.column = column;
        this.width.set(width);
        this.visible.set(visible);
    }
    
    public ColumnSettings() {
    }

    public MainColumn getColumn() {
        return column;
    }
    
    public double getWidth() {
        return width.get();
    }

    public void setWidth(double width) {
        this.width.set(width);
    }
    
    public DoubleProperty widthProperty(){
        return width;
    }

    public boolean isVisible() {
        return visible.get();
    }

    public void setVisible(boolean visible) {
        this.visible.set(visible);
    }
    
    public BooleanProperty visibleProperty(){
        return visible;
    }
    
}
