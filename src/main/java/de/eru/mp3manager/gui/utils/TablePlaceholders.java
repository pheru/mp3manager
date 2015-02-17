package de.eru.mp3manager.gui.utils;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

/**
 *
 * @author Philipp Bruckner
 */
public class TablePlaceholders {

    public static final VBox READING_DIRECTORY = createReadingDirectory();
    public static final VBox EMPTY_DIRECTORY = createEmptyDirectory();
    public static final VBox NO_DIRECTORY = createNoDirectory();

    private static Button emptyDirectoryButton;
    private static Button noDirectoryButton;
    
    private TablePlaceholders() {
        //Utility-Klasse
    }

    private static VBox createReadingDirectory() {
        VBox box = new VBox();
        box.setAlignment(Pos.CENTER);

        Label label = new Label("Verzeichnis wird geladen.\nBitte warten...");
        label.setTextAlignment(TextAlignment.CENTER);

        ProgressIndicator progressIndicator = new ProgressIndicator(-1);
        progressIndicator.setScaleX(0.80);
        progressIndicator.setScaleY(0.80);

        box.getChildren().add(label);
        box.getChildren().add(progressIndicator);
        return box;
    }

    private static VBox createEmptyDirectory() {
        VBox box = new VBox();
        box.setAlignment(Pos.CENTER);

        Label label = new Label("Das gewählte Verzeichnis enthält keine MP3-Dateien!");
        label.setTextAlignment(TextAlignment.CENTER);

        emptyDirectoryButton = new Button();
        
        box.getChildren().add(label);
        box.getChildren().add(emptyDirectoryButton);
        return box;
    }

    private static VBox createNoDirectory() {
        VBox box = new VBox();
        box.setAlignment(Pos.CENTER);

        Label label = new Label("Es wurde kein Verzeichnis ausgewählt.");
        label.setTextAlignment(TextAlignment.CENTER);

        noDirectoryButton = new Button();
        
        box.getChildren().add(label);
        box.getChildren().add(noDirectoryButton);
        return box;
    }

    public static Button getEmptyDirectoryButton() {
        return emptyDirectoryButton;
    }

    public static Button getNoDirectoryButton() {
        return noDirectoryButton;
    }
}
