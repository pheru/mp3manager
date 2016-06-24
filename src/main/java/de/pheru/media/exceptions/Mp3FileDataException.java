package de.pheru.media.exceptions;

public class Mp3FileDataException extends Exception {

    public Mp3FileDataException() {
    }

    public Mp3FileDataException(String message) {
        super(message);
    }

    public Mp3FileDataException(Throwable cause) {
        super(cause);
    }

    public Mp3FileDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
