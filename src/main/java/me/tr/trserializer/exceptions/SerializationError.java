package me.tr.trserializer.exceptions;

/**
 * Thrown when an error occurs while a serializer is processing.
 */
public class SerializationError extends TranslationError {

    public SerializationError(String message) {
        super(message);
    }

    public SerializationError(String message, Throwable cause) {
        super(message, cause);
    }
}
