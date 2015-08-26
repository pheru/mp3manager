package de.pheru.media.gui.nodes;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

/**
 *
 * @author Philipp Bruckner
 */
public class ReadingDirectoryPlaceholder extends VBox{
    
    public ReadingDirectoryPlaceholder() {
        setAlignment(Pos.CENTER);

        Label label = new Label("Verzeichnis wird geladen.\nBitte warten...");
        label.setTextAlignment(TextAlignment.CENTER);

        ProgressIndicator progressIndicator = new ProgressIndicator(-1);
        progressIndicator.setScaleX(0.80);
        progressIndicator.setScaleY(0.80);

        getChildren().add(label);
        getChildren().add(progressIndicator);
    }
}
