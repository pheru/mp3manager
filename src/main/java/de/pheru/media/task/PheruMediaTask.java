package de.pheru.media.task;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;

/**
 *
 * @author Philipp Bruckner
 */
public abstract class PheruMediaTask extends Task<Void> {

    private final ObjectProperty<Status> status = new SimpleObjectProperty<>(Status.READY);

    protected abstract void innerCall();

    @Override
    protected Void call() throws Exception {
        setStatus(Status.RUNNING);
        innerCall();
        return null;
    }

    public Status getStatus() {
        return status.get();
    }

    public void setStatus(final Status status) {
        if (Platform.isFxApplicationThread()) {
            this.status.set(status);
        } else {
            Platform.runLater(() -> {
                PheruMediaTask.this.status.set(status);
            });
        }
    }

    public ObjectProperty<Status> statusProperty() {
        return status;
    }

    public enum Status {

        READY("dodgerblue"),
        RUNNING("dodgerblue"),
        SUCCESSFUL("limegreen"),
        INSUFFICIENT("darkorange"),
        FAILED("red");

        private final String color;

        private Status(String color) {
            this.color = color;
        }

        public String getColor() {
            return color;
        }
    }

}
