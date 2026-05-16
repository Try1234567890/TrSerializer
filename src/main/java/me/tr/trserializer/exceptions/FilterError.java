package me.tr.trserializer.exceptions;

public class FilterError extends RuntimeException {

    public FilterError(String message) {
        super(message);
    }

    public FilterError(String message, Throwable cause) {
        super(message, cause);
    }
}
