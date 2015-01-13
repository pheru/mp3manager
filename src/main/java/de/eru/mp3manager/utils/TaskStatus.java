package de.eru.mp3manager.utils;

/**
 * @author Philipp Bruckner
 */
public enum TaskStatus {

    READY("dodgerblue"),
    RUNNING("dodgerblue"),
    CANCELLED("red"),
    SUCCESSFUL("limegreen");

    private final String color;

    private TaskStatus(String color) {
        this.color = color;
    }

    public String getColor() {
        return color;
    }
}
