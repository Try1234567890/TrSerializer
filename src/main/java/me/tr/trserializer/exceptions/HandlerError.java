package me.tr.trserializer.exceptions;

public class HandlerError extends RuntimeException {

    public HandlerError(String message) {
        super(message);
    }

    public HandlerError(String message, Throwable cause) {
        super(message, cause);
    }
}
