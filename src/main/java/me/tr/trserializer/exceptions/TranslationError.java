package me.tr.trserializer.exceptions;

/**
 * Thrown when an error occurs while a translator is processing.
 */
public class TranslationError extends ProcessError {

    public TranslationError(String message) {
        super(message);
    }

    public TranslationError(String message, Throwable cause) {
        super(message, cause);
    }
}
