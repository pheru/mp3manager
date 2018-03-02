package de.pheru.media.desktop.util;

public class PrioritizedRunnable {

    private final Runnable runnable;
    private final Priority priority;

    public PrioritizedRunnable(final Runnable runnable, final Priority priority) {
        this.runnable = runnable;
        this.priority = priority;
    }

    public Runnable getRunnable() {
        return runnable;
    }

    public Priority getPriority() {
        return priority;
    }

    public enum Priority {
        HIGH(3),
        MEDIUM(2),
        LOW(1);

        private final int value;

        Priority(final int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}
