package de.eru.mp3manager.gui.utils;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

/**
 *
 * @author Philipp Bruckner
 */
public class TablePlaceholder extends VBox {

    private final StringProperty text = new SimpleStringProperty();
    private final BooleanProperty indicatorVisible = new SimpleBooleanProperty();

    public TablePlaceholder(String initialText, boolean initialIndicatorVisible) {
        setAlignment(Pos.CENTER);
        text.set(initialText);
        indicatorVisible.set(initialIndicatorVisible);

        Label label = new Label();
        label.setTextAlignment(TextAlignment.CENTER);
        label.textProperty().bind(text);

        ProgressIndicator progressIndicator = new ProgressIndicator(-1);
        progressIndicator.visibleProperty().bind(indicatorVisible);
        progressIndicator.setScaleX(0.80);
        progressIndicator.setScaleY(0.80);

        getChildren().add(label);
        getChildren().add(progressIndicator);
    }

    public TablePlaceholder() {
        this("", false);
    }

    public String getText() {
        return text.get();
    }

    public void setText(final String text) {
        this.text.set(text);
    }

    public StringProperty textProperty() {
        return text;
    }

    public Boolean isIndicatorVisible() {
        return indicatorVisible.get();
    }

    public void setIndicatorVisible(final Boolean indicatorVisible) {
        this.indicatorVisible.set(indicatorVisible);
    }

    public BooleanProperty indicatorVisibleProperty() {
        return indicatorVisible;
    }

}
