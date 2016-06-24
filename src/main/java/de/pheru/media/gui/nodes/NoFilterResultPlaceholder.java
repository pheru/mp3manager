package de.pheru.media.gui.nodes;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

public class NoFilterResultPlaceholder extends VBox {

    private final StringProperty filter = new SimpleStringProperty("");

    public NoFilterResultPlaceholder() {
        setAlignment(Pos.CENTER);

        Label label = new Label();
        label.setWrapText(true);
        label.textProperty().bind(Bindings.concat("Keine Treffer für \"", filter, "\""));
        label.setTextAlignment(TextAlignment.CENTER);

        getChildren().add(label);
    }

    public String getFilter() {
        return filter.get();
    }

    public void setFilter(final String filter) {
        this.filter.set(filter);
    }

    public StringProperty filterProperty() {
        return filter;
    }
}
