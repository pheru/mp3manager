package de.pheru.media.desktop;

import de.pheru.fx.mvp.UpdateableSplashStage;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class SplashStage extends Stage implements UpdateableSplashStage {

    private final Label messageLabel;
    private final ProgressBar progressBar;

    public SplashStage() {
        messageLabel = new Label("Starte Anwendung ...");

        progressBar = new ProgressBar();
        progressBar.setMaxWidth(Double.MAX_VALUE);

        final VBox content = new VBox(messageLabel, progressBar);
        content.setAlignment(Pos.CENTER);
        content.setMinWidth(300);
        content.getStyleClass().add("dialog-shadow");

        initStyle(StageStyle.TRANSPARENT);
        setScene(new Scene(content, Color.TRANSPARENT));
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
