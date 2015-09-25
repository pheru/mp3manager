package de.pheru.media.exceptions;

/**
 *
 * @author Philipp Bruckner
 */
public class SaveFailedException extends Exception {

    public SaveFailedException() {
    }

    public SaveFailedException(String message) {
        super(message);
    }

    public SaveFailedException(Throwable cause) {
        super(cause);
    }

    public SaveFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
