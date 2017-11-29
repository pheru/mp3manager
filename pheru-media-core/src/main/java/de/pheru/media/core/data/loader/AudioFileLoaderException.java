package de.pheru.media.core.data.loader;

public class AudioFileLoaderException extends Exception {

    public AudioFileLoaderException() {
    }

    public AudioFileLoaderException(final String message) {
        super(message);
    }

    public AudioFileLoaderException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public AudioFileLoaderException(final Throwable cause) {
        super(cause);
    }

    public AudioFileLoaderException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
