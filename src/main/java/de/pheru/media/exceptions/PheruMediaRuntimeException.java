package de.pheru.media.exceptions;

/**
 *
 * @author Philipp Bruckner
 */
public class PheruMediaRuntimeException extends RuntimeException {

    public PheruMediaRuntimeException() {
    }

    public PheruMediaRuntimeException(String message) {
        super(message);
    }

    public PheruMediaRuntimeException(Throwable cause) {
        super(cause);
    }

    public PheruMediaRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
