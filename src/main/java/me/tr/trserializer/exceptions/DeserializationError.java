package me.tr.trserializer.exceptions;

public class DeserializationError extends ProcessError {

    public DeserializationError(String message) {
        super(message);
    }

    public DeserializationError(String message, Throwable cause) {
        super(message, cause);
    }
}
