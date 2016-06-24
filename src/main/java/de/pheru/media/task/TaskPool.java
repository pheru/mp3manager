package de.pheru.media.task;

import de.pheru.media.cdi.events.TaskExceptionEvent;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

/**
 * Klasse zum abarbeiten von Tasks.<br/>
 * Ein TaskPool enthält eine Liste von Tasks, welche nacheinander abgearbeitet
 * werden.<br/>
 * Wird ein neuer Task hinzugefügt, reiht sich dieser in die Liste von Tasks
 * ein. Ist die Liste leer, so wird der Task direkt gestartet.
 */
@ApplicationScoped
public class TaskPool {

    private static final Logger LOGGER = LogManager.getLogger(TaskPool.class);

    @Inject
    private Event<TaskExceptionEvent> taskExceptionEvent;

    private final ObservableList<PheruMediaTask> tasks = FXCollections.observableArrayList();
    private PheruMediaTask currentTask;

    private final StringProperty message = new SimpleStringProperty();
    private final StringProperty title = new SimpleStringProperty();
    private final DoubleProperty progress = new SimpleDoubleProperty();
    private final ObjectProperty<PheruMediaTask.PheruMediaTaskStatus> status = new SimpleObjectProperty<>(PheruMediaTask.PheruMediaTaskStatus.READY);

    private final BooleanProperty stopping = new SimpleBooleanProperty(false);

    /**
     * Fügt dem Taskpool einen Task hinzu und startet den TaskPool, falls dieser
     * nicht läuft.
     *
     * @param task Der hinzuzufügende Task.
     */
    public void addTask(PheruMediaTask task) {
        tasks.add(task);
        start();
    }

    /**
     * Fügt dem Taskpool einen Task hinzu und stellt diesen an die erste Stelle
     * der abzuarbeitenden Tasks.
     *
     * @param task Der hinzuzufügende Task.
     */
    public void addTaskAsNextTask(PheruMediaTask task) {
        tasks.add(0, task);
        start();
    }

    /**
     * Stoppt den TaskPool.
     *
     * @param cancelCurrentTask Ob der aktuelle Task sofort gestoppt werden oder
     *                          noch zu Ende laufen soll.
     */
    public void stop(boolean cancelCurrentTask) {
        stopping.set(true);
        if (cancelCurrentTask) {
            cancelCurrentTask();
        }
    }

    /**
     * Bricht den aktuellen Task ab.
     */
    public void cancelCurrentTask() {
        currentTask.cancel(false);
    }

    /**
     * Leert den TaskPool.
     *
     * @param cancelCurrentTask Ob der aktuelle Task sofort gestoppt werden oder
     *                          noch zu Ende laufen soll.
     */
    public void clear(boolean cancelCurrentTask) {
        tasks.clear();
        if (cancelCurrentTask) {
            cancelCurrentTask();
        }
    }

    /**
     * Startet den nächsten Task, sofern kein anderer zu diesem Zeitpunkt läuft.
     */
    public void start() {
        if (!status.get().equals(PheruMediaTask.PheruMediaTaskStatus.RUNNING) && !stopping.get() && !tasks.isEmpty()) {
            currentTask = tasks.get(0);
            tasks.remove(currentTask);
            currentTask.runningProperty().addListener(createTaskRunningListener());
            message.bind(currentTask.messageProperty());
            title.bind(currentTask.titleProperty());
            progress.bind(currentTask.progressProperty());
            status.bind(currentTask.statusProperty());
            currentTask.exceptionProperty().addListener((ObservableValue<? extends Throwable> observable, Throwable oldValue, Throwable newValue) -> {
                LOGGER.fatal("Unexpected Exception running Task!", newValue);
                status.unbind();
                status.set(PheruMediaTask.PheruMediaTaskStatus.FAILED);
                taskExceptionEvent.fire(new TaskExceptionEvent());
            });
            Thread thread = new Thread(currentTask, currentTask.getClass().getSimpleName());
            thread.setDaemon(true);
            thread.start();
        }
    }

    /**
     * Erzeugt einen ChangeListener, welcher dem RunningProperties eines Tasks
     * hinzugefügt wird und den nächsten Task startet, sobald dieser beendet
     * ist.
     *
     * @return Ein ChangeListener für das RunningProperty eines Tasks.
     */
    private ChangeListener<Boolean> createTaskRunningListener() {
        return (ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            if (!newValue) {
                if (!stopping.get()) {
                    start();
                } else {
                    stopping.set(false);
                }
            }
        };
    }

    public StringProperty messageProperty() {
        return message;
    }

    public StringProperty titleProperty() {
        return title;
    }

    public DoubleProperty progressProperty() {
        return progress;
    }

    public ObjectProperty<PheruMediaTask.PheruMediaTaskStatus> statusProperty() {
        return status;
    }

    public PheruMediaTask.PheruMediaTaskStatus getStatus() {
        return status.get();
    }

    public String getMessage() {
        return message.get();
    }

    public String getTitle() {
        return title.get();
    }

    public double getProgress() {
        return progress.get();
    }
}
