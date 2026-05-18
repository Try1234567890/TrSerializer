package me.tr.trserializer.exceptions;

/**
 * Thrown when an error occurs while a process is processing.
 */
public class ProcessError extends RuntimeException {

    public ProcessError(String message) {
        super(message);
    }

    public ProcessError(String message, Throwable cause) {
        super(message, cause);
    }
}
