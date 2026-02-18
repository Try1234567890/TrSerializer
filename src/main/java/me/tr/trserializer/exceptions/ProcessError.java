package me.tr.trserializer.exceptions;

public class ProcessError extends RuntimeException {

    public ProcessError(String message) {
        super(message);
    }

    public ProcessError(String message, Throwable cause) {
        super(message, cause);
    }
}
