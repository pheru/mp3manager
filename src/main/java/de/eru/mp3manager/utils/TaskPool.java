package de.eru.mp3manager.utils;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
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
 * Ein TaskPool enth�lt eine Liste von Tasks, welche nacheinander abgearbeitet werden.<br/>
 * Wird ein neuer Task hinzugef�gt, reiht sich dieser in die Liste von Tasks ein. Ist die Liste leer, so wird der Task direkt gestartet.
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

    private boolean running = false;
    private boolean stopping = false;

    /**
     * F�gt dem Taskpool einen Task hinzu und startet den TaskPool, falls dieser nicht l�uft.
     *
     * @param task Der hinzuzuf�gende Task.
     */
    public void addTask(Task task) {
        tasks.add(task);
        start();
    }

    /**
     * F�gt dem Taskpool einen Task hinzu und stellt diesen an die erste Stelle der abzuarbeitenden Tasks.
     *
     * @param task Der hinzuzuf�gende Task.
     */
    public void addTaskAsNextTask(Task task) {
        tasks.add(0, task);
        start();
    }

    /**
     * Stoppt den TaskPool.
     *
     * @param cancelCurrentTask Ob der aktuelle Task sofort gestoppt werden oder noch zu Ende laufen soll.
     */
    public void stop(boolean cancelCurrentTask) {
        stopping = true;
        if (cancelCurrentTask) {
            currentTask.cancel();
        }
    }

    /**
     * Leert den TaskPool.
     *
     * @param cancelCurrentTask Ob der aktuelle Task sofort gestoppt werden oder noch zu Ende laufen soll.
     */
    public void clear(boolean cancelCurrentTask) {
        tasks.clear();
        if (cancelCurrentTask) {
            currentTask.cancel();
        }
    }

    /**
     * Startet den n�chsten Task, sofern kein anderer zu diesem Zeitpunkt l�uft.
     */
    public void start() {
        if (!running && !stopping && tasks.size() > 0) {
            running = true;
            currentTask = tasks.get(0);
            tasks.remove(currentTask);
            currentTask.runningProperty().addListener(createTaskRunningListener());
            message.bind(currentTask.messageProperty());
            title.bind(currentTask.titleProperty());
            progress.bind(currentTask.progressProperty());
            Thread thread = new Thread(currentTask);
            thread.setDaemon(true);
            thread.start();
        }
    }

    /**
     * Erzeugt einen ChangeListener, welcher dem RunningProperties eines Tasks hinzugef�gt wird und den n�chsten Task startet, sobald dieser beendet ist.
     *
     * @return Ein ChangeListener f�r das RunningProperty eines Tasks.
     */
    private ChangeListener<Boolean> createTaskRunningListener() {
        return (ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            if (!newValue) {
                running = false;
                if (!stopping) {
                    start();
                } else {
                    stopping = false;
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

}
