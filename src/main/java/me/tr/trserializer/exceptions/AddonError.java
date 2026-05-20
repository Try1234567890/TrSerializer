package me.tr.trserializer.exceptions;

/**
 * Thrown when an error occurs while an addon is processing.
 */
public class AddonError extends RuntimeException {
    public AddonError(String message) {
        super(message);
    }

    public AddonError(String message, Throwable cause) {
        super(message, cause);
    }
}
