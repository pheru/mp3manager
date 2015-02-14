package de.eru.mp3manager.utils;

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
import javafx.concurrent.Task;
import javax.enterprise.context.ApplicationScoped;

/**
 * Klasse zum abarbeiten von Tasks.<br/>
 * Ein TaskPool enthält eine Liste von Tasks, welche nacheinander abgearbeitet
 * werden.<br/>
 * Wird ein neuer Task hinzugefügt, reiht sich dieser in die Liste von Tasks
 * ein. Ist die Liste leer, so wird der Task direkt gestartet.
 *
 * @author Philipp Bruckner
 */
@ApplicationScoped
public class TaskPool {

    private final ObservableList<Task> tasks = FXCollections.observableArrayList();
    private Task currentTask;

    private final StringProperty message = new SimpleStringProperty();
    private final StringProperty title = new SimpleStringProperty();
    private final DoubleProperty progress = new SimpleDoubleProperty();

    private final BooleanProperty running = new SimpleBooleanProperty(false);
    private final BooleanProperty cancelling = new SimpleBooleanProperty(false);
    private final BooleanProperty stopping = new SimpleBooleanProperty(false);

    private final ObjectProperty<TaskStatus> status = new SimpleObjectProperty<>(TaskStatus.READY);

    /**
     * Fügt dem Taskpool einen Task hinzu und startet den TaskPool, falls dieser
     * nicht läuft.
     *
     * @param task Der hinzuzufügende Task.
     */
    public void addTask(Task task) {
        tasks.add(task);
        start();
    }

    /**
     * Fügt dem Taskpool einen Task hinzu und stellt diesen an die erste Stelle
     * der abzuarbeitenden Tasks.
     *
     * @param task Der hinzuzufügende Task.
     */
    public void addTaskAsNextTask(Task task) {
        tasks.add(0, task);
        start();
    }

    /**
     * Stoppt den TaskPool.
     *
     * @param cancelCurrentTask Ob der aktuelle Task sofort gestoppt werden oder
     * noch zu Ende laufen soll.
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
        cancelling.set(true);
        currentTask.cancel(false);
    }

    /**
     * Leert den TaskPool.
     *
     * @param cancelCurrentTask Ob der aktuelle Task sofort gestoppt werden oder
     * noch zu Ende laufen soll.
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
        if (!running.get() && !stopping.get() && tasks.size() > 0) {
            running.set(true);
            status.set(TaskStatus.RUNNING);
            currentTask = tasks.get(0);
            tasks.remove(currentTask);
            currentTask.runningProperty().addListener(createTaskRunningListener());
            message.bind(currentTask.messageProperty());
            title.bind(currentTask.titleProperty());
            progress.bind(currentTask.progressProperty());
            currentTask.exceptionProperty().addListener((ObservableValue observable, Object oldValue, Object newValue) -> {
                ExceptionHandler.handle((Throwable) newValue);
            });
            Thread thread = new Thread(currentTask);
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
                if (!cancelling.get()) {
                    status.set(TaskStatus.SUCCESSFUL);
                } else {
                    status.set(TaskStatus.CANCELLED);
                    cancelling.set(false);
                }
                running.set(false);
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

    public BooleanProperty runningProperty() {
        return running;
    }

    public BooleanProperty cancellingProperty() {
        return cancelling;
    }

    public ObjectProperty<TaskStatus> statusProperty() {
        return status;
    }
}
