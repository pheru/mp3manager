package de.pheru.media.core.data.loader;

public class UnsupportedAudioFileFormatException extends RuntimeException {

    public UnsupportedAudioFileFormatException() {
    }

    public UnsupportedAudioFileFormatException(final String message) {
        super(message);
    }

    public UnsupportedAudioFileFormatException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public UnsupportedAudioFileFormatException(final Throwable cause) {
        super(cause);
    }

    public UnsupportedAudioFileFormatException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
