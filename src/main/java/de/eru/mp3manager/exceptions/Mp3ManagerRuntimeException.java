package de.eru.mp3manager.exceptions;

/**
 *
 * @author Philipp Bruckner
 */
public class Mp3ManagerRuntimeException extends RuntimeException{

    public Mp3ManagerRuntimeException() {
    }

    public Mp3ManagerRuntimeException(String message) {
        super(message);
    }

    public Mp3ManagerRuntimeException(Throwable cause) {
        super(cause);
    }

    public Mp3ManagerRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
