package me.tr.trserializer.exceptions;

public class TranslationError extends ProcessError {

    public TranslationError(String message) {
        super(message);
    }

    public TranslationError(String message, Throwable cause) {
        super(message, cause);
    }
}
