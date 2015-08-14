package de.eru.mp3manager.service;

/**
 *
 * @author Philipp Bruckner
 */
public class RenameFailedException extends Exception{

    public RenameFailedException() {
    }

    public RenameFailedException(String message) {
        super(message);
    }
}
