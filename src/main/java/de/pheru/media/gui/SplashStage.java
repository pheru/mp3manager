package de.pheru.media.gui;

import de.pheru.fx.mvp.UpdateableSplashStage;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SplashStage extends Stage implements UpdateableSplashStage {

    private final Label label;
    private final ProgressBar progressBar;
    private final Button cancelExitButton;

    public SplashStage() {
        label = new Label("Starte Anwendung...");
        progressBar = new ProgressBar(-1);
        cancelExitButton = new Button("Abbrechen");
        cancelExitButton.setOnAction(event -> Platform.exit());

        final Scene scene = new Scene(new VBox(label, progressBar, cancelExitButton));
        setScene(scene);
    }

    @Override
    public void loadingMessageUpdated(String s) {
        label.setText(s);
    }

    @Override
    public void loadingProgressUpdated(double workDone, double max) {
        progressBar.setProgress(workDone / max);
    }

    @Override
    public void loadingFailed(String s, Throwable throwable) {
        label.setText(s);
        cancelExitButton.setText("Beenden");
    }
}
