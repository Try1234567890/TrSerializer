package me.tr.trserializer.exceptions;

/**
 * Thrown when an error occurs while a handler is processing.
 */
public class HandlerError extends RuntimeException {

    public HandlerError(String message) {
        super(message);
    }

    public HandlerError(String message, Throwable cause) {
        super(message, cause);
    }
}
