package de.pheru.media.exceptions;

public class RenameFailedException extends Exception {

    public RenameFailedException() {
    }

    public RenameFailedException(String message) {
        super(message);
    }

    public RenameFailedException(Throwable cause) {
        super(cause);
    }

    public RenameFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
