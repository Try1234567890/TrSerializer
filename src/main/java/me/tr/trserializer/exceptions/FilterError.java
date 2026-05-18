package me.tr.trserializer.exceptions;

/**
 * Thrown when an error occurs while a filter is processing.
 */
public class FilterError extends RuntimeException {

    public FilterError(String message) {
        super(message);
    }

    public FilterError(String message, Throwable cause) {
        super(message, cause);
    }
}
