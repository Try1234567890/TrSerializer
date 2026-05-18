package me.tr.trserializer.exceptions;

/**
 * Thrown when an error occurs while an instancer is processing.
 */
public class InstancerError extends ProcessError {

    public InstancerError(String message) {
        super(message);
    }

    public InstancerError(String message, Throwable cause) {
        super(message, cause);
    }
}
