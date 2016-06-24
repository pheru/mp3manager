package de.pheru.media.gui.nodes;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

public class EmptyDirectoryPlaceholder extends VBox {

    private final Button button = new Button("Verzeichnis wechseln");

    public EmptyDirectoryPlaceholder() {
        setAlignment(Pos.CENTER);

        Label label = new Label("Das gewählte Verzeichnis enthält keine MP3-Dateien");
        label.setTextAlignment(TextAlignment.CENTER);

        getChildren().addAll(label, button);
    }

    public void setOnAction(EventHandler<ActionEvent> eventHandler) {
        button.setOnAction(eventHandler);
    }

    public EventHandler<ActionEvent> getOnAction() {
        return button.getOnAction();
    }

    public Button getButton() {
        return button;
    }

}
