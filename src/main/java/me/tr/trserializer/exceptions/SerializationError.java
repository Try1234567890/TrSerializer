package me.tr.trserializer.exceptions;

public class SerializationError extends ProcessError {

    public SerializationError(String message) {
        super(message);
    }

    public SerializationError(String message, Throwable cause) {
        super(message, cause);
    }
}
