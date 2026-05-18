package me.tr.trserializer.exceptions;

/**
 * Thrown when an error occurs while a deserializer is processing.
 */
public class DeserializationError extends ProcessError {

    public DeserializationError(String message) {
        super(message);
    }

    public DeserializationError(String message, Throwable cause) {
        super(message, cause);
    }
}
