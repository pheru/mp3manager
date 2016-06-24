package de.pheru.media.task;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;

public abstract class PheruMediaTask extends Task<Void> {

    private final ObjectProperty<PheruMediaTaskStatus> status = new SimpleObjectProperty<>(PheruMediaTaskStatus.READY);
    private EventHandler<PheruMediaTaskEvent> onFinishedHandler;

    protected abstract void callImpl();

    @Override
    protected Void call() throws Exception {
        setStatus(PheruMediaTaskStatus.RUNNING);
        callImpl();
        if (onFinishedHandler != null) {
            PheruMediaTaskEvent event = new PheruMediaTaskEvent();
            event.setStatus(getStatus());
            onFinishedHandler.handle(event);
        }
        return null;
    }

    public PheruMediaTaskStatus getStatus() {
        return status.get();
    }

    public void setStatus(final PheruMediaTaskStatus status) {
        if (Platform.isFxApplicationThread()) {
            this.status.set(status);
        } else {
            Platform.runLater(() -> this.status.set(status));
        }
    }

    public ObjectProperty<PheruMediaTaskStatus> statusProperty() {
        return status;
    }

    public void setOnFinished(EventHandler<PheruMediaTaskEvent> onFinishedHandler) {
        this.onFinishedHandler = onFinishedHandler;
    }

    public static class PheruMediaTaskEvent extends Event {

        public static final EventType<PheruMediaTaskEvent> TYPE = new EventType<>("PheruMediaTaskEvent");

        private PheruMediaTaskStatus status;

        public PheruMediaTaskEvent() {
            super(TYPE);
        }

        public PheruMediaTaskStatus getStatus() {
            return status;
        }

        public void setStatus(PheruMediaTaskStatus status) {
            this.status = status;
        }
    }

    public enum PheruMediaTaskStatus {

        READY("dodgerblue"),
        RUNNING("dodgerblue"),
        SUCCESSFUL("limegreen"),
        INSUFFICIENT("darkorange"),
        FAILED("red");

        private final String color;

        PheruMediaTaskStatus(String color) {
            this.color = color;
        }

        public String getColor() {
            return color;
        }
    }

}
