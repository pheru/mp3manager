package de.pheru.media.desktop;

import de.pheru.fx.mvp.UpdateableSplashStage;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SplashStage extends Stage implements UpdateableSplashStage {

    private Label messageLabel;
    private ProgressBar progressBar;

    public SplashStage() {
        messageLabel = new Label("Starte Anwendung");
        progressBar = new ProgressBar();
        final VBox content = new VBox(messageLabel, progressBar);
        setScene(new Scene(content));
    }

    @Override
    public void loadingMessageUpdated(final String s) {
        messageLabel.setText(s);
    }

    @Override
    public void loadingProgressUpdated(final double workDone, final double max) {
        progressBar.setProgress(workDone / max);
    }

    @Override
    public void loadingFailed(final String s, final Throwable throwable) {
        // TODO SplashStage loadingFailed implementieren
    }
}
